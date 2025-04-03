<!-- src/views/CustomMap.vue -->
<template>
  <div class="map-container">
    <div class="left-panel">
      <div class="tool-bar">
        <el-button type="primary" @click="handleAdd">添加地图数据</el-button>
      </div>
      <el-table :data="mapList" height="calc(100% - 50px)" border>
        <el-table-column prop="name" label="地图名称" width="180" />
        <el-table-column prop="catalogId" label="目录ID" width="100" />
        <el-table-column prop="mapType" label="地图类型" width="100" />
        <el-table-column prop="visible" label="是否可见" width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.visible" @change="handleVisibleChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handlePreview(scope.row)">预览</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="right-panel">
      <div id="customMap" class="map-container"></div>
    </div>

    <!-- 左侧抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        title="添加地图数据"
        direction="ltr"
        size="500px"
        :before-close="handleDrawerClose"
    >
      <el-form :model="formData" label-width="120px">
        <el-form-item label="地图名称" required>
          <el-input v-model="formData.name"></el-input>
        </el-form-item>
        <el-form-item label="关联目录ID" required>
          <el-select
              v-model="formData.catalogId"
              placeholder="请选择关联目录"
              filterable
              style="width: 100%"
          >
            <el-option
                v-for="item in flatCatalogList"
                :key="item.id"
                :label="item.label"
                :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地图类型" required>
          <el-select v-model="formData.mapType" style="width: 100%">
            <el-option label="GeoJSON" value="geojson" />
            <el-option label="KML" value="kml" />
          </el-select>
        </el-form-item>
        <el-form-item label="地图文件">
          <el-upload
              action="/water-map-api/api/map/upload"
              :limit="1"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :auto-upload="true"
              :show-file-list="true"
              accept=".geojson,.json,.kml,.kmz"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持GeoJSON、KML格式文件</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="地图数据">
          <el-input
              v-model="formData.mapData"
              type="textarea"
              :rows="10"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="text-align: right; margin-right: 20px;">
          <el-button @click="handleDrawerClose">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import { toGeoJSON } from '@mapbox/togeojson'
import request from "../utils/request.js";

// 数据
const mapList = ref([])
const drawerVisible = ref(false)
const formData = ref({
  id: null,
  name: '',
  catalogId: '',
  mapType: 'geojson',
  mapData: '',
  visible: true
})

// 目录树相关
const catalogTreeData = ref([])
// 将树形结构扁平化处理，方便下拉选择
const flatCatalogList = computed(() => {
  const result = []
  const flatten = (items, prefix = '') => {
    if (!items) return
    items.forEach(item => {
      result.push({
        id: item.id,
        label: prefix + item.label
      })
      if (item.children && item.children.length) {
        flatten(item.children, prefix + '└─ ')
      }
    })
  }
  flatten(catalogTreeData.value)
  return result
})

// 加载目录树
const loadCatalogTree = async () => {
  try {
    const res = await request.get('/api/catalog/tree')
    catalogTreeData.value = res.data || []
  } catch (error) {
    ElMessage.error('获取目录数据失败')
    console.error('获取目录数据失败:', error)
  }
}

// 地图相关
let map = null
let mapLayers = {}

// 初始化地图
const initMap = () => {
  map = L.map('customMap', {
    center: [30.5, 114.3], // 湖北省中心点坐标
    zoom: 7
  })

  // 添加底图
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  // 加载现有地图数据
  loadMaps()
}

// 加载地图数据
const loadMaps = async () => {
  try {
    const response = await request.get('/api/map/list')
    mapList.value = response.data || []

    // 清空现有图层
    Object.keys(mapLayers).forEach(id => {
      map.removeLayer(mapLayers[id])
      delete mapLayers[id]
    })

    // 显示可见的地图数据
    mapList.value.forEach(item => {
      if (item.visible) {
        addMapLayer(item)
      }
    })
  } catch (error) {
    ElMessage.error('获取地图数据失败')
    console.error('获取地图数据失败:', error)
  }
}

// 添加地图图层
const addMapLayer = (mapData) => {
  try {
    const geojson = JSON.parse(mapData.mapData)
    const layer = L.geoJSON(geojson, {
      style: {
        color: '#3388ff',
        weight: 2,
        opacity: 0.7,
        fillOpacity: 0.4
      },
      pointToLayer: (feature, latlng) => {
        return L.circleMarker(latlng, {
          radius: 8,
          fillColor: "#ff7800",
          color: "#000",
          weight: 1,
          opacity: 1,
          fillOpacity: 0.8
        })
      },
      onEachFeature: (feature, layer) => {
        if (feature.properties) {
          let popupContent = '<div class="popup-content">'
          Object.keys(feature.properties).forEach(key => {
            popupContent += `<div><strong>${key}</strong>: ${feature.properties[key]}</div>`
          })
          popupContent += '</div>'
          layer.bindPopup(popupContent)
        }
      }
    }).addTo(map)

    mapLayers[mapData.id] = layer

    // 缩放到该图层
    map.fitBounds(layer.getBounds())
  } catch (e) {
    console.error('解析地图数据失败:', e)
  }
}

// 添加地图数据
const handleAdd = () => {
  formData.value = {
    id: null,
    name: '',
    catalogId: '',
    mapType: 'geojson',
    mapData: '',
    visible: true
  }

  drawerVisible.value = true
}

// 预览地图数据
const handlePreview = (row) => {
  // 清空其他图层
  Object.keys(mapLayers).forEach(id => {
    map.removeLayer(mapLayers[id])
    delete mapLayers[id]
  })

  // 添加当前图层
  addMapLayer(row)
}

// 删除地图数据
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该地图数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/map/${row.id}`)
      ElMessage.success('删除成功')

      // 移除图层
      if (mapLayers[row.id]) {
        map.removeLayer(mapLayers[row.id])
        delete mapLayers[row.id]
      }

      loadMaps()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 处理抽屉关闭
const handleDrawerClose = () => {
  // 显示确认对话框
  if (formData.value.name || formData.value.mapData) {
    ElMessageBox.confirm('是否放弃当前编辑？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      drawerVisible.value = false
    }).catch(() => {})
  } else {
    drawerVisible.value = false
  }
}

// 切换可见状态
const handleVisibleChange = async (row) => {
  try {
    await request.post('/api/map/save', row)

    if (row.visible) {
      // 添加图层
      addMapLayer(row)
    } else {
      // 移除图层
      if (mapLayers[row.id]) {
        map.removeLayer(mapLayers[row.id])
        delete mapLayers[row.id]
      }
    }
  } catch (error) {
    row.visible = !row.visible
    ElMessage.error('更新可见状态失败')
  }
}

// 文件上传成功
const handleUploadSuccess = (response) => {
  formData.value.mapData = response.data
  ElMessage.success('文件上传成功')
}

// 文件上传失败
const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

// 保存地图数据
const handleSave = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入地图名称')
    return
  }

  if (!formData.value.catalogId) {
    ElMessage.warning('请选择关联目录')
    return
  }

  if (!formData.value.mapData) {
    ElMessage.warning('请上传或输入地图数据')
    return
  }

  try {
    await request.post('/api/map/save', formData.value)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    loadMaps()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  nextTick(() => {
    initMap()
    loadCatalogTree()
  })
})
</script>

<style scoped>
.map-container {
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
</style>
