<template>
  <div class="datasource-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>数据源管理</h2>
        <p>管理和配置数据源连接信息，支持多种数据库类型</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建数据源
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="请输入数据源名称" clearable @clear="loadData" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable @change="loadData">
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Hive" value="HIVE" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="loadData">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据列表 -->
    <el-card class="list-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="host" label="主机" width="150" />
        <el-table-column prop="port" label="端口" width="80" />
        <el-table-column prop="dbName" label="数据库" width="120" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="description" label="描述" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="testConnection(row)">测试连接</el-button>
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择数据类型" style="width: 100%">
            <el-option label="MySQL" value="MYSQL" />
            <el-option label="Hive" value="HIVE" />
            <el-option label="Oracle" value="ORACLE" />
            <el-option label="PostgreSQL" value="POSTGRESQL" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机" prop="host">
          <el-input v-model="form.host" placeholder="192.168.1.100" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数据库" prop="database">
          <el-input v-model="form.database" placeholder="数据库名/Schema" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="数据源描述" />
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
import { getDataSourceList, saveDataSource, deleteDataSource, testDataSource } from '@/api/datasource'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建数据源')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  name: '',
  type: null,
  status: null
})

const form = reactive({
  id: null,
  name: '',
  type: 'MYSQL',
  host: '',
  port: 3306,
  database: '',
  username: '',
  password: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const getTypeTag = (type) => {
  const map = { MYSQL: 'success', HIVE: 'danger', ORACLE: 'primary', POSTGRESQL: 'info' }
  return map[type] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDataSourceList()
    let data = res.data || []

    // 前端过滤
    if (searchForm.name) {
      data = data.filter(item => item.name.includes(searchForm.name))
    }
    if (searchForm.type) {
      data = data.filter(item => item.type === searchForm.type)
    }
    if (searchForm.status !== null) {
      data = data.filter(item => item.status === searchForm.status)
    }

    tableData.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.type = null
  searchForm.status = null
  loadData()
}

const openDialog = (row = null) => {
  dialogTitle.value = row ? '编辑数据源' : '新建数据源'
  dialogVisible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.type = 'MYSQL'
  form.host = ''
  form.port = 3306
  form.database = ''
  form.username = ''
  form.password = ''
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
      // 动态生成 jdbcUrl
      if (!form.jdbcUrl) {
        form.jdbcUrl = buildJdbcUrl()
      }
      await saveDataSource(form)
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

const buildJdbcUrl = () => {
  const type = form.type.toUpperCase()
  if (type === 'MYSQL') {
    return `jdbc:mysql://${form.host}:${form.port}/${form.database}?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
  } else if (type === 'HIVE') {
    return `jdbc:hive2://${form.host}:${form.port}/${form.database}`
  } else if (type === 'ORACLE') {
    return `jdbc:oracle:thin:@${form.host}:${form.port}:${form.database}`
  } else if (type === 'POSTGRESQL') {
    return `jdbc:postgresql://${form.host}:${form.port}/${form.database}`
  }
  return ''
}

const testConnection = async (row) => {
  try {
    const res = await testDataSource(row)
    if (res.data) {
      ElMessage.success('连接测试成功')
    } else {
      ElMessage.error('连接测试失败')
    }
  } catch (e) {
    ElMessage.error('连接测试失败：' + e.message)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该数据源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDataSource(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.datasource-container {
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
