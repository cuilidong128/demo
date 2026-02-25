import request from '@/utils/request'

// 认证相关API
export const authApi = {
  // 管理员登录
  login(data) {
    return request.post('/auth/login', data)
  },

  // 刷新Token
  refreshToken(data) {
    return request.post('/auth/refresh', data)
  },

  // 登出
  logout() {
    return request.post('/auth/logout')
  },

  // 获取当前用户信息
  getCurrentUserInfo() {
    return request.get('/auth/me')
  }
}
