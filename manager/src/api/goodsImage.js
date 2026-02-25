import request from '@/utils/request'

// 商品图片API
export const goodsImageApi = {
  // 获取所有图片列表
  getList() {
    return request.get('/goods-image/list')
  },

  // 分页获取图片列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/goods-image/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据商品SPU ID获取图片列表
  getByCommonId(commonId) {
    return request.get(`/goods-image/common/${commonId}`)
  },

  // 根据ID获取图片
  getById(id) {
    return request.get(`/goods-image/${id}`)
  },

  // 创建图片
  create(data) {
    return request.post('/goods-image/create', data)
  },

  // 批量创建图片
  batchCreate(data) {
    return request.post('/goods-image/batch-create', data)
  },

  // 更新图片
  update(data) {
    return request.put('/goods-image/update', data)
  },

  // 删除图片
  delete(id) {
    return request.delete(`/goods-image/${id}`)
  },

  // 批量删除图片
  batchDelete(ids) {
    return request.delete('/goods-image/batch', { params: { ids } })
  },

  // 设置默认图片
  setDefault(imageId, commonId) {
    return request.put(`/goods-image/set-default/${imageId}?commonId=${commonId}`)
  }
}
