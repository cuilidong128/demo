import request from '@/utils/request'

// 用户组管理API
export const adminGroupApi = {
  // 获取所有用户组列表
  getGroupList() {
    return request.get('/admin-group/list')
  },

  // 分页获取用户组列表
  getGroupPage(pageNum = 1, pageSize = 10) {
    return request.get(`/admin-group/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据ID获取用户组
  getGroupById(id) {
    return request.get(`/admin-group/${id}`)
  },

  // 创建用户组
  createGroup(data) {
    return request.post('/admin-group/create', data)
  },

  // 更新用户组
  updateGroup(data) {
    return request.put('/admin-group/update', data)
  },

  // 删除用户组
  deleteGroup(id) {
    return request.delete(`/admin-group/${id}`)
  }
}
