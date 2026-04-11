<template>
  <div class="alert-container">
    <div class="page-header">
      <h2>监控告警</h2>
      <p>配置告警规则，监控系统和任务状态，及时发送告警通知</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCount }}</div>
              <div class="stat-label">待处理告警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon critical">
              <el-icon><WarningFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.criticalCount }}</div>
              <div class="stat-label">严重告警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rules">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalRules }}</div>
              <div class="stat-label">告警规则</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeRules }}</div>
              <div class="stat-label">启用规则</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="告警记录" name="records">
        <el-form :inline="true" style="margin-bottom: 16px">
          <el-form-item>
            <el-select v-model="recordQuery.status" placeholder="告警状态" clearable @change="loadRecords">
              <el-option label="待处理" value="PENDING" />
              <el-option label="已发送" value="SENT" />
              <el-option label="已确认" value="ACKNOWLEDGED" />
              <el-option label="已解决" value="RESOLVED" />
            </el-select>
          </el-form-item>
        </el-form>

        <el-table :data="records" v-loading="recordsLoading" style="width: 100%">
          <el-table-column prop="ruleName" label="规则名称" min-width="150" />
          <el-table-column prop="alertType" label="告警类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getAlertTypeText(row.alertType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertLevel" label="告警级别" width="100">
            <template #default="{ row }">
              <el-tag :type="getLevelTag(row.alertLevel)" size="small">
                {{ row.alertLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertContent" label="告警内容" min-width="250" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertTime" label="告警时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <template v-if="row.status === 'PENDING' || row.status === 'SENT'">
                <el-button type="primary" link @click="acknowledgeRecord(row)">确认</el-button>
              </template>
              <template v-if="row.status !== 'RESOLVED'">
                <el-button type="success" link @click="resolveRecord(row)">解决</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="recordQuery.page"
          :page-size="10"
          :total="totalRecords"
          layout="total, prev, pager, next"
          @current-change="loadRecords"
          style="margin-top: 16px; justify-content: flex-end"
        />
      </el-tab-pane>

      <el-tab-pane label="告警规则" name="rules">
        <div class="toolbar">
          <el-button type="primary" @click="showRuleDialog()">
            <el-icon><Plus /></el-icon>
            新建规则
          </el-button>
        </div>

        <el-table :data="rules" v-loading="rulesLoading" style="width: 100%">
          <el-table-column prop="ruleName" label="规则名称" min-width="150" />
          <el-table-column prop="alertType" label="告警类型" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getAlertTypeText(row.alertType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="alertLevel" label="告警级别" width="100">
            <template #default="{ row }">
              <el-tag :type="getLevelTag(row.alertLevel)" size="small">
                {{ row.alertLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="notifyChannel" label="通知渠道" width="100" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
                         @change="handleRuleStatusChange(row)" />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="showRuleDialog(row)">编辑</el-button>
              <el-button type="danger" link @click="deleteRule(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 规则编辑对话框 -->
    <el-dialog v-model="ruleDialogVisible" :title="ruleForm.id ? '编辑规则' : '新建规则'" width="550px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="告警类型" required>
          <el-select v-model="ruleForm.alertType" placeholder="选择告警类型">
            <el-option label="任务失败" value="TASK_FAIL" />
            <el-option label="任务超时" value="TASK_TIMEOUT" />
            <el-option label="质量检查失败" value="QUALITY_FAIL" />
            <el-option label="系统告警" value="SYSTEM" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警级别" required>
          <el-select v-model="ruleForm.alertLevel" placeholder="选择告警级别">
            <el-option label="CRITICAL" value="CRITICAL" />
            <el-option label="HIGH" value="HIGH" />
            <el-option label="MEDIUM" value="MEDIUM" />
            <el-option label="LOW" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知渠道" required>
          <el-select v-model="ruleForm.notifyChannel" placeholder="选择通知渠道">
            <el-option label="邮件" value="EMAIL" />
            <el-option label="短信" value="SMS" />
            <el-option label="Webhook" value="WEBHOOK" />
            <el-option label="消息队列" value="MQ" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知配置">
          <el-input v-model="ruleForm.notifyConfig" type="textarea" :rows="3"
                    placeholder="通知配置（JSON格式）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="ruleForm.description" type="textarea" :rows="2" placeholder="规则描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 解决对话框 -->
    <el-dialog v-model="resolveDialogVisible" title="解决告警" width="450px">
      <el-form label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="resolveRemark" type="textarea" :rows="3" placeholder="请输入解决说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResolve">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAlertRules, createAlertRule, updateAlertRule, deleteAlertRule,
  updateAlertRuleStatus, getAlertRecords, acknowledgeAlert, resolveAlert,
  getPendingAlertCount, getCriticalAlertCount
} from '@/api/alert'

const activeTab = ref('records')
const rulesLoading = ref(false)
const recordsLoading = ref(false)
const saving = ref(false)
const rules = ref([])
const records = ref([])
const totalRecords = ref(0)
const ruleDialogVisible = ref(false)
const resolveDialogVisible = ref(false)
const currentRecord = ref(null)
const resolveRemark = ref('')

const stats = reactive({
  pendingCount: 0,
  criticalCount: 0,
  totalRules: 0,
  activeRules: 0
})

const recordQuery = reactive({
  status: null,
  page: 1
})

const ruleForm = reactive({
  id: null,
  ruleName: '',
  alertType: '',
  alertLevel: 'MEDIUM',
  notifyChannel: 'EMAIL',
  notifyConfig: '',
  description: '',
  status: 1
})

const getAlertTypeText = (type) => {
  const map = {
    TASK_FAIL: '任务失败',
    TASK_TIMEOUT: '任务超时',
    QUALITY_FAIL: '质量失败',
    SYSTEM: '系统告警'
  }
  return map[type] || type
}

const getLevelTag = (level) => {
  const map = { CRITICAL: 'danger', HIGH: 'warning', MEDIUM: '', LOW: 'info' }
  return map[level] || ''
}

const getStatusTag = (status) => {
  const map = { PENDING: 'warning', SENT: 'info', ACKNOWLEDGED: '', RESOLVED: 'success' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { PENDING: '待处理', SENT: '已发送', ACKNOWLEDGED: '已确认', RESOLVED: '已解决' }
  return map[status] || status
}

const loadStats = async () => {
  try {
    const [pendingRes, criticalRes] = await Promise.all([
      getPendingAlertCount(),
      getCriticalAlertCount()
    ])
    stats.pendingCount = pendingRes.data || 0
    stats.criticalCount = criticalRes.data || 0
  } catch (e) {
    console.error(e)
  }
}

const loadRules = async () => {
  rulesLoading.value = true
  try {
    const res = await getAlertRules()
    rules.value = res.data || []
    stats.totalRules = rules.value.length
    stats.activeRules = rules.value.filter(r => r.status === 1).length
  } catch (e) {
    console.error(e)
  } finally {
    rulesLoading.value = false
  }
}

const loadRecords = async () => {
  recordsLoading.value = true
  try {
    const res = await getAlertRecords(recordQuery.page - 1, 10)
    records.value = res.data?.content || []
    totalRecords.value = res.data?.totalElements || 0
  } catch (e) {
    console.error(e)
  } finally {
    recordsLoading.value = false
  }
}

const showRuleDialog = (rule = null) => {
  if (rule) {
    Object.assign(ruleForm, rule)
  } else {
    Object.assign(ruleForm, {
      id: null,
      ruleName: '',
      alertType: '',
      alertLevel: 'MEDIUM',
      notifyChannel: 'EMAIL',
      notifyConfig: '',
      description: '',
      status: 1
    })
  }
  ruleDialogVisible.value = true
}

const saveRule = async () => {
  if (!ruleForm.ruleName || !ruleForm.alertType || !ruleForm.alertLevel || !ruleForm.notifyChannel) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    if (ruleForm.id) {
      await updateAlertRule(ruleForm.id, ruleForm)
    } else {
      await createAlertRule(ruleForm)
    }
    ElMessage.success('保存成功')
    ruleDialogVisible.value = false
    loadRules()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const deleteRule = async (rule) => {
  try {
    await ElMessageBox.confirm('确定删除该规则？', '提示', { type: 'warning' })
    await deleteAlertRule(rule.id)
    ElMessage.success('删除成功')
    loadRules()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleRuleStatusChange = async (rule) => {
  try {
    await updateAlertRuleStatus(rule.id, rule.status)
    ElMessage.success('状态更新成功')
  } catch (e) {
    rule.status = rule.status === 1 ? 0 : 1
  }
}

const acknowledgeRecord = async (record) => {
  try {
    await acknowledgeAlert(record.id, 'admin')
    ElMessage.success('已确认')
    loadRecords()
    loadStats()
  } catch (e) {
    console.error(e)
  }
}

const resolveRecord = (record) => {
  currentRecord.value = record
  resolveRemark.value = ''
  resolveDialogVisible.value = true
}

const confirmResolve = async () => {
  try {
    await resolveAlert(currentRecord.value.id, 'admin', resolveRemark.value)
    ElMessage.success('已解决')
    resolveDialogVisible.value = false
    loadRecords()
    loadStats()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadStats()
  loadRules()
  loadRecords()
})
</script>

<style scoped>
.alert-container {
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

.stat-icon.pending { background: linear-gradient(135deg, #E6A23C, #f0c78a); }
.stat-icon.critical { background: linear-gradient(135deg, #F56C6C, #f78989); }
.stat-icon.rules { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-icon.active { background: linear-gradient(135deg, #67C23A, #85ce61); }

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

.toolbar {
  margin-bottom: 16px;
}
</style>
