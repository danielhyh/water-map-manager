// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'

// 导入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 注册插件
app.use(router)
app.use(pinia)
app.use(ElementPlus)

app.mount('#app')