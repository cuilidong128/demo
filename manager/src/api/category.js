import request from '@/utils/request'

// 商品类目API
export const categoryApi = {
  // 获取所有类目列表
  getList() {
    return request.get('/category/list')
  },

  // 获取类目树形结构
  getTree() {
    return request.get('/category/tree')
  },

  // 分页获取类目列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/category/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据ID获取类目
  getById(id) {
    return request.get(`/category/${id}`)
  },

  // 根据父级ID获取子类目
  getByParentId(parentId) {
    return request.get(`/category/parent/${parentId}`)
  },

  // 创建类目
  create(data) {
    return request.post('/category/create', data)
  },

  // 更新类目
  update(data) {
    return request.put('/category/update', data)
  },

  // 删除类目
  delete(id) {
    return request.delete(`/category/${id}`)
  }
}
