<template>
  <div id="app">
    <!-- 登录页面 - 完全独立布局 -->
    <template v-if="isLoginPage">
      <router-view />
    </template>
    
    <!-- 主布局 - 包含左侧菜单 -->
    <el-container v-else-if="authStore.isAuthenticated.value">
      <el-header>
        <div class="header-content">
          <h1>管理系统</h1>
          <div class="user-info">
            <span>欢迎，{{ authStore.userInfo.value?.name || '管理员' }}</span>
            <el-button 
              type="danger" 
              plain
              @click="handleLogout"
              size="small"
            >
              退出登录
            </el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu 
            :default-active="$route.path" 
            class="el-menu-vertical-demo"
            @select="handleMenuSelect"
          >
            <!-- 递归渲染菜单 -->
            <template v-for="menu in menuList" :key="menu.id">
              <menu-item :menu="menu" @navigate="navigateTo" />
            </template>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
    
    <!-- 其他未认证页面 -->
    <router-view v-else />
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { computed, ref, onMounted, watch } from 'vue'
import { adminMenuApi } from '@/api/adminMenu'
import { ElMessage } from 'element-plus'
import MenuItem from '@/components/MenuItem.vue'

export default {
  name: 'App',
  components: {
    MenuItem
  },
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()
    
    // 菜单列表
    const menuList = ref([])
    
    // 标记是否已经获取过菜单
    const hasFetchedMenus = ref(false)
    
    // 判断当前是否为登录页面
    const isLoginPage = computed(() => route.path === '/login')
    
    // 获取用户菜单
    const fetchUserMenus = async () => {
      if (!authStore.isAuthenticated.value) return
      
      try {
        const response = await adminMenuApi.getMenuTree()
        if (response && response.code === 200) {
          menuList.value = response.data || []
        } else {
          ElMessage.error(response?.message || '获取菜单失败')
        }
      } catch (error) {
        console.error('获取菜单失败:', error)
        ElMessage.error('获取菜单失败')
      }
    }
    
    // 监听登录状态变化，只有从false变为true时才获取菜单（即登录成功时）
    watch(() => authStore.isAuthenticated.value, (newVal, oldVal) => {
      // 只有登录成功（从false变为true）且未获取过菜单时才获取
      if (newVal === true && oldVal === false && !hasFetchedMenus.value) {
        hasFetchedMenus.value = true
        fetchUserMenus()
      } else if (newVal === false) {
        // 退出登录时重置标记并清空菜单
        hasFetchedMenus.value = false
        menuList.value = []
      }
    })
    
    const handleLogout = async () => {
      await authStore.logout()
      menuList.value = []
      router.push('/login')
    }
    
    // 菜单选择处理
    const handleMenuSelect = (index) => {
      if (index && index !== 'null' && index !== 'undefined') {
        router.push(index)
      }
    }
    
    // 点击菜单项跳转
    const navigateTo = (path) => {
      if (path && path !== 'null' && path !== 'undefined') {
        router.push(path)
      }
    }
    
    // 组件挂载时获取菜单
    onMounted(() => {
      // 延迟执行，确保状态已恢复
      setTimeout(() => {
        // 只在已认证、不是登录页面、且未获取过菜单时才获取
        if (authStore.isAuthenticated.value && route.path !== '/login' && !hasFetchedMenus.value) {
          hasFetchedMenus.value = true
          fetchUserMenus()
        }
      }, 100)
    })
    
    return {
      authStore,
      menuList,
      isLoginPage,
      hasFetchedMenus,
      handleLogout,
      handleMenuSelect,
      navigateTo
    }
  }
}
</script>

<style>
#app {
  height: 100vh;
}

.el-header {
  background-color: #409EFF;
  color: white;
  padding: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  font-size: 14px;
}

.el-aside {
  background-color: #E4E7ED;
}

.el-main {
  background-color: #f5f7fa;
}
</style>