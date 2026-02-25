import request from '@/utils/request'

// 菜单相关API
export const menuApi = {
  // 获取当前登录用户的菜单列表（使用admin-menu/tree）
  getCurrentUserMenus() {
    return request.get('/admin-menu/tree')
  },

  // 清除菜单缓存
  clearMenuCache() {
    return request.post('/menu/clear-cache')
  }
}