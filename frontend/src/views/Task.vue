<template>
  <div class="task-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>任务管理</h2>
        <p>管理和监控数据抽取任务，支持全量和增量抽取模式</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建任务
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" clearable @clear="loadData" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="loadData">
            <el-option label="已停止" :value="0" />
            <el-option label="运行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 任务列表 -->
    <el-card class="list-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="sourceDatasourceName" label="源数据源" width="120" />
        <el-table-column prop="targetDatasourceName" label="目标数据源" width="120" />
        <el-table-column prop="sourceTable" label="源表" width="120" />
        <el-table-column prop="targetTable" label="目标表" width="120" />
        <el-table-column prop="extractMode" label="模式" width="80">
          <template #default="{ row }">
            <el-tag :type="row.extractMode === 'FULL' ? 'primary' : 'success'">
              {{ row.extractMode === 'FULL' ? '全量' : '增量' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cronExpression" label="Cron 表达式" width="120" />
        <el-table-column prop="lastRunTime" label="上次执行时间" width="160" />
        <el-table-column label="上次执行状态" width="100">
          <template #default="{ row }">
            <div v-if="row.lastRunTime">
              <el-tag :type="row.lastRunStatus === 0 ? 'success' : 'danger'" size="small">
                {{ row.lastRunStatus === 0 ? '成功' : '失败' }}
              </el-tag>
            </div>
            <div v-else>-</div>
          </template>
        </el-table-column>
        <el-table-column prop="lastRunDuration" label="上次耗时(秒)" width="100" />
        <el-table-column prop="lastRunRecords" label="上次记录数" width="100" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              type="success"
              @click="handleRun(row)"
              :disabled="row.status === 1"
            >
              {{ row.status === 1 ? '运行中' : '执行' }}
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStop(row)"
              :disabled="row.status !== 1"
            >
              停止
            </el-button>
            <el-button size="small" @click="viewExecutions(row)">记录</el-button>
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

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName">
              <el-input v-model="form.taskName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="抽取模式" prop="extractMode">
              <el-radio-group v-model="form.extractMode">
                <el-radio label="FULL">全量</el-radio>
                <el-radio label="INCREMENT">增量</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源数据源" prop="sourceDatasourceId">
              <el-select v-model="form.sourceDatasourceId" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="ds in datasources"
                  :key="ds.id"
                  :label="ds.name"
                  :value="ds.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标数据源" prop="targetDatasourceId">
              <el-select v-model="form.targetDatasourceId" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="ds in datasources"
                  :key="ds.id"
                  :label="ds.name"
                  :value="ds.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="源表名" prop="sourceTable">
              <el-input v-model="form.sourceTable" placeholder="T_PERSON" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标表名" prop="targetTable">
              <el-input v-model="form.targetTable" placeholder="t_person" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="写入模式" prop="writeMode">
              <el-select v-model="form.writeMode" style="width: 100%">
                <el-option label="INSERT" value="insert" />
                <el-option label="UPDATE" value="update" />
                <el-option label="REPLACE" value="replace" />
                <el-option label="TRUNCATE" value="truncate" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="并发数" prop="splitPkNum">
              <el-input-number v-model="form.splitPkNum" :min="1" :max="10" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="增量字段" prop="incrementField">
          <el-input v-model="form.incrementField" placeholder="增量字段名，如：CREATE_TIME" :disabled="form.extractMode !== 'INCREMENT'" />
        </el-form-item>

        <el-form-item label="过滤条件" prop="filterCondition">
          <el-input v-model="form.filterCondition" placeholder="WHERE 条件，不含 WHERE 关键字" />
        </el-form-item>

        <el-form-item label="分片字段" prop="splitPk">
          <el-input v-model="form.splitPk" placeholder="用于并发的字段，如：ID" />
        </el-form-item>

        <el-form-item label="Cron 表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="0 0 2 * * ? 每天凌晨 2 点" />
        </el-form-item>

        <el-form-item label="任务描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="自定义 SQL" prop="querySql">
          <el-input v-model="form.querySql" type="textarea" :rows="4" placeholder="可选，为空时自动 SELECT * FROM 表" />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, saveTask, deleteTask, runTask, stopTask } from '@/api/task'
import { getDataSourceList } from '@/api/datasource'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新建任务')
const formRef = ref(null)
const tableData = ref([])
const datasources = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  taskName: '',
  status: null
})

const form = reactive({
  id: null,
  taskName: '',
  description: '',
  sourceDatasourceId: null,
  targetDatasourceId: null,
  sourceTable: '',
  targetTable: '',
  extractMode: 'FULL',
  incrementField: '',
  querySql: '',
  writeMode: 'insert',
  filterCondition: '',
  splitPkNum: 1,
  splitPk: '',
  status: 0,
  cronExpression: ''
})

const rules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  sourceDatasourceId: [{ required: true, message: '请选择源数据源', trigger: 'change' }],
  targetDatasourceId: [{ required: true, message: '请选择目标数据源', trigger: 'change' }],
  sourceTable: [{ required: true, message: '请输入源表名', trigger: 'blur' }],
  targetTable: [{ required: true, message: '请输入目标表名', trigger: 'blur' }]
}

const getStatusTag = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '已停止', 1: '运行中', 2: '已完成', 3: '失败' }
  return map[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTaskList()
    let data = res.data || []

    // 前端过滤
    if (searchForm.taskName) {
      data = data.filter(item => item.taskName.includes(searchForm.taskName))
    }
    if (searchForm.status !== null) {
      data = data.filter(item => item.status === searchForm.status)
    }

    // 分页处理
    total.value = data.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    tableData.value = data.slice(start, end)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.taskName = ''
  searchForm.status = null
  loadData()
}

const loadDatasources = async () => {
  const res = await getDataSourceList()
  datasources.value = res.data
}

const openDialog = (row = null) => {
  dialogTitle.value = row ? '编辑任务' : '新建任务'
  dialogVisible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

const resetForm = () => {
  form.id = null
  form.taskName = ''
  form.description = ''
  form.sourceDatasourceId = null
  form.targetDatasourceId = null
  form.sourceTable = ''
  form.targetTable = ''
  form.extractMode = 'FULL'
  form.incrementField = ''
  form.querySql = ''
  form.writeMode = 'insert'
  form.filterCondition = ''
  form.splitPkNum = 1
  form.splitPk = ''
  form.status = 0
  form.cronExpression = ''
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await saveTask(form)
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

const handleRun = async (row) => {
  try {
    await runTask(row.id, 'MANUAL', 'admin')
    ElMessage.success('任务已启动')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '启动失败')
  }
}

const handleStop = async (row) => {
  ElMessageBox.confirm('确定要停止该任务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await stopTask(row.id)
    ElMessage.success('任务已停止')
    loadData()
  }).catch(() => {})
}

const viewExecutions = (row) => {
  router.push(`/task/${row.id}/executions`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteTask(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadDatasources()
})
</script>

<style scoped>
.task-container {
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
