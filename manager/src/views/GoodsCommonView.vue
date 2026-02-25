<template>
  <div class="goods-common-container">
    <div class="header">
      <h2>商品管理</h2>
      <el-button type="primary" @click="openAddDialog">新增商品</el-button>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" width="800px" :show-close="true" class="image-preview-dialog">
      <div class="image-preview-container">
        <el-image :src="previewImageUrl" fit="contain" style="width: 100%; max-height: 600px;" />
      </div>
    </el-dialog>

    <el-table
      :data="goodsTableData"
      stripe
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="commonId" label="SPU ID" width="80" />
      <el-table-column prop="goodsName" label="商品名称" />
      <el-table-column prop="goodsPrice" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.goodsPrice }}
        </template>
      </el-table-column>
      <el-table-column prop="goodsStorage" label="库存" width="80" />
      <el-table-column prop="goodsState" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.goodsState === 1 ? 'success' : 'danger'">
            {{ row.goodsState === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.commonId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; justify-content: flex-end;"
    />

    <!-- 商品编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="900px" :close-on-click-modal="false">
      <el-form :model="goodsForm" :rules="goodsRules" ref="goodsFormRef" label-width="100px">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="goodsForm.goodsName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品单位">
          <el-input v-model="goodsForm.unitName" placeholder="请输入商品单位，如：件、个、套" />
        </el-form-item>
        
        <!-- 类目选择 -->
        <el-form-item label="商品类目">
          <el-cascader
            v-model="selectedCategories"
            :options="categoryTree"
            :props="cascaderProps"
            placeholder="请选择商品类目（可多选）"
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags="3"
            style="width: 100%;"
          />
        </el-form-item>

        <!-- 商品轮播图 -->
        <el-form-item label="商品轮播图">
          <div class="carousel-images">
            <div class="carousel-list">
              <div v-for="(img, index) in goodsForm.goodsImages" :key="index" class="carousel-item">
                <el-image :src="getFullImageUrl(img)" style="width: 100px; height: 100px; object-fit: cover;" />
                <div class="carousel-actions">
                  <el-button type="danger" size="small" text @click="removeCarouselImage(index)">删除</el-button>
                  <el-button v-if="index > 0" type="primary" size="small" text @click="moveCarouselImage(index, -1)">上移</el-button>
                  <el-button v-if="index < goodsForm.goodsImages.length - 1" type="primary" size="small" text @click="moveCarouselImage(index, 1)">下移</el-button>
                </div>
              </div>
              <div class="carousel-add">
                <el-button type="primary" @click="openCarouselSelector">从相册选择</el-button>
              </div>
            </div>
          </div>
        </el-form-item>

        <!-- 规格设置 -->
        <el-divider content-position="left">规格设置</el-divider>
        <div class="spec-section">
          <div v-for="(spec, index) in specList" :key="index" class="spec-item">
            <el-card shadow="hover" class="spec-card">
              <div class="spec-header">
                <span class="spec-title">规格 {{ index + 1 }}</span>
                <el-button type="danger" size="small" text @click="removeSpec(index)">删除</el-button>
              </div>
              <el-row :gutter="10" class="spec-row">
                <el-col :span="8">
                  <el-form-item label="规格名称">
                    <el-select
                      v-model="spec.name"
                      placeholder="选择或输入规格"
                      filterable
                      allow-create
                      default-first-option
                      @change="(val) => handleAttributeChange(val, index)"
                      style="width: 100%;"
                    >
                      <el-option
                        v-for="attr in attributeList"
                        :key="attr.attributeId"
                        :label="attr.attributeName"
                        :value="attr.attributeName"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="16">
                  <el-form-item label="规格值">
                    <div class="spec-values">
                      <el-checkbox-group v-model="spec.selectedValues">
                        <el-checkbox
                          v-for="val in spec.valueOptions"
                          :key="val.attributeValueId"
                          :value="typeof val.attributeValueName === 'object' ? (val.attributeValueName?.name || val.attributeValueName?.label || JSON.stringify(val.attributeValueName)) : String(val.attributeValueName)"
                        >
                          <div class="spec-value-item">
                            <el-image
                              v-if="val.imageUrl"
                              :src="getFullImageUrl(val.imageUrl)"
                              style="width: 20px; height: 20px; object-fit: cover; margin-right: 4px;"
                            />
                            {{ typeof val.attributeValueName === 'object' ? (val.attributeValueName?.name || val.attributeValueName?.label || JSON.stringify(val.attributeValueName)) : val.attributeValueName }}
                          </div>
                        </el-checkbox>
                      </el-checkbox-group>
                      <div class="custom-value-input">
                        <el-input
                          v-model="spec.customValue"
                          placeholder="自定义规格值"
                          size="small"
                          style="width: 150px;"
                        >
                          <template #append>
                            <el-button @click="addCustomValue(index)">添加</el-button>
                          </template>
                        </el-input>
                        <el-button
                          type="primary"
                          size="small"
                          @click="openSpecImageSelector(index)"
                          style="margin-left: 10px;"
                        >
                          选择图片
                        </el-button>
                      </div>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>
          </div>
          <div class="spec-actions">
            <el-button type="primary" size="small" @click="addSpec">添加规格</el-button>
            <el-button type="success" size="small" @click="generateSkus" :disabled="specList.length === 0">生成SKU</el-button>
          </div>
        </div>

        <!-- SKU列表 -->
        <el-divider content-position="left">SKU列表</el-divider>
        <el-table :data="goodsForm.skuList" stripe size="small" v-if="goodsForm.skuList && goodsForm.skuList.length > 0">
          <el-table-column prop="goodsSpecs" label="规格" min-width="150" />
          <el-table-column label="图片" width="80" align="center">
            <template #default="{ row }">
              <div v-if="row.imageName" class="sku-image">
                <el-image :src="getFullImageUrl(row.imageName)" style="width: 40px; height: 40px; object-fit: cover;" />
              </div>
              <el-button v-else type="text" size="small" @click="openSkuImageSelector(row)">选择</el-button>
            </template>
          </el-table-column>
          <el-table-column label="售价" width="110">
            <template #default="{ row }">
              <el-input-number v-model="row.goodsPrice" :min="0" :precision="2" :controls="false" size="small" style="width: 90px;" />
            </template>
          </el-table-column>
          <el-table-column label="成本价" width="110">
            <template #default="{ row }">
              <el-input-number v-model="row.goodsCostPrice" :min="0" :precision="2" :controls="false" size="small" style="width: 90px;" />
            </template>
          </el-table-column>
          <el-table-column label="划线价" width="110">
            <template #default="{ row }">
              <el-input-number v-model="row.goodsMarketPrice" :min="0" :precision="2" :controls="false" size="small" style="width: 90px;" />
            </template>
          </el-table-column>
          <el-table-column label="库存" width="90">
            <template #default="{ row }">
              <el-input-number v-model="row.goodsStorage" :min="0" :controls="false" size="small" style="width: 70px;" />
            </template>
          </el-table-column>
          <el-table-column label="货号" width="120">
            <template #default="{ row }">
              <el-input v-model="row.goodsSerial" size="small" placeholder="货号" style="width: 100px;" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row, $index }">
              <el-button type="danger" size="small" text @click="goodsForm.skuList.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="请添加规格并生成SKU" :image-size="60" />

        <!-- 商品详情 -->
        <el-divider content-position="left">商品详情</el-divider>
        <el-form-item label="PC端详情">
          <div class="editor-container">
            <QuillEditor
              v-model:content="goodsForm.goodsBody"
              contentType="html"
              theme="snow"
              :options="quillOptions"
              style="height: 300px;"
            />
          </div>
        </el-form-item>
        <el-form-item label="移动端详情">
          <div class="editor-container">
            <QuillEditor
              v-model:content="goodsForm.mobileBody"
              contentType="html"
              theme="snow"
              :options="mobileQuillOptions"
              style="height: 300px;"
            />
          </div>
        </el-form-item>

        <!-- 其他设置 -->
        <el-divider content-position="left">其他设置</el-divider>
        <el-form-item label="商品状态">
          <el-radio-group v-model="goodsForm.goodsState">
            <el-radio :value="0">下架</el-radio>
            <el-radio :value="1">上架</el-radio>
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

    <!-- SKU编辑对话框 -->
    <el-dialog :title="skuDialogTitle" v-model="skuDialogVisible" width="600px">
      <el-form :model="skuForm" :rules="skuRules" ref="skuFormRef" label-width="100px">
        <el-form-item label="规格" prop="goodsSpecs">
          <el-input v-model="skuForm.goodsSpecs" placeholder="如：颜色:黑色;内存:128G" />
        </el-form-item>
        <el-form-item label="完整规格">
          <el-input v-model="skuForm.goodsFullSpecs" placeholder="完整规格描述" />
        </el-form-item>
        <el-form-item label="价格" prop="goodsPrice">
          <el-input-number v-model="skuForm.goodsPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="市场价">
          <el-input-number v-model="skuForm.goodsMarketPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="goodsStorage">
          <el-input-number v-model="skuForm.goodsStorage" :min="0" />
        </el-form-item>
        <el-form-item label="货号">
          <el-input v-model="skuForm.goodsSerial" placeholder="商品货号" />
        </el-form-item>
        <el-form-item label="条形码">
          <el-input v-model="skuForm.goodsBarcode" placeholder="商品条形码" />
        </el-form-item>
        <el-form-item label="是否默认">
          <el-radio-group v-model="skuForm.isDefault">
            <el-radio :value="0">否</el-radio>
            <el-radio :value="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="skuDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSkuSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 相册选择器对话框 -->
    <el-dialog 
      title="选择图片" 
      v-model="albumDialogVisible" 
      width="900px" 
      :close-on-click-modal="false"
      class="album-selector-dialog"
    >
      <div class="album-selector">
        <!-- 左侧目录树 -->
        <div class="album-sidebar">
          <div class="sidebar-header">图片目录</div>
          <el-tree
            :data="albumTreeWithSystem"
            :props="{ label: 'albumName', value: 'albumId', children: 'children' }"
            @node-click="handleAlbumSelect"
            highlight-current
            node-key="albumId"
            :default-expanded-keys="[0, -1, -2]"
            :current-node-key="currentAlbumId"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <span class="node-label">{{ node.label }}</span>
                <!-- 只有真实相册（非系统目录）显示操作菜单 -->
                <el-dropdown 
                  v-if="data.albumId > 0" 
                  trigger="hover" 
                  @command="(cmd) => handleAlbumNodeMenuCommand(cmd, data)"
                  @click.stop
                >
                  <span class="node-menu-trigger">...</span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="add">新增相册</el-dropdown-item>
                      <el-dropdown-item command="edit">编辑相册</el-dropdown-item>
                      <el-dropdown-item command="delete">删除相册</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-tree>
        </div>
        
        <!-- 右侧内容区 -->
        <div class="album-content">
          <!-- 工具栏 -->
          <div class="album-toolbar">
            <div class="toolbar-left">
              <el-input
                v-model="imageSearchKeyword"
                placeholder="搜索图片名称"
                clearable
                style="width: 200px;"
                @keyup.enter="handleImageSearch"
                @clear="handleImageSearch"
              >
                <template #append>
                  <el-button @click="handleImageSearch">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
            <div class="toolbar-right">
              <el-button 
                type="primary" 
                @click="confirmImageSelect"
                :disabled="selectedImages.length === 0"
              >
                使用选中图片 ({{ selectedImages.length }})
              </el-button>
              <el-button 
                type="danger" 
                @click="handleBatchDelete"
                :disabled="selectedImages.length === 0"
              >
                删除图片
              </el-button>
              <!-- 转移到输入框，点击弹出树形选择对话框 -->
              <el-input
                v-model="transferTargetName"
                placeholder="转移到"
                readonly
                :disabled="selectedImages.length === 0"
                style="width: 150px;"
                @click="openTransferDialog"
              >
                <template #suffix>
                  <el-icon><arrow-down /></el-icon>
                </template>
              </el-input>
            </div>
          </div>
          
          <!-- 图片网格 -->
          <div class="image-grid-container" v-loading="albumLoading">
            <div class="image-grid" v-if="albumFiles.length > 0">
              <div
                v-for="(file, index) in albumFiles"
                :key="file.filesId"
                class="image-item"
                :class="{ 
                  selected: isImageSelected(file),
                  'select-order': getImageOrder(file) > 0
                }"
                @click="toggleImageSelect(file)"
              >
                <div class="image-wrapper">
                  <el-image 
                    :src="getFullImageUrl(file.filesName)" 
                    style="width: 100%; height: 100%; object-fit: cover;"
                    :preview-src-list="[]"
                  />
                  <!-- 选中序号 -->
                  <div v-if="getImageOrder(file) > 0" class="select-order-badge">
                    {{ getImageOrder(file) }}
                  </div>
                  <!-- 悬浮操作栏 -->
                  <div class="image-actions">
                    <el-button link size="small" @click.stop="handleViewImage(file)">查看</el-button>
                    <el-button link size="small" @click.stop="handleRenameImage(file)">改名</el-button>
                    <el-button link size="small" type="danger" @click.stop="handleDeleteSingle(file)">删除</el-button>
                  </div>
                </div>
                <!-- 图片名称 -->
                <div class="image-name" :title="file.originalName">
                  {{ file.originalName }}
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无图片" :image-size="100" />
          </div>
          
          <!-- 分页 -->
          <div class="album-pagination" v-if="albumTotal > 0">
            <el-pagination
              v-model:current-page="albumPageNum"
              v-model:page-size="albumPageSize"
              :page-sizes="[12, 24, 48]"
              layout="total, sizes, prev, pager, next"
              :total="albumTotal"
              @size-change="handleAlbumSizeChange"
              @current-change="handleAlbumPageChange"
            />
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 重命名对话框 -->
    <el-dialog title="重命名图片" v-model="renameDialogVisible" width="400px">
      <el-form :model="renameForm" label-width="80px">
        <el-form-item label="新名称">
          <el-input v-model="renameForm.newName" placeholder="请输入新名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renameDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRename">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 转移到对话框（树形选择） -->
    <el-dialog 
      title="选择目标相册" 
      v-model="transferDialogVisible" 
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="transfer-tree-container">
        <!-- 左侧一级目录 -->
        <div class="transfer-tree-left">
          <div class="tree-title">一级目录</div>
          <div class="tree-content">
            <div
              v-for="album in firstLevelAlbums"
              :key="album.albumId"
              class="tree-item"
              :class="{ active: selectedFirstLevel?.albumId === album.albumId }"
              @click="selectFirstLevel(album)"
            >
              <el-icon><folder /></el-icon>
              <span>{{ album.albumName }}</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧二级目录 -->
        <div class="transfer-tree-right">
          <div class="tree-title">二级目录</div>
          <div class="tree-content" v-if="secondLevelAlbums.length > 0">
            <div
              v-for="album in secondLevelAlbums"
              :key="album.albumId"
              class="tree-item"
              :class="{ 
                active: selectedTargetAlbum?.albumId === album.albumId,
                disabled: album.albumId === currentAlbumId || album.isSystem
              }"
              @click="selectTargetAlbum(album)"
            >
              <el-icon><folder-opened /></el-icon>
              <span>{{ album.albumName }}</span>
              <el-tag v-if="album.albumId === currentAlbumId" size="small" type="info" class="current-tag">当前</el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无子目录" :image-size="60" />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="transferDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmTransfer"
            :disabled="!selectedTargetAlbum"
          >
            确定转移
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 相册编辑对话框（新增/编辑共用） -->
    <el-dialog 
      :title="albumEditDialogTitle" 
      v-model="albumEditDialogVisible" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="albumEditForm" :rules="albumEditRules" ref="albumEditFormRef" label-width="100px">
        <el-form-item label="相册名称" prop="albumName">
          <el-input v-model="albumEditForm.albumName" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="父级相册">
          <el-tree-select
            v-model="albumEditForm.parentId"
            :data="albumTreeForSelect"
            :props="{ label: 'albumName', value: 'albumId', children: 'children' }"
            placeholder="请选择父级相册（不选则为顶级）"
            clearable
            check-strictly
            :disabled="albumEditType === 'edit'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="albumEditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAlbumEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { goodsCommonApi } from '@/api/goodsCommon'
