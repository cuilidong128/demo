<template>
  <div class="attribute-container">
    <div class="header">
      <h2>商品属性管理</h2>
      <el-button type="primary" @click="openAddDialog">新增属性</el-button>
    </div>

    <el-table
      :data="attributeTableData"
      stripe
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="attributeId" label="ID" width="80" />
      <el-table-column prop="attributeName" label="属性名称" />
      <el-table-column prop="attributeSort" label="排序" width="80" />
      <el-table-column prop="isShow" label="是否显示" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isShow === 1 ? 'success' : 'info'">
            {{ row.isShow === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="info" @click="openValueDialog(row)">属性值</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.attributeId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 属性编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="attributeForm" :rules="attributeRules" ref="attributeFormRef" label-width="100px">
        <el-form-item label="属性名称" prop="attributeName">
          <el-input v-model="attributeForm.attributeName" placeholder="请输入属性名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="attributeForm.attributeSort" :min="0" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-radio-group v-model="attributeForm.isShow">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
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

    <!-- 属性值管理对话框 -->
    <el-dialog title="属性值管理" v-model="valueDialogVisible" width="600px">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" size="small" @click="openAddValueDialog">新增属性值</el-button>
      </div>
      <el-table :data="valueTableData" stripe style="width: 100%">
        <el-table-column prop="attributeValueId" label="ID" width="80" />
        <el-table-column prop="attributeValueName" label="属性值名称" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="openEditValueDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteValue(row.attributeValueId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 属性值编辑对话框 -->
    <el-dialog :title="valueDialogTitle" v-model="valueEditDialogVisible" width="500px">
      <el-form :model="valueForm" :rules="valueRules" ref="valueFormRef" label-width="100px">
        <el-form-item label="属性值名称" prop="attributeValueName">
          <el-input v-model="valueForm.attributeValueName" placeholder="请输入属性值名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="valueEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleValueSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { attributeApi } from '@/api/attribute'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AttributeView',
  setup() {
    const attributeTableData = ref([])
    const loading = ref(false)

    const dialogVisible = ref(false)
    const dialogTitle = ref('新增属性')
    const dialogType = ref('add')
    const attributeFormRef = ref()
    const attributeForm = reactive({
      attributeId: null,
      attributeName: '',
      attributeSort: 0,
      isShow: 1
    })

    const attributeRules = {
      attributeName: [
        { required: true, message: '请输入属性名称', trigger: 'blur' }
      ]
    }

    // 属性值管理
    const valueDialogVisible = ref(false)
    const valueTableData = ref([])
    const currentAttributeId = ref(null)
    
    const valueEditDialogVisible = ref(false)
    const valueDialogTitle = ref('新增属性值')
    const valueDialogType = ref('add')
    const valueFormRef = ref()
    const valueForm = reactive({
      attributeValueId: null,
      attributeId: null,
      attributeValueName: ''
    })
    const valueRules = {
      attributeValueName: [
        { required: true, message: '请输入属性值名称', trigger: 'blur' }
      ]
    }

    const fetchAttributes = async () => {
      loading.value = true
      try {
        const response = await attributeApi.getList()
        if (response.code === 200) {
          attributeTableData.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取属性列表失败')
        }
      } catch (error) {
        ElMessage.error('获取属性列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const openAddDialog = () => {
      dialogType.value = 'add'
      dialogTitle.value = '新增属性'
      Object.assign(attributeForm, {
        attributeId: null,
        attributeName: '',
        attributeSort: 0,
        isShow: 1
      })
      dialogVisible.value = true
    }

    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      dialogTitle.value = '编辑属性'
      Object.assign(attributeForm, {
        attributeId: row.attributeId,
        attributeName: row.attributeName,
        attributeSort: row.attributeSort,
        isShow: row.isShow
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await attributeFormRef.value.validate()
        let response
        if (dialogType.value === 'add') {
          response = await attributeApi.create(attributeForm)
        } else {
          response = await attributeApi.update(attributeForm)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增属性成功' : '更新属性成功')
          dialogVisible.value = false
          fetchAttributes()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此属性吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await attributeApi.delete(id)
        if (response.code === 200) {
          ElMessage.success('删除属性成功')
          fetchAttributes()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 属性值管理
    const openValueDialog = async (row) => {
      currentAttributeId.value = row.attributeId
      valueDialogVisible.value = true
      await fetchValues(row.attributeId)
    }

    const fetchValues = async (attributeId) => {
      try {
        const response = await attributeApi.getValuesByAttributeId(attributeId)
        if (response.code === 200) {
          valueTableData.value = response.data || []
        }
      } catch (error) {
        console.error(error)
      }
    }

    const openAddValueDialog = () => {
      valueDialogType.value = 'add'
      valueDialogTitle.value = '新增属性值'
      Object.assign(valueForm, {
        attributeValueId: null,
        attributeId: currentAttributeId.value,
        attributeValueName: ''
      })
      valueEditDialogVisible.value = true
    }

    const openEditValueDialog = (row) => {
      valueDialogType.value = 'edit'
      valueDialogTitle.value = '编辑属性值'
      Object.assign(valueForm, {
        attributeValueId: row.attributeValueId,
        attributeId: currentAttributeId.value,
        attributeValueName: row.attributeValueName
      })
      valueEditDialogVisible.value = true
    }

    const handleValueSubmit = async () => {
      try {
        await valueFormRef.value.validate()
        let response
        if (valueDialogType.value === 'add') {
          response = await attributeApi.createValue(valueForm)
        } else {
          response = await attributeApi.updateValue(valueForm)
        }

        if (response.code === 200) {
          ElMessage.success(valueDialogType.value === 'add' ? '新增属性值成功' : '更新属性值成功')
          valueEditDialogVisible.value = false
          fetchValues(currentAttributeId.value)
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    const handleDeleteValue = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此属性值吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await attributeApi.deleteValue(id)
        if (response.code === 200) {
          ElMessage.success('删除属性值成功')
          fetchValues(currentAttributeId.value)
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }

    onMounted(() => {
      fetchAttributes()
    })

    return {
      attributeTableData,
      loading,
      dialogVisible,
      dialogTitle,
      attributeFormRef,
      attributeForm,
      attributeRules,
      valueDialogVisible,
      valueTableData,
      valueEditDialogVisible,
      valueDialogTitle,
      valueFormRef,
      valueForm,
      valueRules,
      fetchAttributes,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      openValueDialog,
      openAddValueDialog,
      openEditValueDialog,
      handleValueSubmit,
      handleDeleteValue
    }
  }
}
</script>

<style scoped>
.attribute-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
