import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
    proxy: {
      '/captcha': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/address': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/menu': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin-menu': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin-group': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/category': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/attribute': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/goods-common': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/album': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/goods-common-body': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/goods-image': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/goods-spec-image': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/goods-video': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})