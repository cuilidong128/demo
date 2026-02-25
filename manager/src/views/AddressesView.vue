<template>
  <div class="addresses-container">
    <div class="header">
      <h2>地址管理</h2>
      <el-button type="primary" @click="openAddDialog">新增地址</el-button>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="地址">
          <el-input v-model="searchForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="会员ID">
          <el-input v-model="searchForm.memberId" placeholder="请输入会员ID" type="number" />
        </el-form-item>
        <el-form-item label="是否默认">
          <el-select v-model="searchForm.isDefault" placeholder="请选择">
            <el-option label="全部" value="" />
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="addressList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="addressId" label="ID" width="80" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="areaInfo" label="地区信息" width="150" />
      <el-table-column prop="memberId" label="会员ID" width="100" />
      <el-table-column prop="isDefault" label="是否默认" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isDefault === 1 ? 'success' : 'info'">
            {{ row.isDefault === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.addressId)">删除</el-button>
          <el-button 
            size="small" 
            type="primary" 
            @click="handleSetDefault(row.addressId, row.memberId)"
            :disabled="row.isDefault === 1"
          >
            设为默认
          </el-button>
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

    <!-- 地址编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="地址" prop="address">
          <el-input v-model="addressForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="地区信息" prop="areaInfo">
          <el-input v-model="addressForm.areaInfo" placeholder="请输入地区信息，如：北京市/朝阳区" />
        </el-form-item>
        <el-form-item label="会员ID" prop="memberId">
          <el-input v-model.number="addressForm.memberId" type="number" placeholder="请输入会员ID" />
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="addressForm.isDefault">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
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
import { addressApi } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AddressesView',
  setup() {
    // 地址列表数据
    const addressList = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    
    // 搜索表单
    const searchForm = reactive({
      address: '',
      memberId: '',
      isDefault: ''
    })
    
    // 对话框相关
    const dialogVisible = ref(false)
    const dialogType = ref('add') // 'add' 或 'edit'
    const addressFormRef = ref()
    const addressForm = reactive({
      addressId: null,
      address: '',
      areaInfo: '',
      memberId: null,
      isDefault: 0
    })
    
    const addressRules = {
      address: [
        { required: true, message: '请输入地址', trigger: 'blur' }
      ],
      memberId: [
        { required: true, message: '请输入会员ID', trigger: 'blur' },
        { type: 'number', min: 1, message: '会员ID必须大于0', trigger: 'blur' }
      ]
    }
    
    const dialogTitle = computed(() => dialogType.value === 'add' ? '新增地址' : '编辑地址')
    
    // 获取地址列表
    const fetchAddresses = async () => {
      loading.value = true
      try {
        const response = await addressApi.getAddressPage(currentPage.value, pageSize.value)
        // 检查响应结构并相应处理
        if (response && response.records !== undefined) {
          addressList.value = response.records || []
          total.value = response.total || 0
        } else {
          // 如果是简单数组形式，直接赋值
          addressList.value = response || []
          total.value = response?.length || 0
        }
      } catch (error) {
        ElMessage.error('获取地址列表失败')
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
        if (searchForm.address) params.address = searchForm.address
        if (searchForm.memberId) params.memberId = searchForm.memberId
        if (searchForm.isDefault !== '') params.isDefault = searchForm.isDefault
        
        const response = await addressApi.getAddressesByCondition(params)
        addressList.value = response
        total.value = response.length
      } catch (error) {
        ElMessage.error('搜索地址失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }
    
    // 重置搜索
    const resetSearch = () => {
      searchForm.address = ''
      searchForm.memberId = ''
      searchForm.isDefault = ''
      fetchAddresses()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pageSize.value = size
      fetchAddresses()
    }
    
    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchAddresses()
    }
    
    // 打开新增对话框
    const openAddDialog = () => {
      dialogType.value = 'add'
      Object.assign(addressForm, {
        addressId: null,
        address: '',
        areaInfo: '',
        memberId: null,
        isDefault: 0
      })
      dialogVisible.value = true
    }
    
    // 打开编辑对话框
    const openEditDialog = (row) => {
      dialogType.value = 'edit'
      Object.assign(addressForm, {
        addressId: row.addressId,
        address: row.address,
        areaInfo: row.areaInfo,
        memberId: row.memberId,
        isDefault: row.isDefault
      })
      dialogVisible.value = true
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        await addressFormRef.value.validate()
        
        let result
        if (dialogType.value === 'add') {
          result = await addressApi.addAddress(addressForm)
        } else {
          result = await addressApi.updateAddress(addressForm)
        }
        
        if (result.includes('成功')) {
          ElMessage.success(dialogType.value === 'add' ? '新增地址成功' : '更新地址成功')
          dialogVisible.value = false
          fetchAddresses()
        } else {
          ElMessage.error(dialogType.value === 'add' ? '新增地址失败' : '更新地址失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }
    
    // 删除地址
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此地址吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const result = await addressApi.deleteAddress(id)
        
        if (result.includes('成功')) {
          ElMessage.success('删除地址成功')
          fetchAddresses()
        } else {
          ElMessage.error('删除地址失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 设为默认地址
    const handleSetDefault = async (addressId, memberId) => {
      try {
        await ElMessageBox.confirm('确认设为默认地址吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const result = await addressApi.setDefaultAddress(addressId, memberId)
        
        if (result.includes('成功')) {
          ElMessage.success('设置默认地址成功')
          fetchAddresses()
        } else {
          ElMessage.error('设置默认地址失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
          ElMessage.error('操作失败')
        }
      }
    }
    
    onMounted(() => {
      fetchAddresses()
    })
    
    return {
      addressList,
      loading,
      total,
      currentPage,
      pageSize,
      searchForm,
      dialogVisible,
      dialogType,
      addressFormRef,
      addressForm,
      addressRules,
      dialogTitle,
      fetchAddresses,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      handleSubmit,
      handleDelete,
      handleSetDefault
    }
  }
}
</script>

<style scoped>
.addresses-container {
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