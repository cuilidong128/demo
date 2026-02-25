import request from '@/utils/request'

// 商品视频API
export const goodsVideoApi = {
  // 获取所有视频列表
  getList() {
    return request.get('/goods-video/list')
  },

  // 分页获取视频列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/goods-video/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据商品SPU ID获取视频列表
  getByCommonId(commonId) {
    return request.get(`/goods-video/common/${commonId}`)
  },

  // 根据ID获取视频
  getById(id) {
    return request.get(`/goods-video/${id}`)
  },

  // 创建视频
  create(data) {
    return request.post('/goods-video/create', data)
  },

  // 批量创建视频
  batchCreate(data) {
    return request.post('/goods-video/batch-create', data)
  },

  // 更新视频
  update(data) {
    return request.put('/goods-video/update', data)
  },

  // 删除视频
  delete(id) {
    return request.delete(`/goods-video/${id}`)
  },

  // 批量删除视频
  batchDelete(ids) {
    return request.delete('/goods-video/batch', { params: { ids } })
  },

  // 设置默认视频
  setDefault(videoId, commonId) {
    return request.put(`/goods-video/set-default/${videoId}?commonId=${commonId}`)
  }
}
