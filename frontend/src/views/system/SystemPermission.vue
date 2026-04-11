<template>
  <div class="system-permission-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>权限管理</h2>
        <p>管理菜单权限和按钮权限配置</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建权限
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input v-model="searchForm.name" placeholder="请输入权限名称" clearable @clear="loadData" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据列表 -->
    <el-card class="list-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" row-key="id" default-expand-all :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="权限名称" min-width="200" />
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'MENU' ? 'primary' : 'info'">
              {{ row.type === 'MENU' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级权限" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级权限" clearable style="width: 100%">
            <el-option label="顶级权限" :value="null" />
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限编码" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="MENU">菜单</el-radio>
            <el-radio label="BUTTON">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路径" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionList, savePermission, deletePermission } from '@/api/system/permission'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建权限')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  name: ''
})

const form = reactive({
  id: null,
  parentId: null,
  name: '',
  code: '',
  type: 'MENU',
  path: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionList(searchForm)
    tableData.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.name = ''
  loadData()
}

const openDialog = (row = null) => {
  dialogTitle.value = row ? '编辑权限' : '新建权限'
  dialogVisible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

const resetForm = () => {
  form.id = null
  form.parentId = null
  form.name = ''
  form.code = ''
  form.type = 'MENU'
  form.path = ''
  form.status = 1
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await savePermission(form)
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
  ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-permission-container {
  height: 100%;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-card :deep(.el-card__body) {
  padding: 16px 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.search-form .el-form-item {
  margin-bottom: 0;
  margin-right: 20px;
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
