<template>
  <div class="admin-group-container">
    <div class="header">
      <h2>用户组管理</h2>
      <el-button type="primary" @click="openAddDialog">新增用户组</el-button>
    </div>

    <el-table :data="groupList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="groupId" label="ID" width="80" />
      <el-table-column prop="groupName" label="组名称" />
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openAssignMenuDialog(row)">分配菜单</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.groupId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 用户组编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="groupForm" :rules="groupRules" ref="groupFormRef" label-width="80px">
        <el-form-item label="组名称" prop="groupName">
          <el-input v-model="groupForm.groupName" placeholder="请输入组名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog title="分配菜单权限" v-model="assignMenuDialogVisible" width="500px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        show-checkbox
        node-key="id"
        :props="defaultProps"
        default-expand-all
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignMenuDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAssignMenuSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { adminGroupApi } from '@/api/adminGroup'
import { adminMenuApi } from '@/api/adminMenu'
import { adminGroupPermissionApi } from '@/api/adminGroupPermission'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminGroupView',
  setup() {
    // 用户组列表数据
    const groupList = ref([])
    const loading = ref(false)

    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('add') // 'add' 或 'edit'
    const groupFormRef = ref()
    const groupForm = reactive({
      groupId: null,
      groupName: ''
    })

    const groupRules = {
      groupName: [
        { required: true, message: '请输入组名称', trigger: 'blur' }
      ]
    }

    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增用户组' : '编辑用户组')

    // 分配菜单对话框相关
    const assignMenuDialogVisible = ref(false)
    const menuTreeRef = ref()
    const menuTreeData = ref([])
    const currentGroupId = ref(null)

    const defaultProps = {
      children: 'children',
      label: 'label'  // 使用统一的 label 字段
    }

    // 获取用户组列表
    const fetchGroups = async () => {
      loading.value = true
      try {
        const response = await adminGroupApi.getGroupList()
        if (response.code === 200) {
          groupList.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取用户组列表失败')
        }
      } catch (error) {
        ElMessage.error('获取用户组列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    // 打开新增对话框
    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(groupForm, {
        groupId: null,
        groupName: ''
      })
      dialogVisible.value = true
    }

    // 打开编辑对话框
    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(groupForm, {
        groupId: row.groupId,
        groupName: row.groupName
      })
      dialogVisible.value = true
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await groupFormRef.value.validate()

        let response
        if (dialogType.value === 'add') {
          response = await adminGroupApi.createGroup(groupForm)
        } else {
          response = await adminGroupApi.updateGroup(groupForm)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增用户组成功' : '更新用户组成功')
          dialogVisible.value = false
          fetchGroups()
        } else {
          ElMessage.error(response.message || (dialogType.value === 'add' ? '新增用户组失败' : '更新用户组失败'))
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    // 删除用户组
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此用户组吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await adminGroupApi.deleteGroup(id)

        if (response.code === 200) {
          ElMessage.success('删除用户组成功')
          fetchGroups()
        } else {
          ElMessage.error(response.message || '删除用户组失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 处理菜单树数据，为每个节点添加 label 字段
    // 一级菜单显示 title，二级菜单显示 name
    const processMenuTreeData = (menus, isChild = false) => {
      return menus.map(menu => {
        const processedMenu = {
          ...menu,
          // 一级菜单使用 title，二级菜单使用 name
          label: isChild ? (menu.name || menu.title) : (menu.title || menu.name)
        }
        
        // 递归处理子菜单
        if (processedMenu.children && processedMenu.children.length > 0) {
          processedMenu.children = processMenuTreeData(processedMenu.children, true)
        }
        
        return processedMenu
      })
    }

    // 打开分配菜单对话框
    const openAssignMenuDialog = async (row) => {
      currentGroupId.value = row.groupId
      assignMenuDialogVisible.value = true

      try {
        // 获取菜单树
        const menuResponse = await adminMenuApi.getMenuTree()
        if (menuResponse.code === 200) {
          // 处理菜单数据，添加 label 字段
          const rawMenuData = menuResponse.data || []
          menuTreeData.value = processMenuTreeData(rawMenuData)
        }

        // 获取当前用户组已分配的菜单
        const permissionResponse = await adminGroupPermissionApi.getPermissionsByGroupId(row.groupId)
        if (permissionResponse.code === 200) {
          const permissions = permissionResponse.data || []
          const menuIds = permissions.map(p => p.menuId)

          // 设置选中的节点
          setTimeout(() => {
            if (menuTreeRef.value) {
              menuTreeRef.value.setCheckedKeys(menuIds)
            }
          }, 100)
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('获取菜单数据失败')
      }
    }

    // 提交菜单分配
    const handleAssignMenuSubmit = async () => {
      try {
        // 只获取完全勾选的节点（不包括半选中的父节点）
        // 这样只会保存用户实际勾选的二级或三级菜单
        const checkedKeys = menuTreeRef.value.getCheckedKeys()

        // 先删除该组的所有权限
        await adminGroupPermissionApi.deletePermissionsByGroupId(currentGroupId.value)

        // 批量添加新的权限（只添加实际勾选的菜单）
        if (checkedKeys.length > 0) {
          const permissions = checkedKeys.map(menuId => ({
            groupId: currentGroupId.value,
            menuId: menuId
          }))
          await adminGroupPermissionApi.batchCreatePermissions(permissions)
        }

        ElMessage.success('分配菜单权限成功')
        assignMenuDialogVisible.value = false
      } catch (error) {
        console.error(error)
        ElMessage.error('分配菜单权限失败')
      }
    }

    onMounted(() => {
      fetchGroups()
    })

    return {
      groupList,
      loading,
      dialogVisible,
      dialogType,
      groupFormRef,
      groupForm,
      groupRules,
      dialogTitle,
      assignMenuDialogVisible,
      menuTreeRef,
      menuTreeData,
      defaultProps,
      fetchGroups,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      processMenuTreeData,
      openAssignMenuDialog,
      handleAssignMenuSubmit
    }
  }
}
</script>

<style scoped>
.admin-group-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
