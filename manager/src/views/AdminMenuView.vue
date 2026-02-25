<template>
  <div class="admin-menu-container">
    <div class="header">
      <h2>菜单管理</h2>
      <el-button type="primary" @click="openAddDialog">新增菜单</el-button>
    </div>

    <el-table
      :data="menuTableData"
      stripe
      style="width: 100%"
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="菜单标题" />
      <el-table-column prop="name" label="菜单名称" />
      <el-table-column prop="url" label="URL" />
      <el-table-column prop="permission" label="权限标识" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 菜单编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="menuForm" :rules="menuRules" ref="menuFormRef" label-width="100px">
        <el-form-item label="菜单ID" prop="id">
          <el-input v-model.number="menuForm.id" type="number" placeholder="请输入菜单ID（留空自动生成）" />
        </el-form-item>
        <el-form-item label="菜单标题" prop="title">
          <el-input v-model="menuForm.title" placeholder="请输入菜单标题" />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="父级菜单">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTreeData"
            :props="{ label: 'title', value: 'id', children: 'children' }"
            placeholder="请选择父级菜单（不选则为顶级菜单）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="menuForm.url" placeholder="请输入URL" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="menuForm.permission" placeholder="请输入权限标识" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { adminMenuApi } from '@/api/adminMenu'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminMenuView',
  setup() {
    // 菜单列表数据
    const menuTableData = ref([])
    const menuTreeData = ref([])
    const loading = ref(false)

    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('add') // 'add' 或 'edit'
    const menuFormRef = ref()
    const menuForm = reactive({
      id: null,
      title: '',
      name: '',
      parentId: null,
      url: '',
      permission: ''
    })

    const menuRules = {
      title: [
        { required: true, message: '请输入菜单标题', trigger: 'blur' }
      ],
      name: [
        { required: true, message: '请输入菜单名称', trigger: 'blur' }
      ]
    }

    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增菜单' : '编辑菜单')

    // 将扁平数据转换为树形结构
    const buildTree = (data, parentId = 0) => {
      const result = []
      for (const item of data) {
        if (item.parentId === parentId) {
          const children = buildTree(data, item.id)
          if (children.length > 0) {
            item.children = children
          }
          result.push(item)
        }
      }
      return result
    }

    // 获取菜单列表
    const fetchMenus = async () => {
      loading.value = true
      try {
        const response = await adminMenuApi.getMenuList()
        if (response.code === 200) {
          const flatData = response.data || []
          // 构建树形结构用于表格展示
          menuTableData.value = buildTree(flatData)
          // 添加一个根节点选项用于选择父级菜单
          menuTreeData.value = [{ id: 0, title: '顶级菜单', children: buildTree(flatData) }]
        } else {
          ElMessage.error(response.message || '获取菜单列表失败')
        }
      } catch (error) {
        ElMessage.error('获取菜单列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    // 打开新增对话框
    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(menuForm, {
        id: null,
        title: '',
        name: '',
        parentId: null,
        url: '',
        permission: ''
      })
      dialogVisible.value = true
    }

    // 打开编辑对话框
    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(menuForm, {
        id: row.id,
        title: row.title,
        name: row.name,
        parentId: row.parentId === 0 ? null : row.parentId,
        url: row.url,
        permission: row.permission
      })
      dialogVisible.value = true
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await menuFormRef.value.validate()

        // 处理parentId，如果为null则设为0
        const submitData = { ...menuForm }
        if (submitData.parentId === null || submitData.parentId === undefined) {
          submitData.parentId = 0
        }

        let response
        if (dialogType.value === 'add') {
          response = await adminMenuApi.createMenu(submitData)
        } else {
          response = await adminMenuApi.updateMenu(submitData)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增菜单成功' : '更新菜单成功')
          dialogVisible.value = false
          fetchMenus()
        } else {
          ElMessage.error(response.message || (dialogType.value === 'add' ? '新增菜单失败' : '更新菜单失败'))
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    // 删除菜单
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此菜单吗？删除后其子菜单也会被删除。', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await adminMenuApi.deleteMenu(id)

        if (response.code === 200) {
          ElMessage.success('删除菜单成功')
          fetchMenus()
        } else {
          ElMessage.error(response.message || '删除菜单失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }

    onMounted(() => {
      fetchMenus()
    })

    return {
      menuTableData,
      menuTreeData,
      loading,
      dialogVisible,
      dialogType,
      menuFormRef,
      menuForm,
      menuRules,
      dialogTitle,
      fetchMenus,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.admin-menu-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
