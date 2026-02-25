import request from '@/utils/request'

// 验证码相关API
export const captchaApi = {
  // 获取验证码图片
  getCaptchaImage() {
    return request.get('/captcha/image')
  },

  // 验证验证码（用于前端预验证）
  validateCaptcha(data) {
    return request.post('/captcha/validate', data)
  }
}
