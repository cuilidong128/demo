import request from '@/utils/request'

// 用户组权限管理API
export const adminGroupPermissionApi = {
  // 获取所有组权限列表
  getPermissionList() {
    return request.get('/admin-group-permission/list')
  },

  // 根据组ID获取权限列表
  getPermissionsByGroupId(groupId) {
    return request.get(`/admin-group-permission/group/${groupId}`)
  },

  // 创建组权限
  createPermission(data) {
    return request.post('/admin-group-permission/create', data)
  },

  // 批量创建组权限
  batchCreatePermissions(permissions) {
    return request.post('/admin-group-permission/batch-create', permissions)
  },

  // 删除组权限
  deletePermission(id) {
    return request.delete(`/admin-group-permission/${id}`)
  },

  // 根据组ID删除所有权限
  deletePermissionsByGroupId(groupId) {
    return request.delete(`/admin-group-permission/group/${groupId}`)
  },

  // 设置用户组菜单权限（先删除再批量添加）
  setGroupPermissions(groupId, menuIds) {
    return request.post('/admin-group-permission/set-permissions', {
      groupId,
      menuIds
    })
  }
}
