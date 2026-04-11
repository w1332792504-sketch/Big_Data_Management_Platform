<template>
  <div class="audit-container">
    <div class="page-header">
      <h2>审计日志</h2>
      <p>查看系统操作记录，追踪用户行为和系统事件</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon today">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayCount }}</div>
              <div class="stat-label">今日操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.successCount }}</div>
              <div class="stat-label">成功操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon fail">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.failCount }}</div>
              <div class="stat-label">失败操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="模块">
          <el-select v-model="queryForm.module" placeholder="选择模块" clearable>
            <el-option label="数据源" value="DATASOURCE" />
            <el-option label="任务" value="TASK" />
            <el-option label="用户" value="USER" />
            <el-option label="系统" value="SYSTEM" />
            <el-option label="质量" value="QUALITY" />
            <el-option label="告警" value="ALERT" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="queryForm.operator" placeholder="操作人" clearable />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLogs">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="hover">
      <el-table :data="logs" v-loading="loading" style="width: 100%">
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getModuleText(row.module) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'" size="small">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时" width="80">
          <template #default="{ row }">
            {{ row.duration ? row.duration + 'ms' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        :page-size="10"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadLogs"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="日志详情" width="700px">
      <el-descriptions :column="2" border v-if="currentLog">
        <el-descriptions-item label="模块">{{ getModuleText(currentLog.module) }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentLog.description }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.operator }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ currentLog.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 'SUCCESS' ? 'success' : 'danger'" size="small">
            {{ currentLog.status === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentLog.duration ? currentLog.duration + 'ms' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-input v-model="currentLog.requestParams" type="textarea" :rows="3" readonly />
        </el-descriptions-item>
        <el-descriptions-item label="响应数据" :span="2">
          <el-input v-model="currentLog.responseData" type="textarea" :rows="3" readonly />
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.errorMsg">
          <el-input v-model="currentLog.errorMsg" type="textarea" :rows="2" readonly />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAuditLogs, getAuditLogsByModule, getAuditLogsByOperator, getAuditLogsByTimeRange, getTodayAuditCount } from '@/api/audit'

const loading = ref(false)
const logs = ref([])
const total = ref(0)
const detailDialogVisible = ref(false)
const currentLog = ref(null)

const stats = reactive({
  todayCount: 0,
  successCount: 0,
  failCount: 0
})

const queryForm = reactive({
  module: null,
  operator: '',
  timeRange: null,
  page: 1
})

const getModuleText = (module) => {
  const map = {
    DATASOURCE: '数据源',
    TASK: '任务',
    USER: '用户',
    SYSTEM: '系统',
    QUALITY: '质量',
    ALERT: '告警'
  }
  return map[module] || module
}

const loadStats = async () => {
  try {
    const res = await getTodayAuditCount()
    stats.todayCount = res.data || 0
  } catch (e) {
    console.error(e)
  }
}

const loadLogs = async () => {
  loading.value = true
  try {
    let res
    const page = queryForm.page - 1

    if (queryForm.timeRange && queryForm.timeRange.length === 2) {
      res = await getAuditLogsByTimeRange(queryForm.timeRange[0], queryForm.timeRange[1], page, 10)
    } else if (queryForm.operator) {
      res = await getAuditLogsByOperator(queryForm.operator, page, 10)
    } else if (queryForm.module) {
      res = await getAuditLogsByModule(queryForm.module, page, 10)
    } else {
      res = await getAuditLogs(page, 10)
    }

    logs.value = res.data?.content || []
    total.value = res.data?.totalElements || 0

    // 计算成功/失败数
    stats.successCount = logs.value.filter(l => l.status === 'SUCCESS').length
    stats.failCount = logs.value.filter(l => l.status === 'FAIL').length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.module = null
  queryForm.operator = ''
  queryForm.timeRange = null
  queryForm.page = 1
  loadLogs()
}

const showDetail = (log) => {
  currentLog.value = log
  detailDialogVisible.value = true
}

onMounted(() => {
  loadStats()
  loadLogs()
})
</script>

<style scoped>
.audit-container {
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  border: none;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: white;
}

.stat-icon.today { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-icon.success { background: linear-gradient(135deg, #67C23A, #85ce61); }
.stat-icon.fail { background: linear-gradient(135deg, #F56C6C, #f78989); }

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
</style>
