<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>仪表盘</h2>
        <p>实时监控数据源、任务执行状态和系统运行情况</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon datasource">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.datasourceCount }}</div>
              <div class="stat-label">数据源数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon task">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.taskCount }}</div>
              <div class="stat-label">任务数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.successCount }}</div>
              <div class="stat-label">成功执行</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon failed">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.failedCount }}</div>
              <div class="stat-label">失败执行</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近任务执行记录 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近任务执行记录</span>
              <el-button type="text" @click="viewAllExecutions">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentExecutions" style="width: 100%" v-loading="loading">
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="executionType" label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="row.executionType === 'MANUAL' ? 'primary' : 'success'" size="small">
                  {{ row.executionType === 'MANUAL' ? '手动' : '定时' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="160" />
            <el-table-column prop="duration" label="耗时" width="80">
              <template #default="{ row }">
                {{ row.duration }}秒
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>数据源分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div v-if="datasourceStats.length === 0" class="empty-chart">
              暂无数据源统计信息
            </div>
            <div v-else class="distribution-list">
              <div v-for="item in datasourceStats" :key="item.type" class="distribution-item">
                <div class="distribution-info">
                  <span class="distribution-type">{{ item.type }}</span>
                  <span class="distribution-count">{{ item.count }} 个</span>
                </div>
                <el-progress :percentage="item.percentage" :color="getProgressColor(item.type)" />
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>任务状态分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div v-if="taskStats.length === 0" class="empty-chart">
              暂无任务统计信息
            </div>
            <div v-else class="distribution-list">
              <div v-for="item in taskStats" :key="item.status" class="distribution-item">
                <div class="distribution-info">
                  <span class="distribution-type">{{ item.statusText }}</span>
                  <span class="distribution-count">{{ item.count }} 个</span>
                </div>
                <el-progress :percentage="item.percentage" :color="getStatusProgressColor(item.status)" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboardStats, getRecentExecutions } from '@/api/dashboard'

const router = useRouter()
const loading = ref(false)

const stats = ref({
  datasourceCount: 0,
  taskCount: 0,
  successCount: 0,
  failedCount: 0
})

const recentExecutions = ref([])
const datasourceStats = ref([])
const taskStats = ref([])

const getStatusTag = (status) => {
  const map = { RUNNING: 'warning', COMPLETED: 'success', FAILED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { RUNNING: '运行中', COMPLETED: '已完成', FAILED: '失败' }
  return map[status] || status
}

const getProgressColor = (type) => {
  const colors = {
    MYSQL: '#409EFF',
    HIVE: '#F56C6C',
    ORACLE: '#67C23A',
    POSTGRESQL: '#E6A23C'
  }
  return colors[type] || '#909399'
}

const getStatusProgressColor = (status) => {
  const colors = {
    COMPLETED: '#67C23A',
    RUNNING: '#E6A23C',
    FAILED: '#F56C6C'
  }
  return colors[status] || '#909399'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    const data = res.data || {}
    stats.value = {
      datasourceCount: data.datasourceCount || 0,
      taskCount: data.taskCount || 0,
      successCount: data.successCount || 0,
      failedCount: data.failedCount || 0
    }
    datasourceStats.value = data.datasourceStats || []
    taskStats.value = data.taskStats || []

    const execRes = await getRecentExecutions()
    recentExecutions.value = execRes.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewAllExecutions = () => {
  router.push('/task')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-container {
  height: 100%;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
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

/* 统计卡片 */
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  border: none;
  height: 100%;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card :deep(.el-card__body) {
  padding: 24px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.stat-icon.datasource {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}

.stat-icon.task {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-icon.success {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-icon.failed {
  background: linear-gradient(135deg, #F56C6C, #f78989);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

/* 卡片样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  font-weight: 600;
  color: #303133;
}

.card-header .el-button {
  font-weight: normal;
}

/* 图表容器 */
.chart-container {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.empty-chart {
  color: #909399;
  font-size: 14px;
}

/* 分布列表 */
.distribution-list {
  width: 100%;
  padding: 16px 20px;
}

.distribution-item {
  margin-bottom: 16px;
}

.distribution-item:last-child {
  margin-bottom: 0;
}

.distribution-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.distribution-type {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.distribution-count {
  font-size: 13px;
  color: #909399;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 0;
}

:deep(.el-table__header) {
  background-color: #fafafa;
}

:deep(.el-table__row) {
  height: 48px;
}

:deep(.el-table__cell) {
  padding: 8px 0;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .stat-value {
    font-size: 22px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
    margin-right: 12px;
  }
}
</style>
