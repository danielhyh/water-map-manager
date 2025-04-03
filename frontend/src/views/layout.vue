<!-- src/views/Layout.vue -->
<template>
  <el-container class="layout-container">
    <el-header height="60px">
      <div class="header-title">古旧水利图管理系统</div>
    </el-header>
    <el-main>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="main-tabs">
        <el-tab-pane label="区域描述" name="AreaDescription"></el-tab-pane>
        <el-tab-pane label="自定义地图" name="CustomMap"></el-tab-pane>
        <el-tab-pane label="点位图片" name="PointImage"></el-tab-pane>
      </el-tabs>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeTab = ref('Home')

// 根据当前路由设置激活的标签页
onMounted(() => {
  const name = route.name
  if (name) {
    activeTab.value = name
  }
})

// 标签页切换处理
const handleTabClick = (tab) => {
  router.push({ name: tab.props.name })
}

// 监听路由变化
watch(() => route.name, (newName) => {
  if (newName) {
    activeTab.value = newName
  }
})
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  line-height: 60px;
}

.main-tabs {
  width: 100%;
  margin-bottom: 20px;
}

.el-main {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}
</style>