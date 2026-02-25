import request from '@/utils/request'

// 相册API
export const albumApi = {
  // 获取所有相册列表
  getList() {
    return request.get('/album/list')
  },

  // 获取相册树形结构
  getTree() {
    return request.get('/album/tree')
  },

  // 分页获取相册列表
  getPage(pageNum = 1, pageSize = 10) {
    return request.get(`/album/page?pageNum=${pageNum}&pageSize=${pageSize}`)
  },

  // 根据ID获取相册
  getById(id) {
    return request.get(`/album/${id}`)
  },

  // 创建相册
  create(data) {
    return request.post('/album/create', data)
  },

  // 更新相册
  update(data) {
    return request.put('/album/update', data)
  },

  // 删除相册
  delete(id) {
    return request.delete(`/album/${id}`)
  },

  // ==================== 相册文件管理 ====================
  
  // 根据相册ID获取文件列表
  getFilesByAlbumId(albumId) {
    return request.get(`/album/files/list/${albumId}`)
  },

  // 分页获取文件列表（支持搜索和系统图片筛选）
  getFilesPage(params) {
    const { albumId, isSystem, keyword, pageNum = 1, pageSize = 20 } = params
    let url = `/album/files/page?pageNum=${pageNum}&pageSize=${pageSize}`
    if (albumId !== undefined && albumId !== null) {
      url += `&albumId=${albumId}`
    }
    if (isSystem !== undefined && isSystem !== null) {
      url += `&isSystem=${isSystem}`
    }
    if (keyword) {
      url += `&keyword=${encodeURIComponent(keyword)}`
    }
    return request.get(url)
  },

  // 创建文件记录
  createFile(data) {
    return request.post('/album/files/create', data)
  },

  // 更新文件记录
  updateFile(data) {
    return request.put('/album/files/update', data)
  },

  // 删除文件记录
  deleteFile(id) {
    return request.delete(`/album/files/${id}`)
  },

  // 批量删除文件
  batchDeleteFiles(fileIds) {
    return request.post('/album/files/batch-delete', { fileIds })
  },

  // 批量转移文件
  batchTransferFiles(fileIds, targetAlbumId) {
    return request.post('/album/files/batch-transfer', { fileIds, targetAlbumId })
  },

  // 重命名文件
  renameFile(fileId, newName) {
    return request.post('/album/files/rename', { fileId, newName })
  }
}
