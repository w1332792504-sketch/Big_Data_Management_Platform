<template>
  <div class="mq-monitor-container">
    <div class="page-header">
      <div class="header-info">
        <h2>消息队列监控</h2>
        <p>RabbitMQ 队列状态监控、消息测试与消费追踪</p>
      </div>
      <el-button type="primary" @click="loadQueueStatus" :loading="loading">
        <el-icon><Refresh /></el-icon>
        刷新状态
      </el-button>
    </div>

    <!-- 队列概览 -->
    <div class="queue-overview">
      <el-card v-for="queue in queues" :key="queue.name" class="queue-card" shadow="hover">
        <div class="queue-header">
          <div class="queue-icon" :style="{ background: queue.color }">
            <el-icon :size="20"><ChatDotRound /></el-icon>
          </div>
          <div class="queue-info">
            <h3>{{ queue.label }}</h3>
            <p>{{ queue.name }}</p>
          </div>
          <el-tag :type="queue.connected ? 'success' : 'danger'" size="small">
            {{ queue.connected ? '已连接' : '未连接' }}
          </el-tag>
        </div>
        <div class="queue-stats">
          <div class="stat-item">
            <span class="stat-value">{{ queue.messages }}</span>
            <span class="stat-label">待消费消息</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ queue.consumers }}</span>
            <span class="stat-label">消费者</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ queue.consumed }}</span>
            <span class="stat-label">已消费</span>
          </div>
        </div>
        <div class="queue-actions">
          <el-button size="small" type="primary" @click="openTestDialog(queue)">
            发送测试消息
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 最近消息记录 -->
    <el-card class="message-log-card">
      <template #header>
        <div class="card-header">
          <span>最近消息记录</span>
          <el-button text @click="clearLogs">清空</el-button>
        </div>
      </template>
      <el-table :data="messageLogs" style="width: 100%" max-height="400" v-loading="loading">
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="queue" label="队列" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === '发送' ? 'primary' : 'success'" size="small">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 测试消息对话框 -->
    <el-dialog v-model="testDialogVisible" title="发送测试消息" width="500px">
      <el-form :model="testForm" label-width="80px">
        <el-form-item label="目标队列">
          <el-input :value="testForm.queueLabel" disabled />
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input v-model="testForm.message" type="textarea" :rows="4" placeholder="请输入测试消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendTestMessage" :loading="sending">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, ChatDotRound } from '@element-plus/icons-vue'
import request from '@/api/request'

const loading = ref(false)
const sending = ref(false)
const testDialogVisible = ref(false)

const queues = ref([
  {
    name: 'data.task.execute',
    label: '任务执行队列',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    connected: true,
    messages: 0,
    consumers: 1,
    consumed: 0
  },
  {
    name: 'data.task.notify',
    label: '任务通知队列',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    connected: true,
    messages: 0,
    consumers: 1,
    consumed: 0
  },
  {
    name: 'data.sync.execute',
    label: '数据同步队列',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    connected: true,
    messages: 0,
    consumers: 1,
    consumed: 0
  }
])

const messageLogs = ref([])

const testForm = reactive({
  queueName: '',
  queueLabel: '',
  message: ''
})

let refreshTimer = null

const loadQueueStatus = async () => {
  loading.value = true
  try {
    const res = await request({ url: '/mq/status', method: 'get' })
    const data = res.data || {}
    queues.value.forEach(q => {
      if (data[q.name]) {
        q.messages = data[q.name].messages ?? q.messages
        q.consumers = data[q.name].consumers ?? q.consumers
        q.consumed = data[q.name].consumed ?? q.consumed
        q.connected = data[q.name].connected !== false
      }
    })
  } catch (e) {
    console.error('Failed to load queue status:', e)
  } finally {
    loading.value = false
  }
}

const openTestDialog = (queue) => {
  testForm.queueName = queue.name
  testForm.queueLabel = queue.label
  testForm.message = `测试消息 - ${new Date().toLocaleString()}`
  testDialogVisible.value = true
}

const sendTestMessage = async () => {
  if (!testForm.message.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  sending.value = true
  try {
    await request({
      url: '/mq/test',
      method: 'post',
      params: { queue: testForm.queueName, message: testForm.message }
    })
    ElMessage.success('测试消息已发送')
    messageLogs.value.unshift({
      time: new Date().toLocaleString(),
      queue: testForm.queueLabel,
      type: '发送',
      message: testForm.message,
      status: '成功'
    })
    testDialogVisible.value = false
    loadQueueStatus()
  } catch (e) {
    messageLogs.value.unshift({
      time: new Date().toLocaleString(),
      queue: testForm.queueLabel,
      type: '发送',
      message: testForm.message,
      status: '失败'
    })
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

const clearLogs = () => {
  messageLogs.value = []
}

onMounted(() => {
  loadQueueStatus()
  refreshTimer = setInterval(loadQueueStatus, 30000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<style scoped>
.mq-monitor-container {
  height: 100%;
}

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

.queue-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.queue-card {
  border-radius: 8px;
}

.queue-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.queue-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.queue-info h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.queue-info p {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}

.queue-stats {
  display: flex;
  justify-content: space-around;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.queue-actions {
  text-align: right;
}

.message-log-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
