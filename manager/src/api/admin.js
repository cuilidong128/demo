import request from '@/utils/request'

// 管理员管理API
export const adminApi = {
  // 获取所有管理员列表
  getAdminList() {
    return request.get('/admin/list')
  },

  // 分页获取管理员列表
  getAdminPage(pageNum = 1, pageSize = 10) {
    return request.get(`/admin/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据ID获取管理员
  getAdminById(id) {
    return request.get(`/admin/${id}`)
  },

  // 根据用户组ID获取管理员列表
  getAdminsByGroupId(groupId) {
    return request.get(`/admin/group/${groupId}`)
  },

  // 创建管理员
  createAdmin(data) {
    return request.post('/admin/create', data)
  },

  // 更新管理员
  updateAdmin(data) {
    return request.put('/admin/update', data)
  },

  // 删除管理员
  deleteAdmin(id) {
    return request.delete(`/admin/${id}`)
  },

  // 批量删除管理员
  batchDeleteAdmins(ids) {
    return request.delete('/admin/batch', { data: ids })
  },

  // 解锁管理员账户
  unlockAdmin(id) {
    return request.post(`/admin/unlock/${id}`)
  }
}
