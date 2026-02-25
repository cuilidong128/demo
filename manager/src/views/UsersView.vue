<template>
  <div class="users-container">
    <div class="header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openAddDialog">新增用户</el-button>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.name" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="searchForm.age" placeholder="请输入年龄" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="userList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="sizes, prev, pager, next, total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model.number="userForm.age" type="number" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" type="email" placeholder="请输入邮箱" />
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
import { userApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UsersView',
  setup() {
    // 用户列表数据
    const userList = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    
    // 搜索表单
    const searchForm = reactive({
      name: '',
      age: ''
    })
    
    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('add') // 'add' 或 'edit'
    const userFormRef = ref()
    const userForm = reactive({
      id: null,
      name: '',
      age: null,
      email: ''
    })
    
    const userRules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      age: [
        { required: true, message: '请输入年龄', trigger: 'blur' },
        { type: 'number', min: 0, max: 150, message: '年龄必须在0-150之间', trigger: 'blur' }
      ]
    }
    
    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增用户' : '编辑用户')
    
    // 获取用户列表
    const fetchUsers = async () => {
      // 检查是否已登录
      const token = localStorage.getItem('accessToken')
      if (!token) return
      
      loading.value = true
      try {
        const response = await userApi.getUserPage(currentPage.value, pageSize.value)
        // 检查响应结构并相应处理
        if (response && response.records !== undefined) {
          userList.value = response.records || []
          total.value = response.total || 0
        } else {
          // 如果是简单数组形式，直接赋值
          userList.value = response || []
          total.value = response?.length || 0
        }
      } catch (error) {
        ElMessage.error('获取用户列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索
    const handleSearch = async () => {
      loading.value = true
      try {
        const params = {}
        if (searchForm.name) params.name = searchForm.name
        if (searchForm.age) params.age = searchForm.age
        
        const response = await userApi.getUsersByCondition(params)
        userList.value = response
        total.value = response.length
      } catch (error) {
        ElMessage.error('搜索用户失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }
    
    // 重置搜索
    const resetSearch = () => {
      searchForm.name = ''
      searchForm.age = ''
      fetchUsers()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pageSize.value = size
      fetchUsers()
    }
    
    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchUsers()
    }
    
    // 打开新增对话框
    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(userForm, {
        id: null,
        name: '',
        age: null,
        email: ''
      })
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(userForm, {
        id: row.id,
        name: row.name,
        age: row.age,
        email: row.email
      })
      dialogVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        await userFormRef.value.validate()
        
        let result
        if (dialogType.value === 'add') {
          result = await userApi.addUser(userForm)
        } else {
          result = await userApi.updateUser(userForm)
        }
        
        if (result.includes('成功')) {
          ElMessage.success(dialogType.value === 'add' ? '新增用户成功' : '更新用户成功')
          dialogVisible.value = false
          fetchUsers()
        } else {
          ElMessage.error(dialogType.value === 'add' ? '新增用户失败' : '更新用户失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }
    
    // 删除用户
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此用户吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const result = await userApi.deleteUser(id)
        
        if (result.includes('成功')) {
          ElMessage.success('删除用户成功')
          fetchUsers()
        } else {
          ElMessage.error('删除用户失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    onMounted(() => {
      fetchUsers()
    })
    
    return {
      userList,
      loading,
      total,
      currentPage,
      pageSize,
      searchForm,
      dialogVisible,
      dialogType,
      userFormRef,
      userForm,
      userRules,
      dialogTitle,
      fetchUsers,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>