import request from '@/utils/request'

// 文件上传API
export const fileApi = {
  /**
   * 单文件上传
   * @param {File} file - 文件对象
   * @param {string} uploadType - 上传类型：local-本地，oss-阿里云
   * @param {string} directory - 目标目录
   * @returns {Promise} - 返回文件URL
   */
  upload(file, uploadType = 'local', directory = '') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('uploadType', uploadType)
    if (directory) {
      formData.append('directory', directory)
    }
    
    return request.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 批量文件上传
   * @param {File[]} files - 文件列表
   * @param {string} uploadType - 上传类型
   * @param {string} directory - 目标目录
   * @returns {Promise} - 返回文件URL列表
   */
  uploadBatch(files, uploadType = 'local', directory = '') {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('uploadType', uploadType)
    if (directory) {
      formData.append('directory', directory)
    }
    
    return request.post('/file/upload/batch', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 删除文件
   * @param {string} fileUrl - 文件URL
   * @param {string} uploadType - 上传类型（可选，不传则自动识别）
   * @returns {Promise}
   */
  delete(fileUrl, uploadType = '') {
    return request.delete('/file/delete', {
      params: {
        fileUrl,
        uploadType
      }
    })
  },

  /**
   * 获取文件上传类型
   * @param {string} fileUrl - 文件URL
   * @returns {Promise} - 返回上传类型
   */
  getUploadType(fileUrl) {
    return request.get('/file/type', {
      params: { fileUrl }
    })
  },

  /**
   * 根据相对路径获取完整URL
   * @param {string} relativePath - 相对路径
   * @returns {Promise} - 返回完整URL
   */
  getFullUrl(relativePath) {
    return request.get('/file/full-url', {
      params: { relativePath }
    })
  },

  /**
   * 构建完整URL（前端本地方法）
   * @param {string} relativePath - 相对路径
   * @returns {string} - 完整URL
   */
  buildFullUrl(relativePath) {
    if (!relativePath) return ''
    if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
      return relativePath
    }
    // 使用配置的基础URL
    const baseUrl = import.meta.env.VITE_UPLOAD_BASE_URL || 'http://localhost:8080/uploads'
    return `${baseUrl}/${relativePath}`
  }
}
