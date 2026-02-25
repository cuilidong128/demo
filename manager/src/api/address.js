import request from '@/utils/request'

// 地址相关API
export const addressApi = {
  // 获取地址列表
  getAddresses(params) {
    return request.get('/address/list', { params })
  },

  // 分页获取地址
  getAddressPage(current = 1, size = 10) {
    return request.get(`/address/page?current=${current}&size=${size}`)
  },

  // 根据条件查询地址
  getAddressesByCondition(params) {
    return request.get('/address/listByCondition', { params })
  },

  // 根据ID获取地址
  getAddressById(id) {
    return request.get(`/address/get/${id}`)
  },

  // 新增地址
  addAddress(data) {
    return request.post('/address/save', data)
  },

  // 更新地址
  updateAddress(data) {
    return request.put('/address/update', data)
  },

  // 删除地址
  deleteAddress(id) {
    return request.delete(`/address/delete/${id}`)
  },

  // 获取用户默认地址
  getDefaultAddress(memberId) {
    return request.get(`/address/default/${memberId}`)
  },

  // 设置默认地址
  setDefaultAddress(addressId, memberId) {
    return request.put(`/address/setDefault/${addressId}/${memberId}`)
  }
}