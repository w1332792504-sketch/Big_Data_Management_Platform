<template>
  <div class="system-dictionary-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>字典管理</h2>
        <p>管理系统数据字典和枚举配置</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建字典
      </el-button>
    </div>

    <!-- 数据列表 -->
    <el-card class="list-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="字典名称" />
        <el-table-column prop="code" label="字典编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewItems(row)">字典项</el-button>
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 字典项对话框 -->
    <el-dialog v-model="itemDialogVisible" title="字典项管理" width="800px">
      <el-button type="primary" @click="openItemDialog()" style="margin-bottom: 15px">
        <el-icon><Plus /></el-icon>
        新建字典项
      </el-button>
      <el-table :data="currentDictionaryItems" style="width: 100%">
        <el-table-column prop="label" label="标签" />
        <el-table-column prop="value" label="值" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openItemDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新建/编辑字典对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="字典名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入字典编码" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新建/编辑字典项对话框 -->
    <el-dialog
      v-model="itemDialogFormVisible"
      :title="itemDialogTitle"
      width="500px"
      @close="resetItemForm"
    >
      <el-form :model="itemForm" :rules="itemRules" ref="itemFormRef" label-width="100px">
        <el-form-item label="标签" prop="label">
          <el-input v-model="itemForm.label" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="itemForm.value" placeholder="请输入值" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="itemForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="itemForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleItemSubmit" :loading="itemSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDictionaryList, saveDictionary, deleteDictionary, getDictionaryItems, saveDictionaryItem, deleteDictionaryItem } from '@/api/system/dictionary'

const loading = ref(false)
const submitting = ref(false)
const itemSubmitting = ref(false)
const dialogVisible = ref(false)
const itemDialogVisible = ref(false)
const itemDialogFormVisible = ref(false)
const dialogTitle = ref('新建字典')
const itemDialogTitle = ref('新建字典项')
const formRef = ref(null)
const itemFormRef = ref(null)
const tableData = ref([])
const currentDictionaryItems = ref([])
const currentDictionaryId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 1
})

const itemForm = reactive({
  id: null,
  dictionaryId: null,
  label: '',
  value: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入字典编码', trigger: 'blur' }]
}

const itemRules = {
  label: [{ required: true, message: '请输入标签', trigger: 'blur' }],
  value: [{ required: true, message: '请输入值', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDictionaryList(currentPage.value - 1, pageSize.value)
    tableData.value = res.data.content || res.data.elements || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  dialogTitle.value = row ? '编辑字典' : '新建字典'
  dialogVisible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.code = ''
  form.description = ''
  form.status = 1
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await saveDictionary(form)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } catch (e) {
      console.error(e)
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该字典吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDictionary(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const viewItems = async (row) => {
  currentDictionaryId.value = row.id
  itemDialogVisible.value = true
  try {
    const res = await getDictionaryItems(row.id)
    currentDictionaryItems.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const openItemDialog = (row = null) => {
  itemDialogTitle.value = row ? '编辑字典项' : '新建字典项'
  itemDialogFormVisible.value = true
  if (row) {
    Object.assign(itemForm, row)
  } else {
    itemForm.dictionaryId = currentDictionaryId.value
  }
}

const resetItemForm = () => {
  itemForm.id = null
  itemForm.dictionaryId = null
  itemForm.label = ''
  itemForm.value = ''
  itemForm.sort = 0
  itemForm.status = 1
  itemFormRef.value?.clearValidate()
}

const handleItemSubmit = async () => {
  if (!itemFormRef.value) return
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    itemSubmitting.value = true
    try {
      await saveDictionaryItem(itemForm)
      ElMessage.success('保存成功')
      itemDialogFormVisible.value = false
      viewItems({ id: currentDictionaryId.value })
    } catch (e) {
      console.error(e)
    } finally {
      itemSubmitting.value = false
    }
  })
}

const handleDeleteItem = (row) => {
  ElMessageBox.confirm('确定要删除该字典项吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDictionaryItem(row.id)
    ElMessage.success('删除成功')
    viewItems({ id: currentDictionaryId.value })
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-dictionary-container {
  height: 100%;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-info h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* 列表卡片 */
.list-card {
  border-radius: 8px;
}

.list-card :deep(.el-card__body) {
  padding: 0;
}

/* 表格样式 */
.list-card :deep(.el-table) {
  border-radius: 8px;
}

.list-card :deep(.el-table__header) {
  background-color: #f5f7fa;
}

.list-card :deep(.el-table__row) {
  height: 56px;
}

.list-card :deep(.el-table__cell) {
  padding: 12px 0;
}
</style>
