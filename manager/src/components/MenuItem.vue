<template>
  <!-- 有子菜单的情况 - 递归渲染 -->
  <el-sub-menu 
    v-if="hasChildren(menu)" 
    :index="getIndex(menu)"
  >
    <template #title>
      <span>{{ getMenuLabel(menu, true) }}</span>
    </template>
    <!-- 递归渲染子菜单 -->
    <menu-item 
      v-for="child in menu.children" 
      :key="child.id" 
      :menu="child"
      :is-child="true"
      @navigate="$emit('navigate', $event)"
    />
  </el-sub-menu>
  <!-- 无子菜单的情况 - 可点击的菜单项 -->
  <el-menu-item 
    v-else 
    :index="getIndex(menu)"
    @click="handleClick"
  >
    <span>{{ getMenuLabel(menu, isChild) }}</span>
  </el-menu-item>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    menu: {
      type: Object,
      required: true
    },
    isChild: {
      type: Boolean,
      default: false
    }
  },
  emits: ['navigate'],
  setup(props, { emit }) {
    // 判断是否有子菜单
    const hasChildren = (menu) => {
      return menu.children && menu.children.length > 0
    }
    
    // 获取菜单索引（用于路由跳转）
    const getIndex = (menu) => {
      // 优先使用 url 或 path，如果没有则使用 id
      const path = menu.url || menu.path || String(menu.id)
      // 确保路径以 / 开头
      return path && path.startsWith('/') ? path : '/' + path
    }
    
    // 获取菜单显示文本
    // 一级菜单显示 title，二级菜单显示 name
    const getMenuLabel = (menu, isChildMenu) => {
      if (isChildMenu) {
        // 二级菜单：优先显示 name，如果没有则显示 title
        return menu.name || menu.title || '未命名菜单'
      } else {
        // 一级菜单：优先显示 title，如果没有则显示 name
        return menu.title || menu.name || '未命名菜单'
      }
    }
    
    // 处理点击事件
    const handleClick = () => {
      let path = props.menu.url || props.menu.path
      if (path && path !== 'null' && path !== 'undefined') {
        // 确保路径以 / 开头
        if (!path.startsWith('/')) {
          path = '/' + path
        }
        emit('navigate', path)
      }
    }
    
    return {
      hasChildren,
      getIndex,
      getMenuLabel,
      handleClick,
      isChild: props.isChild
    }
  }
}
</script>
