<!-- src/views/AreaDescription.vue -->
<template>
  <div class="area-container">
    <div class="left-panel">
      <div class="tool-bar">
        <el-button type="primary" @click="handleAdd">添加区域描述</el-button>
      </div>
      <el-table :data="areaList" height="calc(100% - 50px)" border>
        <el-table-column prop="name" label="区域名称" width="180" />
        <el-table-column label="目录名称" width="180">
          <template #default="scope">
            {{ getCatalogName(scope.row.catalogId) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述">
          <template #default="scope">
            <el-tooltip
                class="box-item"
                effect="dark"
                :content="scope.row.description"
                placement="top-start"
            >
              <div class="description-text">{{ scope.row.description }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="right-panel">
      <div id="areaMap" class="map-container"></div>
    </div>

    <!-- 左侧抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        :title="drawerType === 'add' ? '添加区域描述' : '编辑区域描述'"
        direction="ltr"
        size="500px"
        :before-close="handleDrawerClose"
        :destroy-on-close="false"
    >
      <div class="drawer-content">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="区域名称" required>
            <el-input v-model="formData.name"></el-input>
          </el-form-item>
          <el-form-item label="目录名称" required>
            <el-popover
                placement="bottom-start"
                :width="300"
                trigger="click"
                v-model:visible="catalogTreeVisible"
                popper-class="catalog-tree-popover"
            >
              <template #reference>
                <el-input
                    v-model="selectedCatalogName"
                    readonly
                    placeholder="点击选择目录"
                    @click="catalogTreeVisible = true"
                ></el-input>
              </template>
              <el-tree
                  :data="catalogTreeData"
                  node-key="id"
                  :props="{
                    label: 'label',
                    children: 'children'
                  }"
                  default-expand-all
                  @node-click="handleNodeClick"
              ></el-tree>
            </el-popover>
            <input type="hidden" v-model="formData.catalogId">
          </el-form-item>
          <el-form-item label="区域描述">
            <el-input
                v-model="formData.description"
                type="textarea"
                :rows="4"
            ></el-input>
          </el-form-item>
          <el-form-item label="区域范围" required>
            <el-input
                v-model="formData.areaBounds"
                readonly
                placeholder="在地图上绘制后自动填充"
            ></el-input>
            <div class="draw-tip">请在地图上使用绘制工具创建区域</div>
          </el-form-item>
        </el-form>
        <div class="drawer-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, watch} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

// 导入 leaflet-draw
import 'leaflet-draw'
import 'leaflet-draw/dist/leaflet.draw.css'
import '../utils/leaflet-draw-zh.js'

// 导入图标资源
import iconRetinaUrl from 'leaflet/dist/images/marker-icon-2x.png'
import iconUrl from 'leaflet/dist/images/marker-icon.png'
import shadowUrl from 'leaflet/dist/images/marker-shadow.png'
import request from "../utils/request.js";

// 地图相关
let map = null
let drawControl = null
let editableLayers = null
let currentDrawnShape = null // 用于存储当前绘制的图形

// 数据
const areaList = ref([])
const drawerVisible = ref(false)
const drawerType = ref('add')
const formData = ref({
  id: null,
  name: '',
  catalogId: '',
  description: '',
  areaBounds: ''
})
// 目录树相关
const catalogTreeVisible = ref(false)
const selectedCatalogName = ref('')

const handleNodeClick = (data) => {
  formData.value.catalogId = data.id
  selectedCatalogName.value = data.label
  catalogTreeVisible.value = false
}

// 添加目录数据
const catalogTreeData = ref([])
const loadCatalogTree = async () => {
  try {
    const res = await request.get('/api/catalog/tree')
    catalogTreeData.value = res.data || []
  } catch (error) {
    ElMessage.error('获取目录数据失败')
    console.error('获取目录数据失败:', error)
  }
}

// 获取目录名称
const getCatalogName = (catalogId) => {
  if (!catalogId) return '未选择'

  const catalog = findCatalogById(catalogTreeData.value, catalogId)
  return catalog ? catalog.label : catalogId
}

// 初始化地图
const initMap = () => {
  // 修复 Leaflet 默认图标问题
  delete L.Icon.Default.prototype._getIconUrl
  L.Icon.Default.mergeOptions({
    iconRetinaUrl,
    iconUrl,
    shadowUrl
  })

  map = L.map('areaMap', {
    center: [30.5, 114.3], // 湖北省中心点坐标
    zoom: 7
  })

  // 添加底图
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  // 初始化绘制图层
  editableLayers = new L.FeatureGroup()
  map.addLayer(editableLayers)

  // 初始化绘制控件，只保留多边形绘制工具
  drawControl = new L.Control.Draw({
    edit: {
      featureGroup: editableLayers
    },
    draw: {
      polygon: {
        allowIntersection: false,
        showArea: true
      },
      rectangle: false, // 移除矩形绘制
      circle: false,
      circlemarker: false,
      marker: false,
      polyline: false
    }
  })
  map.addControl(drawControl)

  // 监听绘制完成事件
  map.on(L.Draw.Event.CREATED, function(e) {
    // 清空当前图层
    editableLayers.clearLayers()

    const layer = e.layer
    editableLayers.addLayer(layer)
    currentDrawnShape = layer

    // 将绘制的图形转为GeoJSON并存储
    const geoJson = layer.toGeoJSON()
    const geoJsonString = JSON.stringify(geoJson.geometry)

    // 如果抽屉已经打开，则更新表单数据
    if (drawerVisible.value) {
      formData.value.areaBounds = geoJsonString
    } else {
      // 存储到临时绘制数据中
      localStorage.setItem('tempAreaBounds', geoJsonString)
    }
  })

  // 监听编辑完成事件
  map.on(L.Draw.Event.EDITED, function(e) {
    const layers = e.layers
    layers.eachLayer(function(layer) {
      const geoJson = layer.toGeoJSON()
      formData.value.areaBounds = JSON.stringify(geoJson.geometry)
    })
  })

  // 加载现有区域
  loadAreas()
}

// 加载区域数据
const loadAreas = async () => {
  try {
    const response = await request.get('/api/area/list')
    areaList.value = response.data || []

    // 清空现有图层
    if (editableLayers) {
      editableLayers.clearLayers()
    }

    // 显示区域范围
    areaList.value.forEach(area => {
      try {
        if (!area.areaBounds) return

        const geometry = JSON.parse(area.areaBounds)
        const layer = L.geoJSON(geometry, {
          style: {
            color: '#3388ff',
            weight: 2,
            opacity: 0.7,
            fillOpacity: 0.4
          }
        }).addTo(editableLayers)

        // 添加弹出框
        layer.bindPopup(`<div><strong>${area.name}</strong><p>${area.description || ''}</p></div>`)
      } catch (e) {
        console.error('解析区域数据失败:', e)
      }
    })
  } catch (error) {
    ElMessage.error('获取区域数据失败')
    console.error('获取区域数据失败:', error)
  }
}

// 添加区域
const handleAdd = () => {
  drawerType.value = 'add'
  formData.value = {
    id: null,
    name: '',
    catalogId: '',
    description: '',
    areaBounds: ''
  }
  selectedCatalogName.value = ''

  // 如果有临时存储的区域范围数据，则回填
  const tempAreaBounds = localStorage.getItem('tempAreaBounds')
  if (tempAreaBounds) {
    formData.value.areaBounds = tempAreaBounds
    localStorage.removeItem('tempAreaBounds')
  }

  drawerVisible.value = true
}

// 编辑区域
const handleEdit = (row) => {
  drawerType.value = 'edit'

  // 深拷贝确保不会直接修改原数据
  formData.value = JSON.parse(JSON.stringify(row))

  // 回填目录名称
  const catalogItem = findCatalogById(catalogTreeData.value, row.catalogId)
  if (catalogItem) {
    selectedCatalogName.value = catalogItem.label
  } else {
    selectedCatalogName.value = row.catalogId
  }

  // 清空绘制层
  editableLayers.clearLayers()

  // 显示当前编辑的区域
  try {
    if (row.areaBounds) {
      const geometry = JSON.parse(row.areaBounds)
      const layer = L.geoJSON(geometry, {
        style: {
          color: '#ff0000',
          weight: 2,
          opacity: 0.7,
          fillOpacity: 0.4
        }
      }).addTo(editableLayers)

      // 缩放到当前图形
      map.fitBounds(layer.getBounds())

      // 保存当前图层用于编辑
      currentDrawnShape = layer
    }
  } catch (e) {
    console.error('解析区域数据失败:', e)
  }

  drawerVisible.value = true
}

// 处理抽屉关闭
const handleDrawerClose = (done) => {
  // 显示确认对话框
  if (formData.value.name || formData.value.description || formData.value.areaBounds) {
    ElMessageBox.confirm('是否放弃当前编辑？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      done()
    }).catch(() => {})
  } else {
    done()
  }
}

// 根据ID查找目录项
const findCatalogById = (items, id) => {
  if (!items) return null

  for (const item of items) {
    if (item.id === id) {
      return item
    }
    if (item.children && item.children.length) {
      const found = findCatalogById(item.children, id)
      if (found) return found
    }
  }
  return null
}

// 监听抽屉关闭事件
watch(() => drawerVisible.value, (newVal) => {
  if (!newVal) {
    // 关闭抽屉时重新加载区域数据
    loadAreas()
  }
})

onMounted(() => {
  nextTick(() => {
    initMap()
    loadCatalogTree()
  })
})

// 删除区域
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该区域描述吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/area/${row.id}`)
      ElMessage.success('删除成功')
      loadAreas()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 保存区域
const handleSave = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入区域名称')
    return
  }

  if (!formData.value.catalogId) {
    ElMessage.warning('请选择关联目录')
    return
  }

  if (!formData.value.areaBounds) {
    ElMessage.warning('请绘制区域范围')
    return
  }

  try {
    await request.post('/api/area/save', formData.value)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    loadAreas()
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('保存失败:', error)
  }
}
</script>

<style scoped>
.area-container {
  display: flex;
  width: 100%;
  height: 100%;
}
.left-panel {
  width: 40%;
  height: 100%;
  border-right: 1px solid #ddd;
  padding: 10px;
  box-sizing: border-box;
}
.right-panel {
  width: 60%;
  height: 100%;
}
.map-container {
  width: 100%;
  height: 100%;
}
.tool-bar {
  margin-bottom: 10px;
}
.description-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 250px;
}
.draw-tip {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}
.drawer-content {
  padding: 20px;
  height: calc(100% - 60px);
}
.drawer-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px 20px;
  border-top: 1px solid #e4e7ed;
  background: #fff;
  text-align: right;
}
</style>
