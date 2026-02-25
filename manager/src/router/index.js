import { createRouter, createWebHistory } from 'vue-router'
import UsersView from '@/views/UsersView.vue'
import AddressesView from '@/views/AddressesView.vue'
import LoginView from '@/views/LoginView.vue'
import AdminGroupView from '@/views/AdminGroupView.vue'
import AdminMenuView from '@/views/AdminMenuView.vue'
import AdminView from '@/views/AdminView.vue'
import CategoryView from '@/views/CategoryView.vue'
import GoodsCommonView from '@/views/GoodsCommonView.vue'
import AttributeView from '@/views/AttributeView.vue'
import AlbumView from '@/views/AlbumView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/users',
    name: 'Users',
    component: UsersView,
    meta: { requiresAuth: true }
  },
  {
    path: '/addresses',
    name: 'Addresses',
    component: AddressesView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-groups',
    name: 'AdminGroups',
    component: AdminGroupView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-group/page',
    name: 'AdminGroupPage',
    component: AdminGroupView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-menus',
    name: 'AdminMenus',
    component: AdminMenuView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-menu/list',
    name: 'AdminMenuList',
    component: AdminMenuView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admins',
    name: 'Admins',
    component: AdminView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/page',
    name: 'AdminPage',
    component: AdminView,
    meta: { requiresAuth: true }
  },
  {
    path: '/category/list',
    name: 'CategoryList',
    component: CategoryView,
    meta: { requiresAuth: true }
  },
  {
    path: '/goods-common/list',
    name: 'GoodsCommonList',
    component: GoodsCommonView,
    meta: { requiresAuth: true }
  },
  {
    path: '/attribute/list',
    name: 'AttributeList',
    component: AttributeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/album/list',
    name: 'AlbumList',
    component: AlbumView,
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    redirect: '/users',
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  
  // 如果访问需要认证的页面但没有token，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
  }
  // 如果已登录且访问登录页，跳转到首页
  else if (to.path === '/login' && token) {
    next('/')
  }
  // 其他情况正常跳转
  else {
    next()
  }
})

export default router