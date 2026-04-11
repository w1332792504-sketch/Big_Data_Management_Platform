<template>
  <div class="system-organization-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>组织管理</h2>
        <p>管理组织架构、部门和岗位信息</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建组织
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="组织名称">
          <el-input v-model="searchForm.name" placeholder="请输入组织名称" clearable @clear="loadData" @keyup.enter="loadData" />
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
        <el-table-column prop="name" label="组织名称" min-width="200" />
        <el-table-column prop="code" label="组织编码" width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'DEPT' ? 'primary' : 'success'">
              {{ row.type === 'DEPT' ? '部门' : '岗位' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="负责人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
        <el-form-item label="上级组织" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级组织" clearable style="width: 100%">
            <el-option label="顶级组织" :value="null" />
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="组织名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入组织名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入组织编码" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-radio-group v-model="form.type">
                <el-radio label="DEPT">部门</el-radio>
                <el-radio label="POSITION">岗位</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { getOrganizationList, saveOrganization, deleteOrganization } from '@/api/system/organization'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建组织')
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
  type: 'DEPT',
  leader: '',
  phone: '',
  address: '',
  status: 1,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入组织编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrganizationList(searchForm)
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
  dialogTitle.value = row ? '编辑组织' : '新建组织'
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
  form.type = 'DEPT'
  form.leader = ''
  form.phone = ''
  form.address = ''
  form.status = 1
  form.remark = ''
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await saveOrganization(form)
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
  ElMessageBox.confirm('确定要删除该组织吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteOrganization(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-organization-container {
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
