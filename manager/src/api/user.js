import request from '@/utils/request'

// 用户相关API
export const userApi = {
  // 获取用户列表
  getUsers(params) {
    return request.get('/user/list', { params })
  },

  // 分页获取用户
  getUserPage(current = 1, size = 10) {
    return request.get(`/user/page?current=${current}&size=${size}`)
  },

  // 根据条件查询用户
  getUsersByCondition(params) {
    return request.get('/user/listByCondition', { params })
  },

  // 根据ID获取用户
  getUserById(id) {
    return request.get(`/user/get/${id}`)
  },

  // 新增用户
  addUser(data) {
    return request.post('/user/save', data)
  },

  // 更新用户
  updateUser(data) {
    return request.put('/user/update', data)
  },

  // 删除用户
  deleteUser(id) {
    return request.delete(`/user/delete/${id}`)
  }
}