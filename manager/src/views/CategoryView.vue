<template>
  <div class="category-container">
    <div class="header">
      <h2>商品类目管理</h2>
      <el-button type="primary" @click="openAddDialog">新增类目</el-button>
    </div>

    <el-table
      :data="categoryTableData"
      stripe
      style="width: 100%"
      v-loading="loading"
      row-key="categoryId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="categoryId" label="ID" width="80" />
      <el-table-column prop="categoryName" label="类目名称" />
      <el-table-column prop="deep" label="层级" width="80">
        <template #default="{ row }">
          <el-tag :type="row.deep === 1 ? 'primary' : row.deep === 2 ? 'success' : 'warning'">
            {{ row.deep }}级
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="categorySort" label="排序" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.categoryId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 类目编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="100px">
        <el-form-item label="类目名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入类目名称" />
        </el-form-item>
        <el-form-item label="父级类目">
          <el-tree-select
            v-model="categoryForm.parentId"
            :data="categoryTreeData"
            :props="{ label: 'categoryName', value: 'categoryId', children: 'children' }"
            placeholder="请选择父级类目（不选则为顶级）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.categorySort" :min="0" />
        </el-form-item>
        <el-form-item label="移动端图片">
          <el-input v-model="categoryForm.appImage" placeholder="请输入图片URL" />
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
import { categoryApi } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CategoryView',
  setup() {
    const categoryTableData = ref([])
    const categoryTreeData = ref([])
    const loading = ref(false)

    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const categoryFormRef = ref()
    const categoryForm = reactive({
      categoryId: null,
      categoryName: '',
      parentId: null,
      categorySort: 0,
      appImage: '',
      deep: 1
    })

    const categoryRules = {
      categoryName: [
        { required: true, message: '请输入类目名称', trigger: 'blur' }
      ]
    }

    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增类目' : '编辑类目')

    const buildTree = (data, parentId = 0) => {
      const result = []
      for (const item of data) {
        if (item.parentId === parentId) {
          const children = buildTree(data, item.categoryId)
          if (children.length > 0) {
            item.children = children
          }
          result.push(item)
        }
      }
      return result
    }

    const fetchCategories = async () => {
      loading.value = true
      try {
        const response = await categoryApi.getList()
        if (response.code === 200) {
          const flatData = response.data || []
          categoryTableData.value = buildTree(flatData)
          categoryTreeData.value = [{ categoryId: 0, categoryName: '顶级类目', children: buildTree(flatData) }]
        } else {
          ElMessage.error(response.message || '获取类目列表失败')
        }
      } catch (error) {
        ElMessage.error('获取类目列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(categoryForm, {
        categoryId: null,
        categoryName: '',
        parentId: null,
        categorySort: 0,
        appImage: '',
        deep: 1
      })
      dialogVisible.value = true
    }

    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(categoryForm, {
        categoryId: row.categoryId,
        categoryName: row.categoryName,
        parentId: row.parentId === 0 ? null : row.parentId,
        categorySort: row.categorySort,
        appImage: row.appImage,
        deep: row.deep
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await categoryFormRef.value.validate()
        const submitData = { ...categoryForm }
        if (submitData.parentId === null || submitData.parentId === undefined) {
          submitData.parentId = 0
          submitData.deep = 1
        } else {
          const parent = categoryTreeData.value[0].children.find(c => c.categoryId === submitData.parentId)
          submitData.deep = parent ? (parent.deep + 1) : 1
        }

        let response
        if (dialogType.value === 'add') {
          response = await categoryApi.create(submitData)
        } else {
          response = await categoryApi.update(submitData)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增类目成功' : '更新类目成功')
          dialogVisible.value = false
          fetchCategories()
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
        await ElMessageBox.confirm('确认删除此类目吗？删除后其子类目也会被删除。', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await categoryApi.delete(id)
        if (response.code === 200) {
          ElMessage.success('删除类目成功')
          fetchCategories()
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
      fetchCategories()
    })

    return {
      categoryTableData,
      categoryTreeData,
      loading,
      dialogVisible,
      dialogType,
      categoryFormRef,
      categoryForm,
      categoryRules,
      dialogTitle,
      fetchCategories,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete
    }
  }
}
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
