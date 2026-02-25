<template>
  <div class="album-container">
    <div class="header">
      <h2>相册管理</h2>
      <el-button type="primary" @click="openAddDialog">新增相册</el-button>
    </div>

    <el-table
      :data="albumTableData"
      stripe
      style="width: 100%"
      v-loading="loading"
      row-key="albumId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="albumId" label="ID" width="80" />
      <el-table-column prop="albumName" label="相册名称" />
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="info" @click="openFilesDialog(row)">查看文件</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.albumId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 相册编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="albumForm" :rules="albumRules" ref="albumFormRef" label-width="100px">
        <el-form-item label="相册名称" prop="albumName">
          <el-input v-model="albumForm.albumName" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="父级相册">
          <el-tree-select
            v-model="albumForm.parentId"
            :data="albumTreeData"
            :props="{ label: 'albumName', value: 'albumId', children: 'children' }"
            placeholder="请选择父级相册（不选则为顶级）"
            clearable
            check-strictly
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 文件管理对话框 -->
    <el-dialog title="相册文件管理" v-model="filesDialogVisible" width="800px">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" size="small" @click="openAddFileDialog">上传文件</el-button>
      </div>
      <el-table :data="filesTableData" stripe style="width: 100%">
        <el-table-column prop="filesId" label="ID" width="80" />
        <el-table-column label="预览" width="120">
          <template #default="{ row }">
            <el-image 
              :src="getImageUrl(row.filesName)" 
              :preview-src-list="[getImageUrl(row.filesName)]"
              style="width: 60px; height: 60px; object-fit: cover;"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="originalName" label="文件名" />
        <el-table-column prop="filesSize" label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.filesSize) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDeleteFile(row.filesId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 文件上传对话框 -->
    <el-dialog title="上传文件" v-model="fileUploadDialogVisible" width="500px">
      <el-form :model="fileForm" ref="fileFormRef" label-width="100px">
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="原始文件名">
          <el-input v-model="fileForm.originalName" placeholder="请输入原始文件名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="fileUploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleFileSubmit">上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { albumApi } from '@/api/album'
import { fileApi } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AlbumView',
  setup() {
    const albumTableData = ref([])
    const albumTreeData = ref([])
    const loading = ref(false)

    const dialogVisible = ref(false)
    const dialogTitle = ref('新增相册')
    const dialogType = ref('add')
    const albumFormRef = ref()
    const albumForm = reactive({
      albumId: null,
      albumName: '',
      parentId: 0
    })

    const albumRules = {
      albumName: [
        { required: true, message: '请输入相册名称', trigger: 'blur' }
      ]
    }

    // 文件管理
    const filesDialogVisible = ref(false)
    const filesTableData = ref([])
    const currentAlbumId = ref(null)
    
    const fileUploadDialogVisible = ref(false)
    const fileFormRef = ref()
    const uploadRef = ref()
    const fileForm = reactive({
      albumId: null,
      filesName: '',
      originalName: '',
      filesSize: 0,
      file: null
    })

    const buildTree = (data, parentId = 0) => {
      const result = []
      for (const item of data) {
        if (item.parentId === parentId) {
          const children = buildTree(data, item.albumId)
          if (children.length > 0) {
            item.children = children
          }
          result.push(item)
        }
      }
      return result
    }

    const fetchAlbums = async () => {
      loading.value = true
      try {
        const response = await albumApi.getList()
        if (response.code === 200) {
          const flatData = response.data || []
          albumTableData.value = buildTree(flatData)
          albumTreeData.value = [{ albumId: 0, albumName: '顶级相册', children: buildTree(flatData) }]
        } else {
          ElMessage.error(response.message || '获取相册列表失败')
        }
      } catch (error) {
        ElMessage.error('获取相册列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const openAddDialog = () => {
      dialogType.value = 'add'
      dialogTitle.value = '新增相册'
      Object.assign(albumForm, {
        albumId: null,
        albumName: '',
        parentId: 0
      })
      dialogVisible.value = true
    }

    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      dialogTitle.value = '编辑相册'
      Object.assign(albumForm, {
        albumId: row.albumId,
        albumName: row.albumName,
        parentId: row.parentId || 0
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await albumFormRef.value.validate()
        const submitData = { ...albumForm }
        if (submitData.parentId === null || submitData.parentId === undefined) {
          submitData.parentId = 0
        }

        let response
        if (dialogType.value === 'add') {
          response = await albumApi.create(submitData)
        } else {
          response = await albumApi.update(submitData)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增相册成功' : '更新相册成功')
          dialogVisible.value = false
          fetchAlbums()
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
        await ElMessageBox.confirm('确认删除此相册吗？删除后其子相册也会被删除。', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await albumApi.delete(id)
        if (response.code === 200) {
          ElMessage.success('删除相册成功')
          fetchAlbums()
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

    // 文件管理
    const openFilesDialog = async (row) => {
      currentAlbumId.value = row.albumId
      filesDialogVisible.value = true
      await fetchFiles(row.albumId)
    }

    const fetchFiles = async (albumId) => {
      try {
        const response = await albumApi.getFilesByAlbumId(albumId)
        if (response.code === 200) {
          filesTableData.value = response.data || []
        }
      } catch (error) {
        console.error(error)
      }
    }

    const formatFileSize = (size) => {
      if (!size) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB']
      let index = 0
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024
        index++
      }
      return size.toFixed(2) + ' ' + units[index]
    }

    // 获取图片完整URL
    const getImageUrl = (relativePath) => {
      return fileApi.buildFullUrl(relativePath)
    }

    const openAddFileDialog = () => {
      Object.assign(fileForm, {
        albumId: currentAlbumId.value,
        filesName: '',
        originalName: '',
        filesSize: 0,
        file: null
      })
      fileUploadDialogVisible.value = true
    }

    const handleFileChange = (file) => {
      fileForm.file = file.raw
      fileForm.originalName = file.name
      fileForm.filesSize = file.size
    }

    const handleFileSubmit = async () => {
      if (!fileForm.file) {
        ElMessage.error('请选择文件')
        return
      }

      try {
        // 1. 先调用文件上传接口
        const uploadResponse = await fileApi.upload(fileForm.file, 'local', 'album')
        if (uploadResponse.code !== 200) {
          ElMessage.error(uploadResponse.message || '文件上传失败')
          return
        }

        // 2. 保存文件记录到相册（使用相对路径）
        const result = uploadResponse.data
        const submitData = {
          albumId: currentAlbumId.value,
          filesName: result.relativePath,  // 保存相对路径到数据库
          originalName: fileForm.originalName || fileForm.file.name,
          filesSize: fileForm.filesSize
        }
        const response = await albumApi.createFile(submitData)
        if (response.code === 200) {
          ElMessage.success('文件上传成功')
          fileUploadDialogVisible.value = false
          // 清空上传组件
          uploadRef.value?.clearFiles()
          fetchFiles(currentAlbumId.value)
        } else {
          ElMessage.error(response.message || '保存文件记录失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('上传失败')
      }
    }

    const handleDeleteFile = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此文件吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await albumApi.deleteFile(id)
        if (response.code === 200) {
          ElMessage.success('删除文件成功')
          fetchFiles(currentAlbumId.value)
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
      fetchAlbums()
    })

    return {
      albumTableData,
      albumTreeData,
      loading,
      dialogVisible,
      dialogTitle,
      albumFormRef,
      albumForm,
      albumRules,
      filesDialogVisible,
      filesTableData,
      fileUploadDialogVisible,
      fileFormRef,
      uploadRef,
      fileForm,
      fetchAlbums,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      openFilesDialog,
      openAddFileDialog,
      handleFileChange,
      handleFileSubmit,
      handleDeleteFile,
      formatFileSize,
      getImageUrl
    }
  }
}
</script>

<style scoped>
.album-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
