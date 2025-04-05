// vite.config.js
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/water-map-api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
