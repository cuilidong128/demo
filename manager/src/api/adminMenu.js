import request from '@/utils/request'

// 菜单管理API
export const adminMenuApi = {
  // 获取所有菜单列表（树形结构）
  getMenuTree() {
    return request.get('/admin-menu/tree')
  },

  // 获取所有菜单列表（扁平结构）
  getMenuList() {
    return request.get('/admin-menu/list')
  },

  // 根据ID获取菜单
  getMenuById(id) {
    return request.get(`/admin-menu/${id}`)
  },

  // 创建菜单
  createMenu(data) {
    return request.post('/admin-menu/create', data)
  },

  // 更新菜单
  updateMenu(data) {
    return request.put('/admin-menu/update', data)
  },

  // 删除菜单
  deleteMenu(id) {
    return request.delete(`/admin-menu/${id}`)
  }
}
