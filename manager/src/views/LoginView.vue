<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>管理系统登录</h2>
        <p>请输入您的账号信息</p>
      </div>
      
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="captchaCode">
          <div class="captcha-container">
            <el-input
              v-model="loginForm.captchaCode"
              placeholder="请输入验证码"
              prefix-icon="Picture"
              size="large"
              clearable
            />
            <div class="captcha-image" @click="refreshCaptcha">
              <img 
                v-if="captchaImage" 
                :src="captchaImage" 
                alt="验证码"
                title="点击刷新验证码"
              />
              <div v-else class="captcha-placeholder">
                <el-icon><Picture /></el-icon>
                <span>获取验证码</span>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
            block
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Picture } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { captchaApi } from '@/api/captcha'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'LoginView',
  components: {
    User,
    Lock,
    Picture
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const loginFormRef = ref()
    const loading = ref(false)
    const captchaImage = ref('')
    const captchaUuid = ref('')

    // 登录表单数据
    const loginForm = reactive({
      username: '',
      password: '',
      captchaCode: ''
    })

    // 表单验证规则
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      captchaCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { len: 4, message: '验证码长度为4位', trigger: 'blur' }
      ]
    }

    // 获取验证码
    const getCaptcha = async () => {
      try {
        const response = await captchaApi.getCaptchaImage()
        if (response && response.code === 200) {
          captchaImage.value = response.data.image
          captchaUuid.value = response.data.uuid
        }
      } catch (error) {
        console.error('获取验证码失败:', error)
        ElMessage.error('获取验证码失败')
      }
    }

    // 刷新验证码
    const refreshCaptcha = () => {
      getCaptcha()
    }

    // 处理登录
    const handleLogin = async () => {
      if (!loginFormRef.value) return

      try {
        // 表单验证
        await loginFormRef.value.validate()
        
        // 检查是否有验证码UUID
        if (!captchaUuid.value) {
          ElMessage.error('请先获取验证码')
          return
        }

        loading.value = true
        
        // 准备登录数据
        const loginData = {
          username: loginForm.username.trim(),
          password: loginForm.password,
          captchaUuid: captchaUuid.value,
          captchaCode: loginForm.captchaCode.trim()
        }

        // 执行登录
        const response = await authApi.login(loginData)
        
        if (response && response.code === 200) {
          // 存储认证信息
          await authStore.setUserInfo({
            token: response.data.accessToken,
            refreshToken: response.data.refreshToken,
            userInfo: response.data.userInfo
          })
          
          ElMessage.success('登录成功')
          
          // 跳转到首页
          router.push('/')
        } else {
          ElMessage.error(response?.message || '登录失败')
          // 登录失败后刷新验证码
          refreshCaptcha()
        }
        
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败')
        // 登录失败后刷新验证码
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }

    // 页面加载时获取验证码
    onMounted(() => {
      getCaptcha()
    })

    return {
      loginForm,
      loginRules,
      loginFormRef,
      loading,
      captchaImage,
      captchaUuid,
      handleLogin,
      refreshCaptcha
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-header {
  padding: 30px 40px 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.login-header h2 {
  margin: 0 0 10px;
  font-size: 24px;
  font-weight: 500;
}

.login-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.login-form {
  padding: 30px 40px;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-container :deep(.el-form-item__content) {
  display: flex;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  transition: all 0.3s;
}

.captcha-image:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #909399;
  font-size: 12px;
}

.captcha-placeholder .el-icon {
  font-size: 16px;
  margin-bottom: 2px;
}

.login-button {
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-box {
    max-width: 100%;
  }
  
  .login-header,
  .login-form {
    padding: 20px;
  }
  
  .captcha-container {
    flex-direction: column;
  }
  
  .captcha-image {
    width: 100%;
    height: 40px;
  }
}
</style>