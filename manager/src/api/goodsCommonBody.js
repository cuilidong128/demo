import request from '@/utils/request'

// 商品详情API
export const goodsCommonBodyApi = {
  // 根据商品SPU ID获取详情
  getByCommonId(commonId) {
    return request.get(`/goods-common-body/${commonId}`)
  },

  // 创建商品详情
  create(data) {
    return request.post('/goods-common-body/create', data)
  },

  // 更新商品详情
  update(data) {
    return request.put('/goods-common-body/update', data)
  },

  // 保存或更新商品详情
  saveOrUpdate(data) {
    return request.post('/goods-common-body/save-or-update', data)
  },

  // 删除商品详情
  delete(commonId) {
    return request.delete(`/goods-common-body/${commonId}`)
  }
}
