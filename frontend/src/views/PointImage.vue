<!-- src/views/PointImage.vue -->
<template>
  <div class="point-container">
    <div class="left-panel">
      <div class="tool-bar">
        <el-button type="primary" @click="handleAdd">添加点位图片</el-button>
        <el-input
            v-model="searchKeyword"
            placeholder="搜索点位图片..."
            class="search-input"
            @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      <el-table :data="pointList" height="calc(100% - 50px)" border>
        <el-table-column prop="title" label="标题" width="180" />
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
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image
                :src="scope.row.imageUrl"
                :preview-src-list="[scope.row.imageUrl]"
                style="width: 50px; height: 50px"
                fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handlePreview(scope.row)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="right-panel">
      <div id="pointMap" class="map-container"></div>
    </div>

    <!-- 左侧抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        title="添加点位图片"
        direction="ltr"
        size="500px"
        :before-close="handleDrawerClose"
        :destroy-on-close="false"
    >
      <div class="drawer-content">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="标题" required>
            <el-input v-model="formData.title"></el-input>
          </el-form-item>
          <el-form-item label="描述">
            <el-input
                v-model="formData.description"
                type="textarea"
                :rows="4"
            ></el-input>
          </el-form-item>
          <el-form-item label="图片" required>
            <el-upload
                action="/water-map-api/api/pointImage/upload"
                :limit="1"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :auto-upload="true"
                :show-file-list="true"
                accept="image/*"
                list-type="picture-card"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="关联点位" required>
            <el-input
                v-model="formData.points"
                readonly
                placeholder="在地图上添加点位后自动填充"
                type="textarea"
                :rows="4"
            ></el-input>
            <div class="draw-tip">请在地图上使用标记工具添加点位</div>
          </el-form-item>
        </el-form>
        <div class="drawer-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="previewVisible" title="点位图片详情" width="600px">
      <div class="preview-container">
        <div class="preview-title">{{ currentPreview.title }}</div>
        <div class="preview-image">
          <el-image
              :src="currentPreview.imageUrl"
              :preview-src-list="[currentPreview.imageUrl]"
              style="width: 100%;"
              fit="contain"
          />
        </div>
        <div class="preview-description">{{ currentPreview.description }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet-draw/dist/leaflet.draw.css'
import 'leaflet-draw'
import '../utils/leaflet-draw-zh.js'
import request from "../utils/request.js";

// 数据
const pointList = ref([])
const drawerVisible = ref(false)
const previewVisible = ref(false)
const currentPreview = ref({})
const searchKeyword = ref('')
const formData = ref({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  points: ''
})

// 地图相关
let map = null
let drawControl = null
let editableLayers = null
let pointLayers = {}
let selectedPoints = [] // 用于存储当前选择的点位

// 初始化地图
const initMap = () => {
  map = L.map('pointMap', {
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

  // 初始化绘制控件，只保留标记工具
  drawControl = new L.Control.Draw({
    edit: {
      featureGroup: editableLayers
    },
    draw: {
      polygon: false,
      rectangle: false,
      circle: false,
      circlemarker: false,
      marker: true,
      polyline: false
    }
  })
  map.addControl(drawControl)

  // 监听绘制完成事件
  map.on(L.Draw.Event.CREATED, function(e) {
    const layer = e.layer
    editableLayers.addLayer(layer)

    // 更新选中的点位
    const latLng = layer.getLatLng()
    selectedPoints.push({
      lat: latLng.lat,
      lng: latLng.lng
    })

    // 将点位数据转为JSON
    const pointsJson = JSON.stringify(selectedPoints)

    // 如果抽屉已经打开，则更新表单数据
    if (drawerVisible.value) {
      formData.value.points = pointsJson
    } else {
      // 存储到临时绘制数据中
      localStorage.setItem('tempPoints', pointsJson)
    }
  })

  // 加载现有点位
  loadPoints()
}

// 加载点位数据
const loadPoints = async () => {
  try {
    const response = await request.get('/api/pointImage/list')
    pointList.value = response.data || []

    // 清空现有图层
    Object.keys(pointLayers).forEach(id => {
      map.removeLayer(pointLayers[id])
      delete pointLayers[id]
    })

    // 显示点位
    pointList.value.forEach(point => {
      addPointLayer(point)
    })
  } catch (error) {
    ElMessage.error('获取点位数据失败')
    console.error('获取点位数据失败:', error)
  }
}

// 添加点位图层
const addPointLayer = (pointData) => {
  try {
    if (!pointData.points) return

    const points = JSON.parse(pointData.points)
    const layers = new L.FeatureGroup()

    points.forEach(point => {
      const marker = L.marker([point.lat, point.lng])
          .bindPopup(`<div class="popup-content">
          <div class="popup-title">${pointData.title}</div>
          <div class="popup-image"><img src="${pointData.imageUrl}" style="max-width:200px;"></div>
          <div class="popup-description">${pointData.description || ''}</div>
        </div>`)

      marker.on('click', () => {
        // 点击时打开详情
        currentPreview.value = pointData
        previewVisible.value = true
      })

      layers.addLayer(marker)
    })

    layers.addTo(map)
    pointLayers[pointData.id] = layers
  } catch (e) {
    console.error('解析点位数据失败:', e)
  }
}

// 处理抽屉关闭
const handleDrawerClose = (done) => {
  // 显示确认对话框
  if (formData.value.title || formData.value.description || formData.value.imageUrl || formData.value.points) {
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

// 添加点位图片
const handleAdd = () => {
  formData.value = {
    id: null,
    title: '',
    description: '',
    imageUrl: '',
    points: ''
  }

  // 如果有临时存储的点位数据，则回填
  const tempPoints = localStorage.getItem('tempPoints')
  if (tempPoints) {
    formData.value.points = tempPoints
    selectedPoints = JSON.parse(tempPoints)
    localStorage.removeItem('tempPoints')
  } else {
    // 如果没有临时点位数据，则清空已选择的点位
    selectedPoints = []
  }

  drawerVisible.value = true
}

// 预览点位图片
const handlePreview = (row) => {
  currentPreview.value = row
  previewVisible.value = true

  // 在地图上高亮显示
  if (pointLayers[row.id]) {
    map.fitBounds(pointLayers[row.id].getBounds())
  }
}

// 删除点位图片
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该点位图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/pointImage/${row.id}`)
      ElMessage.success('删除成功')

      // 移除图层
      if (pointLayers[row.id]) {
        map.removeLayer(pointLayers[row.id])
        delete pointLayers[row.id]
      }

      loadPoints()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 文件上传成功
const handleUploadSuccess = (response) => {
  formData.value.imageUrl = response.data
  ElMessage.success('图片上传成功')
}

// 文件上传失败
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 搜索点位图片
const handleSearch = async () => {
  if (!searchKeyword.value) {
    loadPoints()
    return
  }

  try {
    const response = await request.get('/api/pointImage/search', {
      params: { keyword: searchKeyword.value }
    })
    pointList.value = response.data || []

    // 清空现有图层
    Object.keys(pointLayers).forEach(id => {
      map.removeLayer(pointLayers[id])
      delete pointLayers[id]
    })

    // 显示点位
    pointList.value.forEach(point => {
      addPointLayer(point)
    })
  } catch (error) {
    ElMessage.error('搜索失败')
    console.error('搜索失败:', error)
  }
}

// 保存点位图片
const handleSave = async () => {
  if (!formData.value.title) {
    ElMessage.warning('请输入标题')
    return
  }

  if (!formData.value.imageUrl) {
    ElMessage.warning('请上传图片')
    return
  }

  if (!formData.value.points) {
    ElMessage.warning('请添加关联点位')
    return
  }

  try {
    await request.post('/api/pointImage/save', formData.value)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    selectedPoints = []
    loadPoints()
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('保存失败:', error)
  }
}

// 监听抽屉关闭事件
watch(() => drawerVisible.value, (newVal) => {
  if (!newVal) {
    // 关闭抽屉时重新加载点位数据
    loadPoints()
  }
})

onMounted(() => {
  nextTick(() => {
    initMap()
  })
})
</script>

<style scoped>
.point-container {
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
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.right-panel {
  width: 60%;
  height: 100%;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

.tool-bar {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  flex-grow: 1;
  max-width: 250px;
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

.preview-container {
  padding: 10px;
}

.preview-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
}

.preview-image {
  margin-bottom: 15px;
  text-align: center;
  max-height: 300px;
  overflow: hidden;
}

.preview-description {
  line-height: 1.6;
  color: #606266;
  text-align: justify;
}

/* 弹出窗口样式 */
.popup-content {
  padding: 5px;
}

.popup-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.popup-image {
  margin: 5px 0;
  text-align: center;
}

.popup-description {
  font-size: 12px;
  color: #666;
}
</style>
