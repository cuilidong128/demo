<template>
  <div class="admin-container">
    <div class="header">
      <h2>管理员管理</h2>
      <el-button type="primary" @click="openAddDialog">新增管理员</el-button>
    </div>

    <el-table :data="adminList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="adminId" label="ID" width="80" />
      <el-table-column prop="name" label="用户名" />
      <el-table-column prop="groupName" label="所属用户组" />
      <el-table-column prop="isSuper" label="是否超管" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isSuper === 1 ? 'danger' : 'info'">
            {{ row.isSuper === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="handleUnlock(row.adminId)" v-if="row.groupId === null || row.groupId <= 0">解锁</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.adminId)">删除</el-button>
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

    <!-- 管理员编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="100px">
        <el-form-item label="用户名" prop="name">
          <el-input v-model="adminForm.name" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="密码" v-else>
          <el-input v-model="adminForm.password" type="password" placeholder="不修改请留空" />
        </el-form-item>
        <el-form-item label="所属用户组" prop="groupId">
          <el-select v-model="adminForm.groupId" placeholder="请选择用户组" style="width: 100%">
            <el-option
              v-for="group in groupList"
              :key="group.groupId"
              :label="group.groupName"
              :value="group.groupId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否超管" prop="isSuper">
          <el-radio-group v-model="adminForm.isSuper">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
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
import { adminApi } from '@/api/admin'
import { adminGroupApi } from '@/api/adminGroup'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminView',
  setup() {
    // 管理员列表数据
    const adminList = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    // 用户组列表
    const groupList = ref([])

    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('add') // 'add' 或 'edit'
    const adminFormRef = ref()
    const adminForm = reactive({
      adminId: null,
      name: '',
      password: '',
      groupId: null,
      groupName: '',
      isSuper: 0
    })

    const adminRules = {
      name: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur', validator: (rule, value, callback) => {
          if (dialogType.value === 'add' && (!value || value === '')) {
            callback(new Error('请输入密码'))
          } else {
            callback()
          }
        }}
      ],
      groupId: [
        { required: true, message: '请选择用户组', trigger: 'change' }
      ]
    }

    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增管理员' : '编辑管理员')

    // 获取管理员列表
    const fetchAdmins = async () => {
      loading.value = true
      try {
        const response = await adminApi.getAdminPage(currentPage.value, pageSize.value)
        if (response.code === 200) {
          adminList.value = response.data.records || []
          total.value = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取管理员列表失败')
        }
      } catch (error) {
        ElMessage.error('获取管理员列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    // 获取用户组列表
    const fetchGroups = async () => {
      try {
        const response = await adminGroupApi.getGroupList()
        if (response.code === 200) {
          groupList.value = response.data || []
        }
      } catch (error) {
        console.error('获取用户组列表失败:', error)
      }
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pageSize.value = size
      fetchAdmins()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchAdmins()
    }

    // 打开新增对话框
    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(adminForm, {
        adminId: null,
        name: '',
        password: '',
        groupId: null,
        groupName: '',
        isSuper: 0
      })
      dialogVisible.value = true
    }

    // 打开编辑对话框
    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(adminForm, {
        adminId: row.adminId,
        name: row.name,
        password: '',
        groupId: row.groupId,
        groupName: row.groupName,
        isSuper: row.isSuper || 0
      })
      dialogVisible.value = true
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await adminFormRef.value.validate()

        // 查找用户组名称
        const selectedGroup = groupList.value.find(g => g.groupId === adminForm.groupId)
        const submitData = { ...adminForm }
        if (selectedGroup) {
          submitData.groupName = selectedGroup.groupName
        }

        // 如果密码为空且是编辑模式，不提交密码字段
        if (dialogType.value === 'edit' && !submitData.password) {
          delete submitData.password
        }

        let response
        if (dialogType.value === 'add') {
          response = await adminApi.createAdmin(submitData)
        } else {
          response = await adminApi.updateAdmin(submitData)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增管理员成功' : '更新管理员成功')
          dialogVisible.value = false
          fetchAdmins()
        } else {
          ElMessage.error(response.message || (dialogType.value === 'add' ? '新增管理员失败' : '更新管理员失败'))
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    // 删除管理员
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此管理员吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await adminApi.deleteAdmin(id)

        if (response.code === 200) {
          ElMessage.success('删除管理员成功')
          fetchAdmins()
        } else {
          ElMessage.error(response.message || '删除管理员失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 解锁管理员
    const handleUnlock = async (id) => {
      try {
        const response = await adminApi.unlockAdmin(id)
        if (response.code === 200) {
          ElMessage.success('解锁管理员账户成功')
          fetchAdmins()
        } else {
          ElMessage.error(response.message || '解锁管理员账户失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('解锁失败')
      }
    }

    onMounted(() => {
      fetchAdmins()
      fetchGroups()
    })

    return {
      adminList,
      loading,
      total,
      currentPage,
      pageSize,
      groupList,
      dialogVisible,
      dialogType,
      adminFormRef,
      adminForm,
      adminRules,
      dialogTitle,
      fetchAdmins,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      handleUnlock
    }
  }
}
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
