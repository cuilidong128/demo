import request from '@/utils/request'

// 商品属性API
export const attributeApi = {
  // 获取所有属性列表
  getList() {
    return request.get('/attribute/list')
  },

  // 分页获取属性列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/attribute/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据类目ID获取属性列表
  getByCategoryId(categoryId) {
    return request.get(`/attribute/category/${categoryId}`)
  },

  // 根据ID获取属性
  getById(id) {
    return request.get(`/attribute/${id}`)
  },

  // 创建属性
  create(data) {
    return request.post('/attribute/create', data)
  },

  // 更新属性
  update(data) {
    return request.put('/attribute/update', data)
  },

  // 删除属性
  delete(id) {
    return request.delete(`/attribute/${id}`)
  },

  // ==================== 属性值管理 ====================
  
  // 根据属性ID获取属性值列表
  getValuesByAttributeId(attributeId) {
    return request.get(`/attribute/value/list/${attributeId}`)
  },

  // 创建属性值
  createValue(data) {
    return request.post('/attribute/value/create', data)
  },

  // 更新属性值
  updateValue(data) {
    return request.put('/attribute/value/update', data)
  },

  // 删除属性值
  deleteValue(id) {
    return request.delete(`/attribute/value/${id}`)
  }
}