import { categoryApi } from '@/api/category'
import { albumApi } from '@/api/album'
import { attributeApi } from '@/api/attribute'
import { fileApi } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowDown, Folder, FolderOpened } from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

export default {
  name: 'GoodsCommonView',
  components: {
    QuillEditor
  },
  setup() {
    const goodsTableData = ref([])
    const loading = ref(false)
    const pageNum = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const goodsFormRef = ref()
    const goodsForm = reactive({
      commonId: null,
      goodsName: '',
      unitName: '',
      categoryId: null,
      categoryId1: null,
      categoryId2: null,
      categoryId3: null,
      goodsImages: [],
      goodsPrice: 0,
      goodsMarketPrice: 0,
      goodsCostPrice: 0,
      goodsStorage: 0,
      goodsState: 1,
      goodsBody: '',
      mobileBody: '',
      skuList: []
    })

    const goodsRules = {
      goodsName: [
        { required: true, message: '请输入商品名称', trigger: 'blur' }
      ]
    }

    const dialogTitle = ref('新增商品')

    

    // SKU管理
    const skuDialogVisible = ref(false)
    const skuDialogType = ref('add')
    const skuDialogTitle = ref('添加SKU')
    const skuFormRef = ref()
    const currentSpu = ref(null)
    const skuForm = reactive({
      goodsId: null,
      commonId: null,
      goodsName: '',
      goodsSpecs: '',
      goodsFullSpecs: '',
      goodsPrice: 0,
      goodsMarketPrice: 0,
      goodsStorage: 0,
      goodsSerial: '',
      goodsBarcode: '',
      isDefault: 0
    })

    const skuRules = {
      goodsSpecs: [
        { required: true, message: '请输入规格', trigger: 'blur' }
      ],
      goodsPrice: [
        { required: true, message: '请输入价格', trigger: 'blur' }
      ],
      goodsStorage: [
        { required: true, message: '请输入库存', trigger: 'blur' }
      ]
    }

    // 类目
    const categoryTree = ref([])
    const selectedCategories = ref([])
    
    // 级联选择器配置（支持多选）
    const cascaderProps = {
      value: 'categoryId',
      label: 'categoryName',
      children: 'children',
      multiple: true,
      emitPath: true, // 改为 true，这样可以正确显示完整路径
      checkStrictly: true // 允许选择任意级别
    }
    
    // 根据类目ID查找完整路径
    const findCategoryPath = (categoryId, tree = categoryTree.value, path = []) => {
      for (const node of tree) {
        const currentPath = [...path, node.categoryId]
        if (node.categoryId === categoryId) {
          return currentPath
        }
        if (node.children && node.children.length > 0) {
          const found = findCategoryPath(categoryId, node.children, currentPath)
          if (found) return found
        }
      }
      return null
    }

    // 规格
    const specList = ref([])
    const attributeList = ref([])
    const currentSpecIndex = ref(null)

    // 相册选择器
    const albumDialogVisible = ref(false)
    const albumTree = ref([])
    const albumFiles = ref([])
    const selectedImage = ref('')
    const currentSkuRow = ref(null)
    const imageSelectMode = ref('sku') // 'sku' 或 'spec' 或 'carousel'
    
    // Quill 编辑器配置
    const quillOptions = {
      placeholder: '请输入PC端商品详情...',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],
          ['blockquote', 'code-block'],
          [{ 'header': 1 }, { 'header': 2 }],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          [{ 'script': 'sub' }, { 'script': 'super' }],
          [{ 'indent': '-1' }, { 'indent': '+1' }],
          [{ 'direction': 'rtl' }],
          [{ 'size': ['small', false, 'large', 'huge'] }],
          [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
          [{ 'color': [] }, { 'background': [] }],
          [{ 'font': [] }],
          [{ 'align': [] }],
          ['clean'],
          ['link', 'image']
        ]
      }
    }
    
    // 移动端编辑器配置（简化工具栏）
    const mobileQuillOptions = {
      placeholder: '请输入移动端商品详情...',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline'],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          [{ 'header': [1, 2, 3, false] }],
          [{ 'color': [] }, { 'background': [] }],
          [{ 'align': [] }],
          ['clean'],
          ['link', 'image']
        ]
      }
    }
    
    // 新版相册选择器相关
    const albumLoading = ref(false)
    const currentAlbumId = ref(null)
    const currentIsSystem = ref(null) // null表示全部，0表示非系统，1表示系统
    const selectedImages = ref([]) // 多选图片数组
    const imageSearchKeyword = ref('')
    const albumPageNum = ref(1)
    const albumPageSize = ref(12)
    const albumTotal = ref(0)
    const renameDialogVisible = ref(false)
    const renameForm = reactive({
      fileId: null,
      newName: ''
    })
    const imagePreviewVisible = ref(false)
    const previewImageUrl = ref('')
    
    // 转移到相关
    const transferDialogVisible = ref(false)
    const transferTargetName = ref('')
    const selectedFirstLevel = ref(null)
    const selectedTargetAlbum = ref(null)
    
    // 相册编辑相关
    const albumEditDialogVisible = ref(false)
    const albumEditDialogTitle = ref('新增相册')
    const albumEditType = ref('add') // 'add' 或 'edit'
    const albumEditFormRef = ref()
    const albumEditForm = reactive({
      albumId: null,
      albumName: '',
      parentId: 0
    })
    const albumEditRules = {
      albumName: [
        { required: true, message: '请输入相册名称', trigger: 'blur' }
      ]
    }
    
    // 当前选中的真实相册（非系统目录）
    const currentRealAlbum = computed(() => {
      if (!currentAlbumId.value || currentAlbumId.value <= 0) return null
      // 在相册树中查找
      const findAlbum = (items) => {
        for (const item of items) {
          if (item.albumId === currentAlbumId.value) return item
          if (item.children) {
            const found = findAlbum(item.children)
            if (found) return found
          }
        }
        return null
      }
      return findAlbum(albumTree.value)
    })
    
    // 是否可以编辑当前相册
    const canEditCurrentAlbum = computed(() => {
      return currentRealAlbum.value !== null
    })
    
    // 是否可以删除当前相册
    const canDeleteCurrentAlbum = computed(() => {
      return currentRealAlbum.value !== null
    })
    
    // 用于下拉选择的相册树（添加顶级选项）
    const albumTreeForSelect = computed(() => {
      return [
        { albumId: 0, albumName: '顶级相册', children: albumTree.value }
      ]
    })
    
    // 带系统目录的相册树
    const albumTreeWithSystem = computed(() => {
      const systemAlbums = [
        {
          albumId: -1,
          albumName: '全部图片',
          parentId: 0,
          isSystem: null,
          disabled: true
        },
        {
          albumId: -2,
          albumName: '系统图片',
          parentId: 0,
          isSystem: 1,
          disabled: true
        }
      ]
      return [...systemAlbums, ...albumTree.value]
    })
    
    // 可转移的相册列表（排除系统目录）
    const transferableAlbums = computed(() => {
      const result = []
      const flatten = (items) => {
        items.forEach(item => {
          if (item.albumId > 0) {
            result.push(item)
          }
          if (item.children && item.children.length > 0) {
            flatten(item.children)
          }
        })
      }
      flatten(albumTree.value)
      return result
    })
    
    // 一级目录列表（排除系统目录和当前目录）
    const firstLevelAlbums = computed(() => {
      return albumTree.value.filter(item => item.albumId > 0 && !item.isSystem)
    })
    
    // 二级目录列表（根据选中的一级目录）
    const secondLevelAlbums = computed(() => {
      if (!selectedFirstLevel.value) return []
      return selectedFirstLevel.value.children?.filter(item => item.albumId > 0 && !item.isSystem) || []
    })

    const fetchGoods = async () => {
      loading.value = true
      try {
        const response = await goodsCommonApi.getPage(pageNum.value, pageSize.value)
        if (response.code === 200) {
          goodsTableData.value = response.data.records || []
          total.value = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取商品列表失败')
        }
      } catch (error) {
        ElMessage.error('获取商品列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const fetchCategories = async () => {
      try {
        const response = await categoryApi.getTree()
        if (response.code === 200) {
          categoryTree.value = response.data || []
        }
      } catch (error) {
        console.error('获取类目失败', error)
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchGoods()
    }

    const handleCurrentChange = (val) => {
      pageNum.value = val
      fetchGoods()
    }

    const openAddDialog = () => {
      dialogType.value = 'add'
      dialogTitle.value = '新增商品'
      selectedCategories.value = []
      specList.value = []
      // 完全重置goodsForm，确保编辑器内容被清空
      Object.assign(goodsForm, {
        commonId: null,
        goodsName: '',
        unitName: '',
        categoryId: null,
        categoryId1: null,
        categoryId2: null,
        categoryId3: null,
        goodsImages: [],
        goodsPrice: 0,
        goodsMarketPrice: 0,
        goodsCostPrice: 0,
        goodsStorage: 0,
        goodsState: 1,
        goodsBody: '',
        mobileBody: '',
        skuList: []
      })
      // 使用nextTick确保编辑器正确重置
      nextTick(() => {
        dialogVisible.value = true
      })
    }

    const openEditDialog = async (row) => {
      dialogType.value = 'edit'
      dialogTitle.value = '编辑商品'
      
      // 重置规格列表
      specList.value = []
      
      // 获取完整商品详情
      try {
        const response = await goodsCommonApi.getById(row.commonId)
        if (response.code === 200 && response.data) {
          const detail = response.data
          // 多选类目，处理 categoryId 字符串（如 "1,2,3"）转换为路径数组
          if (detail.categoryId) {
            const categoryIds = detail.categoryId.toString().split(',').filter(Boolean).map(Number)
            // 查找每个类目ID的完整路径
            const paths = categoryIds.map(id => findCategoryPath(id)).filter(Boolean)
            selectedCategories.value = paths
          } else {
            selectedCategories.value = []
          }
          
          // 处理goodsImages，确保是数组
          let images = detail.goodsImages
          if (typeof images === 'string') {
            try {
              images = JSON.parse(images)
            } catch (e) {
              images = []
            }
          }
          if (!Array.isArray(images)) {
            images = []
          }
          
          // 从SKU列表解析规格信息
          if (detail.skuList && detail.skuList.length > 0) {
            parseSpecsFromSkuList(detail.skuList)
          }
          
          Object.assign(goodsForm, {
            commonId: detail.commonId,
            goodsName: detail.goodsName,
            unitName: detail.unitName || '',
            categoryId: detail.categoryId,
            categoryId1: detail.categoryId1,
            categoryId2: detail.categoryId2,
            categoryId3: detail.categoryId3,
            goodsImages: images,
            goodsPrice: detail.goodsPrice,
            goodsMarketPrice: detail.goodsMarketPrice,
            goodsCostPrice: detail.goodsCostPrice,
            goodsStorage: detail.goodsStorage,
            goodsState: detail.goodsState,
            goodsBody: detail.goodsBody || '',
            mobileBody: detail.mobileBody || '',
            skuList: detail.skuList ? [...detail.skuList] : []
          })
        } else {
          // 如果获取详情失败，使用列表数据
          if (row.categoryId) {
            const categoryIds = row.categoryId.toString().split(',').filter(Boolean).map(Number)
            const paths = categoryIds.map(id => findCategoryPath(id)).filter(Boolean)
            selectedCategories.value = paths
          } else {
            selectedCategories.value = []
          }
          Object.assign(goodsForm, {
            commonId: row.commonId,
            goodsName: row.goodsName,
            unitName: row.unitName || '',
            categoryId: row.categoryId,
            categoryId1: row.categoryId1,
            categoryId2: row.categoryId2,
            categoryId3: row.categoryId3,
            goodsImages: [],
            goodsPrice: row.goodsPrice,
            goodsMarketPrice: row.goodsMarketPrice,
            goodsCostPrice: row.goodsCostPrice,
            goodsStorage: row.goodsStorage,
            goodsState: row.goodsState,
            goodsBody: '',
            mobileBody: '',
            skuList: row.skuList ? [...row.skuList] : []
          })
        }
      } catch (error) {
        console.error('获取商品详情失败', error)
        // 使用列表数据
        if (row.categoryId) {
          const categoryIds = row.categoryId.toString().split(',').filter(Boolean).map(Number)
          const paths = categoryIds.map(id => findCategoryPath(id)).filter(Boolean)
          selectedCategories.value = paths
        } else {
          selectedCategories.value = []
        }
        Object.assign(goodsForm, {
          commonId: row.commonId,
          goodsName: row.goodsName,
          unitName: row.unitName || '',
          categoryId: row.categoryId,
          categoryId1: row.categoryId1,
          categoryId2: row.categoryId2,
          categoryId3: row.categoryId3,
          goodsImages: [],
          goodsPrice: row.goodsPrice,
          goodsMarketPrice: row.goodsMarketPrice,
          goodsCostPrice: row.goodsCostPrice,
          goodsStorage: row.goodsStorage,
          goodsState: row.goodsState,
          goodsBody: '',
          mobileBody: '',
          skuList: row.skuList ? [...row.skuList] : []
        })
      }
      
      dialogVisible.value = true
    }

    const fetchAttributes = async (categoryId) => {
      try {
        const response = await attributeApi.getByCategoryId(categoryId)
        if (response.code === 200) {
          attributeList.value = response.data || []
        }
      } catch (error) {
        console.error('获取属性失败', error)
      }
    }

    const handleAttributeChange = async (val, index) => {
      const spec = specList.value[index]
      
      // 根据输入的名称查找是否是现有属性
      const attr = attributeList.value.find(a => a.attributeName === val)
      
      if (attr) {
        // 选择了现有属性
        spec.name = attr.attributeName
        spec.attributeId = attr.attributeId
        // 获取属性值列表
        try {
          const response = await attributeApi.getValuesByAttributeId(attr.attributeId)
          if (response.code === 200) {
            spec.valueOptions = (response.data || []).map(v => ({
              attributeValueId: v.attributeValueId,
              attributeValueName: v.attributeValueName,
              imageUrl: ''
            }))
          }
        } catch (error) {
          console.error('获取属性值失败', error)
        }
      } else {
        // 自定义输入的属性名
        spec.name = val
        spec.attributeId = null
        spec.valueOptions = []
      }
      spec.selectedValues = []
    }

    const addSpec = () => {
      specList.value.push({
        attributeId: null,
        name: '',
        valueOptions: [],
        selectedValues: [],
        customValue: ''
      })
    }

    const removeSpec = (index) => {
      specList.value.splice(index, 1)
    }

    const addCustomValue = (index) => {
      const spec = specList.value[index]
      const value = spec.customValue?.trim()
      if (!value) {
        ElMessage.warning('请输入规格值')
        return
      }
      
      // 检查是否已存在（处理字符串和对象的情况）
      const exists = spec.selectedValues.some(v => {
        const vStr = typeof v === 'object' ? (v?.name || v?.label || JSON.stringify(v)) : String(v)
        return vStr === value
      })
      if (exists) {
        ElMessage.warning('该规格值已存在')
        return
      }
      
      // 添加到选项和选中值
      const newValue = {
        attributeValueId: 'custom_' + Date.now(),
        attributeValueName: value,
        imageUrl: spec.tempImageUrl || ''
      }
      spec.valueOptions.push(newValue)
      spec.selectedValues.push(value)
      spec.customValue = ''
      spec.tempImageUrl = ''
    }

    const openSpecImageSelector = (index) => {
      currentSpecIndex.value = index
      imageSelectMode.value = 'spec'
      resetAlbumSelector()
      fetchAlbumTree()
      albumDialogVisible.value = true
      handleAlbumSelect({ albumId: -1, isSystem: null })
    }

    // 从SKU列表解析规格信息
    const parseSpecsFromSkuList = (skuList) => {
      if (!skuList || skuList.length === 0) return
      
      // 解析第一个SKU的规格
      const firstSku = skuList[0]
      if (!firstSku.goodsSpecs) return
      
      // 解析规格字符串 "颜色:黑色;内存:128G"
      const specPairs = firstSku.goodsSpecs.split(';')
      const specNames = specPairs.map(pair => pair.split(':')[0]).filter(Boolean)
      
      // 构建规格列表
      specNames.forEach((name, index) => {
        const spec = {
          attributeId: null,
          name: name,
          valueOptions: [],
          selectedValues: [],
          customValue: ''
        }
        
        // 收集该规格的所有值
        const valueSet = new Set()
        skuList.forEach(sku => {
          if (sku.goodsSpecs) {
            const pairs = sku.goodsSpecs.split(';')
            pairs.forEach(pair => {
              const [n, v] = pair.split(':')
              if (n === name && v) {
                valueSet.add(v)
              }
            })
          }
        })
        
        // 添加到选项和选中值
        valueSet.forEach(value => {
          spec.valueOptions.push({
            attributeValueId: 'parsed_' + index + '_' + value,
            attributeValueName: value,
            imageUrl: ''
          })
          spec.selectedValues.push(value)
        })
        
        specList.value.push(spec)
      })
    }

    const generateSkus = () => {
      // 过滤掉空的规格
      const validSpecs = specList.value.filter(s => s.name && s.selectedValues && s.selectedValues.length > 0)
      if (validSpecs.length === 0) {
        ElMessage.warning('请至少添加一个规格并选择规格值')
        return
      }

      // 解析规格值
      const specValues = validSpecs.map(s => ({
        name: s.name,
        values: s.selectedValues.map(val => {
          // 处理val可能是对象的情况
          const valStr = typeof val === 'object' ? (val?.name || val?.label || JSON.stringify(val)) : String(val)
          const option = s.valueOptions.find(v => {
            const vName = typeof v.attributeValueName === 'object' ? (v.attributeValueName?.name || v.attributeValueName?.label || JSON.stringify(v.attributeValueName)) : String(v.attributeValueName)
            return vName === valStr
          })
          return {
            value: valStr,
            imageUrl: option?.imageUrl || ''
          }
        })
      }))

      // 生成SKU组合
      const combinations = generateCombinations(specValues)
      
      goodsForm.skuList = combinations.map(combo => {
        const firstImage = combo.find(c => c.imageUrl)?.imageUrl || ''
        return {
          goodsSpecs: combo.map(c => `${c.name}:${c.value}`).join(';'),
          goodsFullSpecs: combo.map(c => `${c.name}:${c.value}`).join(' '),
          goodsPrice: 0,
          goodsMarketPrice: 0,
          goodsCostPrice: 0,
          goodsStorage: 0,
          goodsSerial: '',
          goodsBarcode: '',
          imageName: firstImage,
          isDefault: 0
        }
      })

      ElMessage.success(`生成了 ${goodsForm.skuList.length} 个SKU`)
    }

    const generateCombinations = (specs) => {
      if (specs.length === 0) return []
      if (specs.length === 1) {
        return specs[0].values.map(v => [{ name: specs[0].name, value: v.value, imageUrl: v.imageUrl }])
      }
      
      const [first, ...rest] = specs
      const restCombinations = generateCombinations(rest)
      const result = []
      
      for (const value of first.values) {
        for (const combo of restCombinations) {
          result.push([{ name: first.name, value: value.value, imageUrl: value.imageUrl }, ...combo])
        }
      }
      
      return result
    }


    // 重置相册选择器状态
    const resetAlbumSelector = () => {
      selectedImages.value = []
      imageSearchKeyword.value = ''
      albumPageNum.value = 1
      albumPageSize.value = 12
      currentAlbumId.value = null
      currentIsSystem.value = null
    }

    const openSkuImageSelector = (row) => {
      imageSelectMode.value = 'sku'
      currentSkuRow.value = row
      resetAlbumSelector()
      fetchAlbumTree()
      albumDialogVisible.value = true
      handleAlbumSelect({ albumId: -1, isSystem: null })
    }

    const fetchAlbumTree = async () => {
      try {
        const response = await albumApi.getTree()
        if (response.code === 200) {
          albumTree.value = response.data || []
        }
      } catch (error) {
        console.error('获取相册失败', error)
      }
    }

    // 新版分页获取文件列表
    const fetchAlbumFiles = async () => {
      albumLoading.value = true
      try {
        const params = {
          albumId: currentAlbumId.value,
          isSystem: currentIsSystem.value,
          keyword: imageSearchKeyword.value,
          pageNum: albumPageNum.value,
          pageSize: albumPageSize.value
        }
        const response = await albumApi.getFilesPage(params)
        if (response.code === 200) {
          albumFiles.value = response.data.records || []
          albumTotal.value = response.data.total || 0
        }
      } catch (error) {
        console.error('获取相册文件失败', error)
        ElMessage.error('获取图片列表失败')
      } finally {
        albumLoading.value = false
      }
    }

    const handleAlbumSelect = async (data) => {
      currentAlbumId.value = data.albumId > 0 ? data.albumId : null
      currentIsSystem.value = data.isSystem
      albumPageNum.value = 1
      await fetchAlbumFiles()
    }
    
    // 图片搜索
    const handleImageSearch = () => {
      albumPageNum.value = 1
      fetchAlbumFiles()
    }
    
    // 分页变化
    const handleAlbumPageChange = (page) => {
      albumPageNum.value = page
      fetchAlbumFiles()
    }
    
    const handleAlbumSizeChange = (size) => {
      albumPageSize.value = size
      albumPageNum.value = 1
      fetchAlbumFiles()
    }
    
    // 多选图片相关
    const isImageSelected = (file) => {
      return selectedImages.value.some(item => item.filesId === file.filesId)
    }
    
    const getImageOrder = (file) => {
      const index = selectedImages.value.findIndex(item => item.filesId === file.filesId)
      return index >= 0 ? index + 1 : 0
    }
    
    const toggleImageSelect = (file) => {
      const index = selectedImages.value.findIndex(item => item.filesId === file.filesId)
      if (index >= 0) {
        selectedImages.value.splice(index, 1)
      } else {
        selectedImages.value.push(file)
      }
    }
    
    // 查看大图
    const handleViewImage = (file) => {
      previewImageUrl.value = getFullImageUrl(file.filesName)
      imagePreviewVisible.value = true
    }
    
    // 重命名
    const handleRenameImage = (file) => {
      renameForm.fileId = file.filesId
      renameForm.newName = file.originalName
      renameDialogVisible.value = true
    }
    
    const confirmRename = async () => {
      if (!renameForm.newName.trim()) {
        ElMessage.warning('请输入新名称')
        return
      }
      try {
        const response = await albumApi.renameFile(renameForm.fileId, renameForm.newName.trim())
        if (response.code === 200) {
          ElMessage.success('重命名成功')
          renameDialogVisible.value = false
          fetchAlbumFiles()
        } else {
          ElMessage.error(response.message || '重命名失败')
        }
      } catch (error) {
        console.error('重命名失败', error)
        ElMessage.error('重命名失败')
      }
    }
    
    // 删除单张图片
    const handleDeleteSingle = async (file) => {
      try {
        await ElMessageBox.confirm('确定要删除这张图片吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await albumApi.deleteFile(file.filesId)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          // 从选中列表中移除
          const index = selectedImages.value.findIndex(item => item.filesId === file.filesId)
          if (index >= 0) {
            selectedImages.value.splice(index, 1)
          }
          fetchAlbumFiles()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败', error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 批量删除
    const handleBatchDelete = async () => {
      if (selectedImages.value.length === 0) {
        ElMessage.warning('请选择要删除的图片')
        return
      }
      try {
        await ElMessageBox.confirm(`确定要删除选中的 ${selectedImages.value.length} 张图片吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const fileIds = selectedImages.value.map(file => file.filesId)
        const response = await albumApi.batchDeleteFiles(fileIds)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          selectedImages.value = []
          fetchAlbumFiles()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败', error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 打开转移对话框
    const openTransferDialog = () => {
      if (selectedImages.value.length === 0) {
        ElMessage.warning('请先选择要转移的图片')
        return
      }
      selectedFirstLevel.value = null
      selectedTargetAlbum.value = null
      transferDialogVisible.value = true
    }
    
    // 选择一级目录
    const selectFirstLevel = (album) => {
      selectedFirstLevel.value = album
      selectedTargetAlbum.value = null
    }
    
    // 选择目标相册
    const selectTargetAlbum = (album) => {
      // 不能选择当前目录
      if (album.albumId === currentAlbumId.value) {
        ElMessage.warning('不能转移到当前目录')
        return
      }
      selectedTargetAlbum.value = album
    }
    
    // 确认转移
    const confirmTransfer = async () => {
      if (!selectedTargetAlbum.value) {
        ElMessage.warning('请选择目标相册')
        return
      }
      if (selectedImages.value.length === 0) {
        ElMessage.warning('请选择要转移的图片')
        return
      }
      try {
        const fileIds = selectedImages.value.map(file => file.filesId)
        const response = await albumApi.batchTransferFiles(fileIds, selectedTargetAlbum.value.albumId)
        if (response.code === 200) {
          ElMessage.success('转移成功')
          selectedImages.value = []
          transferTargetName.value = ''
          transferDialogVisible.value = false
          fetchAlbumFiles()
        } else {
          ElMessage.error(response.message || '转移失败')
        }
      } catch (error) {
        console.error('批量转移失败', error)
        ElMessage.error('转移失败')
      }
    }
    
    // 相册节点菜单命令处理
    const handleAlbumNodeMenuCommand = (command, albumData) => {
      switch (command) {
        case 'add':
          // 在当前相册下新增子相册
          openAddAlbumDialogWithParent(albumData.albumId)
          break
        case 'edit':
          openEditAlbumDialogByData(albumData)
          break
        case 'delete':
          handleDeleteAlbumByData(albumData)
          break
      }
    }
    
    // 打开新增相册对话框（指定父级）
    const openAddAlbumDialogWithParent = (parentId) => {
      albumEditType.value = 'add'
      albumEditDialogTitle.value = '新增相册'
      albumEditForm.albumId = null
      albumEditForm.albumName = ''
      albumEditForm.parentId = parentId || 0
      albumEditDialogVisible.value = true
    }
    
    // 打开编辑相册对话框（通过数据）
    const openEditAlbumDialogByData = (albumData) => {
      albumEditType.value = 'edit'
      albumEditDialogTitle.value = '编辑相册'
      albumEditForm.albumId = albumData.albumId
      albumEditForm.albumName = albumData.albumName
      albumEditForm.parentId = albumData.parentId || 0
      albumEditDialogVisible.value = true
    }
    
    // 删除相册（通过数据）
    const handleDeleteAlbumByData = async (albumData) => {
      const albumId = albumData.albumId
      const albumName = albumData.albumName
      
      try {
        // 先检查相册是否有图片
        const checkResponse = await albumApi.getFilesByAlbumId(albumId)
        const files = checkResponse.data || []
        
        if (files.length > 0) {
          ElMessage.warning(`相册 "${albumName}" 中还有 ${files.length} 张图片，不能删除`)
          return
        }
        
        // 确认删除
        await ElMessageBox.confirm(`确定要删除相册 "${albumName}" 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await albumApi.delete(albumId)
        if (response.code === 200) {
          ElMessage.success('删除相册成功')
          // 如果删除的是当前选中的相册，重置到全部图片
          if (currentAlbumId.value === albumId) {
            currentAlbumId.value = null
            handleAlbumSelect({ albumId: -1, isSystem: null })
          }
          // 刷新相册树
          await fetchAlbumTree()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除相册失败', error)
          ElMessage.error('删除失败')
        }
      }
    }
    
    // 确认相册编辑（新增/编辑）
    const confirmAlbumEdit = async () => {
      try {
        await albumEditFormRef.value.validate()
        
        const submitData = {
          albumId: albumEditForm.albumId,
          albumName: albumEditForm.albumName,
          parentId: albumEditForm.parentId || 0
        }
        
        let response
        if (albumEditType.value === 'add') {
          response = await albumApi.create(submitData)
        } else {
          response = await albumApi.update(submitData)
        }
        
        if (response.code === 200) {
          ElMessage.success(albumEditType.value === 'add' ? '新增相册成功' : '更新相册成功')
          albumEditDialogVisible.value = false
          // 刷新相册树
          await fetchAlbumTree()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('相册编辑失败', error)
        ElMessage.error('操作失败')
      }
    }
    
    // 删除相册
    const handleDeleteAlbum = async () => {
      if (!currentRealAlbum.value) {
        ElMessage.warning('请先选择要删除的相册')
        return
      }
      
      const albumId = currentRealAlbum.value.albumId
      const albumName = currentRealAlbum.value.albumName
      
      try {
        // 先检查相册是否有图片
        const checkResponse = await albumApi.getFilesByAlbumId(albumId)
        const files = checkResponse.data || []
        
        if (files.length > 0) {
          ElMessage.warning(`相册 "${albumName}" 中还有 ${files.length} 张图片，不能删除`)
          return
        }
        
        // 确认删除
        await ElMessageBox.confirm(`确定要删除相册 "${albumName}" 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await albumApi.delete(albumId)
        if (response.code === 200) {
          ElMessage.success('删除相册成功')
          // 重置当前相册选择
          currentAlbumId.value = null
          // 刷新相册树
          await fetchAlbumTree()
          // 重新加载全部图片
          handleAlbumSelect({ albumId: -1, isSystem: null })
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除相册失败', error)
          ElMessage.error('删除失败')
        }
      }
    }

    const confirmImageSelect = () => {
      if (selectedImages.value.length === 0) {
        ElMessage.warning('请选择图片')
        return
      }
      
      // 获取选中图片的文件名数组（按选择顺序）
      const selectedFileNames = selectedImages.value.map(file => file.filesName)
      
      if (imageSelectMode.value === 'sku' && currentSkuRow.value) {
        currentSkuRow.value.imageName = selectedFileNames[0]
        ElMessage.success('SKU图片设置成功')
      } else if (imageSelectMode.value === 'spec' && currentSpecIndex.value !== null) {
        // 为规格值设置临时图片
        specList.value[currentSpecIndex.value].tempImageUrl = selectedFileNames[0]
        ElMessage.success('图片已选择，请在添加规格值时使用')
      } else if (imageSelectMode.value === 'carousel') {
        // 添加到轮播图列表，保持选择顺序
        let addedCount = 0
        selectedFileNames.forEach(fileName => {
          if (!goodsForm.goodsImages.includes(fileName)) {
            goodsForm.goodsImages.push(fileName)
            addedCount++
          }
        })
        if (addedCount > 0) {
          ElMessage.success(`成功添加 ${addedCount} 张图片到轮播图`)
        } else {
          ElMessage.warning('选中的图片都已在轮播图中')
        }
      }
      
      albumDialogVisible.value = false
      selectedImages.value = []
    }

    // 打开轮播图选择器
    const openCarouselSelector = () => {
      imageSelectMode.value = 'carousel'
      currentSkuRow.value = null
      currentSpecIndex.value = null
      resetAlbumSelector()
      fetchAlbumTree()
      albumDialogVisible.value = true
      handleAlbumSelect({ albumId: -1, isSystem: null })
    }

    // 删除轮播图
    const removeCarouselImage = (index) => {
      goodsForm.goodsImages.splice(index, 1)
      ElMessage.success('图片已删除')
    }

    // 移动轮播图位置
    const moveCarouselImage = (index, direction) => {
      const newIndex = index + direction
      if (newIndex < 0 || newIndex >= goodsForm.goodsImages.length) return
      
      // 交换位置
      const temp = goodsForm.goodsImages[index]
      goodsForm.goodsImages[index] = goodsForm.goodsImages[newIndex]
      goodsForm.goodsImages[newIndex] = temp
    }

    const getFullImageUrl = (path) => {
      return fileApi.buildFullUrl(path)
    }



    const openAddSkuDialog = (row) => {
      currentSpu.value = row
      skuDialogType.value = 'add'
      skuDialogTitle.value = '添加SKU'
      Object.assign(skuForm, {
        goodsId: null,
        commonId: row.commonId,
        goodsName: row.goodsName,
        goodsSpecs: '',
        goodsFullSpecs: '',
        goodsPrice: row.goodsPrice || 0,
        goodsMarketPrice: row.goodsMarketPrice || 0,
        goodsStorage: 0,
        goodsSerial: '',
        goodsBarcode: '',
        isDefault: 0
      })
      skuDialogVisible.value = true
    }

    const openEditSkuDialog = (sku) => {
      skuDialogType.value = 'edit'
      skuDialogTitle.value = '编辑SKU'
      Object.assign(skuForm, { ...sku })
      skuDialogVisible.value = true
    }

    const handleSkuSubmit = async () => {
      try {
        await skuFormRef.value.validate()
        let response
        if (skuDialogType.value === 'add') {
          response = await goodsCommonApi.createSku(skuForm)
        } else {
          response = await goodsCommonApi.updateSku(skuForm)
        }

        if (response.code === 200) {
          ElMessage.success(skuDialogType.value === 'add' ? '添加SKU成功' : '更新SKU成功')
          skuDialogVisible.value = false
          fetchGoods()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('操作失败')
      }
    }

    const handleDeleteSku = async (id) => {
      try {
        await ElMessageBox.confirm('确认删除此SKU吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await goodsCommonApi.deleteSku(id)
        if (response.code === 200) {
          ElMessage.success('删除SKU成功')
          fetchGoods()
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

    const handleSubmit = async () => {
      try {
        await goodsFormRef.value.validate()
        
        // 处理多选类目，将所有选中的类目ID拼接成字符串
        let categoryIdStr = ''
        if (selectedCategories.value && selectedCategories.value.length > 0) {
          // emitPath: true 时，每个元素是路径数组，取最后一个元素（叶子节点ID）
          const allCategoryIds = selectedCategories.value.map(path => {
            return Array.isArray(path) ? path[path.length - 1] : path
          }).filter(Boolean)
          categoryIdStr = allCategoryIds.join(',')
        }
        
        // 构建请求数据
        const requestData = {
          spu: {
            commonId: goodsForm.commonId,
            goodsName: goodsForm.goodsName,
            unitName: goodsForm.unitName,
            categoryId1: goodsForm.categoryId1,
            categoryId2: goodsForm.categoryId2,
            categoryId3: goodsForm.categoryId3,
            goodsState: goodsForm.goodsState
            // 价格、库存、主图、轮播图、类目由后端根据SKU和传入的数据自动设置
          },
          goodsImages: goodsForm.goodsImages,
          categoryId: categoryIdStr,
          skuList: goodsForm.skuList,
          goodsBody: goodsForm.goodsBody,
          mobileBody: goodsForm.mobileBody
        }
        
        let response
        if (dialogType.value === 'add') {
          response = await goodsCommonApi.create(requestData)
        } else {
          // 编辑时也发送完整数据（包含goodsBody、mobileBody）
          response = await goodsCommonApi.update(requestData)
        }

        if (response.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增商品成功' : '更新商品成功')
          dialogVisible.value = false
          fetchGoods()
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
        await ElMessageBox.confirm('确认删除此商品吗？', '提示', {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const response = await goodsCommonApi.delete(id)
        if (response.code === 200) {
          ElMessage.success('删除商品成功')
          fetchGoods()
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
      fetchGoods()
      fetchCategories()
    })

    return {
      goodsTableData,
      loading,
      pageNum,
      pageSize,
      total,
      dialogVisible,
      dialogTitle,
      goodsFormRef,
      goodsForm,
      goodsRules,

      skuDialogVisible,
      skuDialogTitle,
      skuFormRef,
      skuForm,
      skuRules,
      categoryTree,
      selectedCategories,
      cascaderProps,
      findCategoryPath,
      specList,
      attributeList,
      albumDialogVisible,
      albumTree,
      albumFiles,
      selectedImage,
      fetchGoods,
      handleSizeChange,
      handleCurrentChange,
      openAddDialog,
      openEditDialog,
      handleAttributeChange,
      addSpec,
      removeSpec,
      addCustomValue,
      openSpecImageSelector,
      generateSkus,
      openSkuImageSelector,
      handleAlbumSelect,
      confirmImageSelect,
      getFullImageUrl,

      openAddSkuDialog,
      openEditSkuDialog,
      handleSkuSubmit,
      handleDeleteSku,
      handleSubmit,
      handleDelete,
      openCarouselSelector,
      removeCarouselImage,
      moveCarouselImage,
      parseSpecsFromSkuList,
      // 新版相册选择器
      albumLoading,
      currentAlbumId,
      selectedImages,
      imageSearchKeyword,
      albumPageNum,
      albumPageSize,
      albumTotal,
      renameDialogVisible,
      renameForm,
      imagePreviewVisible,
      previewImageUrl,
      albumTreeWithSystem,
      transferableAlbums,
      isImageSelected,
      getImageOrder,
      toggleImageSelect,
      handleImageSearch,
      handleAlbumPageChange,
      handleAlbumSizeChange,
      handleViewImage,
      handleRenameImage,
      confirmRename,
      handleDeleteSingle,
      handleBatchDelete,
      // 转移到相关
      transferDialogVisible,
      transferTargetName,
      selectedFirstLevel,
      selectedTargetAlbum,
      firstLevelAlbums,
      secondLevelAlbums,
      openTransferDialog,
      selectFirstLevel,
      selectTargetAlbum,
      confirmTransfer,
      // 相册管理相关
      albumEditDialogVisible,
      albumEditDialogTitle,
      albumEditType,
      albumEditFormRef,
      albumEditForm,
      albumEditRules,
      canEditCurrentAlbum,
      canDeleteCurrentAlbum,
      albumTreeForSelect,
      handleAlbumNodeMenuCommand,
      confirmAlbumEdit,
      // Quill 编辑器配置
      quillOptions,
      mobileQuillOptions
    }
  }
}
</script>

<style scoped>
.goods-common-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sku-list-container {
  padding: 10px 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin: 0 20px;
}

.sku-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.sku-title {
  font-weight: bold;
  color: #606266;
}

.spec-section {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
}

.spec-item {
  margin-bottom: 15px;
}

.spec-card {
  margin-bottom: 10px;
}

.spec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.spec-title {
  font-weight: bold;
  color: #606266;
}

.spec-row {
  align-items: flex-start;
}

.spec-values {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.spec-value-item {
  display: flex;
  align-items: center;
}

.custom-value-input {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.spec-actions {
  margin-top: 15px;
}

.image-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.selected-image {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sku-image {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.album-selector {
  display: flex;
  height: 400px;
}

.album-sidebar {
  width: 200px;
  border-right: 1px solid #e4e7ed;
  padding-right: 10px;
  overflow-y: auto;
}

.album-content {
  flex: 1;
  padding-left: 10px;
  overflow-y: auto;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-item {
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: 4px;
  padding: 2px;
}

.image-item.selected {
  border-color: #409eff;
}

/* 新版相册选择器样式 */
.album-selector-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.album-selector {
  display: flex;
  height: 600px;
}

.album-sidebar {
  width: 200px;
  border-right: 1px solid #e4e7ed;
  padding: 15px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.sidebar-header {
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.album-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 15px;
  overflow: hidden;
}

.album-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.toolbar-right {
  display: flex;
  gap: 10px;
}

.image-grid-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 15px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
}

.image-item {
  cursor: pointer;
  border: 2px solid transparent;
  border-radius: 4px;
  padding: 4px;
  transition: all 0.3s;
  position: relative;
}

.image-item:hover {
  border-color: #c0c4cc;
}

.image-item.selected {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100px;
  overflow: hidden;
  border-radius: 4px;
}

.select-order-badge {
  position: absolute;
  top: 5px;
  left: 5px;
  width: 24px;
  height: 24px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  z-index: 10;
}

.image-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  padding: 5px;
  display: flex;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-wrapper:hover .image-actions {
  opacity: 1;
}

.image-actions .el-button {
  color: white;
  padding: 2px 5px;
  font-size: 12px;
}

.image-actions .el-button--danger {
  color: #f56c6c;
}

.image-name {
  margin-top: 5px;
  font-size: 12px;
  color: #606266;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.album-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 轮播图样式 */
.carousel-images {
  width: 100%;
}

.carousel-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-start;
}

.carousel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.carousel-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
  align-items: center;
}

.carousel-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  min-width: 120px;
}

/* 转移到对话框树形样式 */
.transfer-tree-container {
  display: flex;
  height: 400px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.transfer-tree-left,
.transfer-tree-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.transfer-tree-left {
  border-right: 1px solid #e4e7ed;
}

.tree-title {
  padding: 12px 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
  color: #303133;
}

.tree-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.tree-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.3s;
}

.tree-item:hover {
  background-color: #ecf5ff;
}

.tree-item.active {
  background-color: #409eff;
  color: white;
}

.tree-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.tree-item .current-tag {
  margin-left: auto;
}

/* 相册侧边栏菜单样式 */
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  color: #303133;
  font-weight: bold;
}

.album-menu-trigger {
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  color: #606266;
  transition: all 0.3s;
}

.album-menu-trigger:hover {
  background-color: #e4e7ed;
  color: #409eff;
}

/* Quill 编辑器容器样式 */
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

:deep(.ql-toolbar) {
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: 1px solid #dcdfe6;
}

:deep(.ql-container) {
  border: none;
}

/* 自定义树节点样式 */
.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  padding-right: 8px;
}

.node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-menu-trigger {
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  color: #909399;
  opacity: 0;
  transition: all 0.3s;
}

.custom-tree-node:hover .node-menu-trigger {
  opacity: 1;
}

.node-menu-trigger:hover {
  background-color: #e4e7ed;
  color: #409eff;
}

/* 确保下拉菜单点击不触发节点选择 */
:deep(.el-tree-node__content) {
  position: relative;
}

:deep(.el-dropdown) {
  display: inline-flex;
}
</style>
