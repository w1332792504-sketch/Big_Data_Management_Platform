<template>
  <div class="executions-container">
    <el-page-header @back="goBack" :title="'执行记录'">
      <template #content>
        <span class="page-extra">任务：{{ taskName }}</span>
      </template>
    </el-page-header>

    <el-card class="box-card" style="margin-top: 20px">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="executionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.executionType === 'MANUAL' ? 'primary' : 'success'">
              {{ row.executionType === 'MANUAL' ? '手动' : '定时' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="耗时 (秒)" width="100" />
        <el-table-column prop="extractCount" label="抽取记录数" width="120" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column label="详情" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">查看详情</el-button>
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

    <!-- 执行详情对话框 -->
    <el-dialog v-model="detailVisible" title="执行详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="执行 ID">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ currentDetail.taskName }}</el-descriptions-item>
        <el-descriptions-item label="执行类型">
          <el-tag :type="currentDetail.executionType === 'MANUAL' ? 'primary' : 'success'">
            {{ currentDetail.executionType === 'MANUAL' ? '手动' : '定时' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentDetail.status)">
            {{ getStatusText(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentDetail.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentDetail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="执行耗时">{{ currentDetail.duration }} 秒</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentDetail.operator }}</el-descriptions-item>
        <el-descriptions-item label="源表记录数">{{ currentDetail.sourceCount }}</el-descriptions-item>
        <el-descriptions-item label="目标表记录数">{{ currentDetail.targetCount }}</el-descriptions-item>
        <el-descriptions-item label="抽取记录数">{{ currentDetail.extractCount }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>错误信息</el-divider>
      <el-alert
        v-if="currentDetail.errorMsg"
        title="执行出错"
        type="error"
        :closable="false"
        show-icon
      >
        <pre style="max-height: 200px; overflow: auto">{{ currentDetail.errorMsg }}</pre>
      </el-alert>
      <el-empty v-else description="无错误信息" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTaskById, getTaskExecutions } from '@/api/task'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detailVisible = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const taskName = ref('')
const currentDetail = ref({})

const getStatusTag = (status) => {
  const map = { RUNNING: 'warning', SUCCESS: 'success', FAILED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { RUNNING: '运行中', SUCCESS: '成功', FAILED: '失败' }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const taskRes = await getTaskById(route.params.id)
    taskName.value = taskRes.data.taskName

    const res = await getTaskExecutions(route.params.id, currentPage.value - 1, pageSize.value)
    tableData.value = res.data.content || res.data.elements || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.executions-container {
  height: 100%;
}

.page-extra {
  font-size: 14px;
  color: #909399;
  margin-left: 16px;
}
</style>
