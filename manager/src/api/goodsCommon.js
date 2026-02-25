import request from '@/utils/request'

// 商品SPU API
export const goodsCommonApi = {
  // 获取所有商品SPU列表
  getList() {
    return request.get('/goods-common/list')
  },

  // 分页获取商品SPU列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/goods-common/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据类目ID获取商品列表
  getByCategoryId(categoryId) {
    return request.get(`/goods-common/category/${categoryId}`)
  },

  // 根据ID获取商品SPU
  getById(id) {
    return request.get(`/goods-common/${id}`)
  },

  // 创建商品SPU
  create(data) {
    return request.post('/goods-common/create', data)
  },

  // 更新商品SPU
  update(data) {
    return request.put('/goods-common/update', data)
  },

  // 删除商品SPU
  delete(id) {
    return request.delete(`/goods-common/${id}`)
  },

  // ==================== SKU管理 ====================
  
  // 根据SPU ID获取SKU列表
  getSkusByCommonId(commonId) {
    return request.get(`/goods-common/sku/list/${commonId}`)
  },

  // 创建SKU
  createSku(data) {
    return request.post('/goods-common/sku/create', data)
  },

  // 更新SKU
  updateSku(data) {
    return request.put('/goods-common/sku/update', data)
  },

  // 删除SKU
  deleteSku(id) {
    return request.delete(`/goods-common/sku/${id}`)
  }
}
