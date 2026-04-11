<template>
  <div class="quality-container">
    <div class="page-header">
      <h2>数据质量管理</h2>
      <p>配置和执行数据质量检查规则，监控数据质量状况</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalRules }}</div>
              <div class="stat-label">规则总数</div>
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
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pass">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.passCount }}</div>
              <div class="stat-label">检查通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon fail">
              <el-icon><WarningFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.failCount }}</div>
              <div class="stat-label">检查失败</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="质量规则" name="rules">
        <div class="toolbar">
          <el-button type="primary" @click="showRuleDialog()">
            <el-icon><Plus /></el-icon>
            新建规则
          </el-button>
          <el-button @click="executeAllRules" :loading="executing">
            <el-icon><VideoPlay /></el-icon>
            执行全部
          </el-button>
        </div>

        <el-table :data="rules" v-loading="loading" style="width: 100%">
          <el-table-column prop="ruleName" label="规则名称" min-width="150" />
          <el-table-column prop="ruleType" label="规则类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getRuleTypeTag(row.ruleType)" size="small">
                {{ getRuleTypeText(row.ruleType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="tableName" label="目标表" width="150" />
          <el-table-column prop="columnName" label="目标列" width="120" />
          <el-table-column prop="severity" label="严重级别" width="100">
            <template #default="{ row }">
              <el-tag :type="getSeverityTag(row.severity)" size="small">
                {{ row.severity }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
                         @change="handleStatusChange(row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="executeRule(row)">执行</el-button>
              <el-button type="primary" link @click="showRuleDialog(row)">编辑</el-button>
              <el-button type="danger" link @click="deleteRule(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="检查结果" name="results">
        <el-form :inline="true" :model="resultQuery" style="margin-bottom: 16px">
          <el-form-item label="数据源">
            <el-select v-model="resultQuery.datasourceId" placeholder="选择数据源" clearable>
              <el-option v-for="ds in datasources" :key="ds.id" :label="ds.name" :value="ds.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadResults">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="results" v-loading="resultsLoading" style="width: 100%">
          <el-table-column prop="ruleName" label="规则名称" min-width="150" />
          <el-table-column prop="tableName" label="表名" width="150" />
          <el-table-column prop="checkStatus" label="检查状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.checkStatus)" size="small">
                {{ getStatusText(row.checkStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalRecords" label="总记录数" width="100" />
          <el-table-column prop="passRecords" label="通过数" width="100" />
          <el-table-column prop="failRecords" label="失败数" width="100" />
          <el-table-column prop="passRate" label="通过率" width="100">
            <template #default="{ row }">
              <span :style="{ color: row.passRate >= 95 ? '#67C23A' : row.passRate >= 80 ? '#E6A23C' : '#F56C6C' }">
                {{ row.passRate ? row.passRate.toFixed(2) + '%' : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="checkTime" label="检查时间" width="160" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 规则编辑对话框 -->
    <el-dialog v-model="ruleDialogVisible" :title="ruleForm.id ? '编辑规则' : '新建规则'" width="600px">
      <el-form :model="ruleForm" label-width="100px">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则类型" required>
          <el-select v-model="ruleForm.ruleType" placeholder="选择规则类型">
            <el-option label="完整性检查" value="COMPLETENESS" />
            <el-option label="唯一性检查" value="UNIQUENESS" />
            <el-option label="准确性检查" value="ACCURACY" />
            <el-option label="一致性检查" value="CONSISTENCY" />
            <el-option label="时效性检查" value="TIMELINESS" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据源" required>
          <el-select v-model="ruleForm.datasourceId" placeholder="选择数据源">
            <el-option v-for="ds in datasources" :key="ds.id" :label="ds.name" :value="ds.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="表名" required>
          <el-input v-model="ruleForm.tableName" placeholder="请输入表名" />
        </el-form-item>
        <el-form-item label="列名">
          <el-input v-model="ruleForm.columnName" placeholder="请输入列名" />
        </el-form-item>
        <el-form-item label="规则表达式">
          <el-input v-model="ruleForm.ruleExpression" type="textarea" :rows="3"
                    placeholder="自定义检查条件，如：age > 0 AND age < 150" />
        </el-form-item>
        <el-form-item label="严重级别">
          <el-select v-model="ruleForm.severity" placeholder="选择严重级别">
            <el-option label="HIGH" value="HIGH" />
            <el-option label="MEDIUM" value="MEDIUM" />
            <el-option label="LOW" value="LOW" />
          </el-select>
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
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getQualityRules, createQualityRule, updateQualityRule, deleteQualityRule,
  updateQualityRuleStatus, executeQualityCheck, executeAllQualityChecks,
  getQualityResults, getQualityFailCount
} from '@/api/quality'
import { getDatasources } from '@/api/datasource'

const activeTab = ref('rules')
const loading = ref(false)
const rules = ref([])
const datasources = ref([])
const results = ref([])
const resultsLoading = ref(false)
const executing = ref(false)
const saving = ref(false)
const ruleDialogVisible = ref(false)

const stats = reactive({
  totalRules: 0,
  activeRules: 0,
  passCount: 0,
  failCount: 0
})

const resultQuery = reactive({
  datasourceId: null
})

const ruleForm = reactive({
  id: null,
  ruleName: '',
  ruleType: '',
  datasourceId: null,
  tableName: '',
  columnName: '',
  ruleExpression: '',
  severity: 'MEDIUM',
  description: '',
  status: 1
})

const getRuleTypeTag = (type) => {
  const map = {
    COMPLETENESS: 'primary',
    UNIQUENESS: 'success',
    ACCURACY: 'warning',
    CONSISTENCY: 'info',
    TIMELINESS: 'danger'
  }
  return map[type] || ''
}

const getRuleTypeText = (type) => {
  const map = {
    COMPLETENESS: '完整性',
    UNIQUENESS: '唯一性',
    ACCURACY: '准确性',
    CONSISTENCY: '一致性',
    TIMELINESS: '时效性'
  }
  return map[type] || type
}

const getSeverityTag = (severity) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[severity] || ''
}

const getStatusTag = (status) => {
  const map = { PASS: 'success', WARNING: 'warning', FAIL: 'danger' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { PASS: '通过', WARNING: '警告', FAIL: '失败' }
  return map[status] || status
}

const loadRules = async () => {
  loading.value = true
  try {
    const res = await getQualityRules()
    rules.value = res.data || []
    stats.totalRules = rules.value.length
    stats.activeRules = rules.value.filter(r => r.status === 1).length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadDatasources = async () => {
  try {
    const res = await getDatasources()
    datasources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadResults = async () => {
  if (!resultQuery.datasourceId) {
    ElMessage.warning('请选择数据源')
    return
  }
  resultsLoading.value = true
  try {
    const res = await getQualityResults(resultQuery.datasourceId)
    results.value = res.data?.content || []
  } catch (e) {
    console.error(e)
  } finally {
    resultsLoading.value = false
  }
}

const showRuleDialog = (rule = null) => {
  if (rule) {
    Object.assign(ruleForm, rule)
  } else {
    Object.assign(ruleForm, {
      id: null,
      ruleName: '',
      ruleType: '',
      datasourceId: null,
      tableName: '',
      columnName: '',
      ruleExpression: '',
      severity: 'MEDIUM',
      description: '',
      status: 1
    })
  }
  ruleDialogVisible.value = true
}

const saveRule = async () => {
  if (!ruleForm.ruleName || !ruleForm.ruleType || !ruleForm.datasourceId || !ruleForm.tableName) {
    ElMessage.warning('请填写必填项')
    return
  }
  saving.value = true
  try {
    if (ruleForm.id) {
      await updateQualityRule(ruleForm.id, ruleForm)
    } else {
      await createQualityRule(ruleForm)
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
    await deleteQualityRule(rule.id)
    ElMessage.success('删除成功')
    loadRules()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleStatusChange = async (rule) => {
  try {
    await updateQualityRuleStatus(rule.id, rule.status)
    ElMessage.success('状态更新成功')
  } catch (e) {
    rule.status = rule.status === 1 ? 0 : 1
  }
}

const executeRule = async (rule) => {
  try {
    ElMessage.info('开始执行检查...')
    await executeQualityCheck(rule.id)
    ElMessage.success('检查执行完成')
    loadRules()
  } catch (e) {
    console.error(e)
  }
}

const executeAllRules = async () => {
  try {
    executing.value = true
    ElMessage.info('开始执行所有启用的规则...')
    await executeAllQualityChecks()
    ElMessage.success('全部检查执行完成')
    loadRules()
  } catch (e) {
    console.error(e)
  } finally {
    executing.value = false
  }
}

onMounted(() => {
  loadRules()
  loadDatasources()
})
</script>

<style scoped>
.quality-container {
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

.stat-icon.total { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-icon.active { background: linear-gradient(135deg, #67C23A, #85ce61); }
.stat-icon.pass { background: linear-gradient(135deg, #67C23A, #85ce61); }
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

.toolbar {
  margin-bottom: 16px;
}
</style>
