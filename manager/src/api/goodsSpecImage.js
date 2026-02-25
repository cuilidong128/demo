import request from '@/utils/request'

// 商品SKU管理图片API
export const goodsSpecImageApi = {
  // 获取所有规格图片列表
  getList() {
    return request.get('/goods-spec-image/list')
  },

  // 分页获取规格图片列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/goods-spec-image/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据商品SPU ID获取规格图片列表
  getByCommonId(commonId) {
    return request.get(`/goods-spec-image/common/${commonId}`)
  },

  // 根据ID获取规格图片
  getById(id) {
    return request.get(`/goods-spec-image/${id}`)
  },

  // 创建规格图片
  create(data) {
    return request.post('/goods-spec-image/create', data)
  },

  // 批量创建规格图片
  batchCreate(data) {
    return request.post('/goods-spec-image/batch-create', data)
  },

  // 更新规格图片
  update(data) {
    return request.put('/goods-spec-image/update', data)
  },

  // 删除规格图片
  delete(id) {
    return request.delete(`/goods-spec-image/${id}`)
  },

  // 批量删除规格图片
  batchDelete(ids) {
    return request.delete('/goods-spec-image/batch', { params: { ids } })
  }
}
