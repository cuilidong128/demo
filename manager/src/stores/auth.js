import { ref } from 'vue'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

// 创建全局状态（单例模式）
const isAuthenticated = ref(false)
const userInfo = ref(null)
const accessToken = ref('')
const refreshToken = ref('')

// 初始化认证状态
const initAuth = () => {
  const token = localStorage.getItem('accessToken')
  const refresh = localStorage.getItem('refreshToken')
  const user = localStorage.getItem('userInfo')
  
  if (token && user) {
    accessToken.value = token
    refreshToken.value = refresh || ''
    userInfo.value = JSON.parse(user)
    isAuthenticated.value = true
  }
}

// 设置用户信息
const setUserInfo = async (authData) => {
  try {
    accessToken.value = authData.token
    refreshToken.value = authData.refreshToken
    userInfo.value = authData.userInfo
    isAuthenticated.value = true

    // 存储到localStorage
    localStorage.setItem('accessToken', authData.token)
    localStorage.setItem('refreshToken', authData.refreshToken)
    localStorage.setItem('userInfo', JSON.stringify(authData.userInfo))
    
    return true
  } catch (error) {
    console.error('设置用户信息失败:', error)
    return false
  }
}

// 清除认证信息
const clearAuth = () => {
  accessToken.value = ''
  refreshToken.value = ''
  userInfo.value = null
  isAuthenticated.value = false

  // 清除localStorage
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('userInfo')
}

// 登出
const logout = async () => {
  try {
    // 调用后端登出接口
    await authApi.logout()
  } catch (error) {
    console.error('登出接口调用失败:', error)
  } finally {
    // 无论后端是否成功，都清除本地状态
    clearAuth()
    ElMessage.success('已退出登录')
  }
}

// 刷新Token
const refreshAuthToken = async () => {
  if (!refreshToken.value) {
    clearAuth()
    return false
  }

  try {
    const response = await authApi.refreshToken({
      refreshToken: refreshToken.value
    })

    if (response && response.code === 200) {
      // 更新Token
      accessToken.value = response.data.accessToken
      localStorage.setItem('accessToken', response.data.accessToken)
      return true
    } else {
      clearAuth()
      return false
    }
  } catch (error) {
    console.error('刷新Token失败:', error)
    clearAuth()
    return false
  }
}

// 检查Token是否过期
const isTokenExpired = () => {
  if (!accessToken.value) return true
  
  try {
    const payload = JSON.parse(atob(accessToken.value.split('.')[1]))
    const exp = payload.exp * 1000 // 转换为毫秒
    return Date.now() >= exp
  } catch (error) {
    return true
  }
}

// 获取当前用户信息
const getCurrentUserInfo = async () => {
  if (!isAuthenticated.value) return null

  try {
    const response = await authApi.getCurrentUserInfo()
    if (response && response.code === 200) {
      userInfo.value = response.data
      localStorage.setItem('userInfo', JSON.stringify(response.data))
      return response.data
    }
    return null
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return null
  }
}

// 初始化时检查认证状态
initAuth()

// 创建认证状态管理（单例）
export function useAuthStore() {
  return {
    // 状态
    isAuthenticated,
    userInfo,
    accessToken,
    refreshToken,
    
    // 方法
    setUserInfo,
    clearAuth,
    logout,
    refreshAuthToken,
    isTokenExpired,
    getCurrentUserInfo,
    initAuth
  }
}