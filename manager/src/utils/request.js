import axios from 'axios'

const apiClient = axios.create({
  baseURL: '',
  timeout: 10000
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 添加认证Token
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

export default apiClient