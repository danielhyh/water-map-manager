// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
    baseURL: '/water-map-api', // 设置基础URL
    timeout: 15000 // 请求超时时间
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code && res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res
    },
    error => {
        console.error('响应错误:', error)
        ElMessage.error(error.message || '服务器异常')
        return Promise.reject(error)
    }
)

export default service
