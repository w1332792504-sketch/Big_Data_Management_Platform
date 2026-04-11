<template>
  <div class="metadata-container">
    <div class="page-header">
      <h2>元数据管理</h2>
      <p>查看和管理数据源的元数据信息，支持元数据同步和血缘分析</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>数据源列表</span>
          </template>
          <div class="datasource-list">
            <div v-for="ds in datasources" :key="ds.id"
                 :class="['datasource-item', { active: selectedDatasource?.id === ds.id }]"
                 @click="selectDatasource(ds)">
              <el-icon><Database /></el-icon>
              <span>{{ ds.name }}</span>
              <el-tag size="small" type="info">{{ ds.type }}</el-tag>
            </div>
            <el-empty v-if="datasources.length === 0" description="暂无数据源" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card shadow="hover" v-if="selectedDatasource">
          <template #header>
            <div class="card-header">
              <span>{{ selectedDatasource.name }} - 元数据</span>
              <el-button type="primary" @click="syncMetadata" :loading="syncing">
                <el-icon><Refresh /></el-icon>
                同步元数据
              </el-button>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="表结构" name="tables">
              <el-table :data="tableMetadata" v-loading="loading" style="width: 100%">
                <el-table-column prop="databaseName" label="数据库" width="150" />
                <el-table-column prop="tableName" label="表名" min-width="200" />
                <el-table-column prop="rowCount" label="行数" width="120">
                  <template #default="{ row }">
                    {{ row.rowCount?.toLocaleString() || '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="comment" label="注释" min-width="200" show-overflow-tooltip />
                <el-table-column prop="lastSyncTime" label="同步时间" width="160" />
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="showColumns(row)">查看列</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="列信息" name="columns">
              <el-form :inline="true" style="margin-bottom: 16px">
                <el-form-item label="数据库">
                  <el-select v-model="selectedDatabase" placeholder="选择数据库" clearable @change="loadTables">
                    <el-option v-for="db in databases" :key="db" :label="db" :value="db" />
                  </el-select>
                </el-form-item>
                <el-form-item label="表">
                  <el-select v-model="selectedTable" placeholder="选择表" clearable @change="loadColumns">
                    <el-option v-for="t in tables" :key="t" :label="t" :value="t" />
                  </el-select>
                </el-form-item>
              </el-form>

              <el-table :data="columnMetadata" v-loading="columnsLoading" style="width: 100%">
                <el-table-column prop="columnName" label="列名" min-width="150" />
                <el-table-column prop="columnType" label="类型" width="120" />
                <el-table-column label="长度" width="80">
                  <template #default="{ row }">
                    {{ row.columnLength || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="可空" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.isNullable ? 'success' : 'danger'" size="small">
                      {{ row.isNullable ? '是' : '否' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="主键" width="80">
                  <template #default="{ row }">
                    <el-tag v-if="row.isPrimaryKey" type="warning" size="small">主键</el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column prop="defaultValue" label="默认值" width="120" show-overflow-tooltip />
                <el-table-column prop="comment" label="注释" min-width="200" show-overflow-tooltip />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <el-empty v-else description="请选择数据源" />
      </el-col>
    </el-row>

    <!-- 列信息对话框 -->
    <el-dialog v-model="columnsDialogVisible" :title="currentTable?.tableName + ' - 列信息'" width="900px">
      <el-table :data="currentTableColumns" v-loading="columnsLoading" style="width: 100%">
        <el-table-column prop="columnName" label="列名" min-width="150" />
        <el-table-column prop="columnType" label="类型" width="120" />
        <el-table-column label="可空" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isNullable ? 'success' : 'danger'" size="small">
              {{ row.isNullable ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主键" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isPrimaryKey" type="warning" size="small">主键</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="注释" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  syncMetadata as syncMetadataApi,
  getDatabases, getTables, getColumns,
  getTableMetadata, getMetadataByDatasource
} from '@/api/metadata'
import { getDatasources } from '@/api/datasource'

const activeTab = ref('tables')
const loading = ref(false)
const syncing = ref(false)
const columnsLoading = ref(false)
const datasources = ref([])
const selectedDatasource = ref(null)
const tableMetadata = ref([])
const columnMetadata = ref([])
const databases = ref([])
const tables = ref([])
const selectedDatabase = ref(null)
const selectedTable = ref(null)
const columnsDialogVisible = ref(false)
const currentTable = ref(null)
const currentTableColumns = ref([])

const loadDatasources = async () => {
  try {
    const res = await getDatasources()
    datasources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const selectDatasource = async (ds) => {
  selectedDatasource.value = ds
  await loadMetadata()
  await loadDatabases()
}

const loadMetadata = async () => {
  if (!selectedDatasource.value) return
  loading.value = true
  try {
    const res = await getTableMetadata(selectedDatasource.value.id)
    tableMetadata.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadDatabases = async () => {
  if (!selectedDatasource.value) return
  try {
    const res = await getDatabases(selectedDatasource.value.id)
    databases.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadTables = async () => {
  if (!selectedDatasource.value || !selectedDatabase.value) {
    tables.value = []
    return
  }
  try {
    const res = await getTables(selectedDatasource.value.id, selectedDatabase.value)
    tables.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const loadColumns = async () => {
  if (!selectedDatasource.value || !selectedDatabase.value || !selectedTable.value) {
    columnMetadata.value = []
    return
  }
  columnsLoading.value = true
  try {
    const res = await getColumns(selectedDatasource.value.id, selectedDatabase.value, selectedTable.value)
    columnMetadata.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    columnsLoading.value = false
  }
}

const syncMetadata = async () => {
  if (!selectedDatasource.value) return
  syncing.value = true
  try {
    await syncMetadataApi(selectedDatasource.value.id)
    ElMessage.success('元数据同步成功')
    await loadMetadata()
    await loadDatabases()
  } catch (e) {
    console.error(e)
    ElMessage.error('同步失败')
  } finally {
    syncing.value = false
  }
}

const showColumns = async (table) => {
  currentTable.value = table
  columnsDialogVisible.value = true
  columnsLoading.value = true
  try {
    const res = await getColumns(selectedDatasource.value.id, table.databaseName, table.tableName)
    currentTableColumns.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    columnsLoading.value = false
  }
}

onMounted(() => {
  loadDatasources()
})
</script>

<style scoped>
.metadata-container {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.datasource-list {
  max-height: 500px;
  overflow-y: auto;
}

.datasource-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
}

.datasource-item:hover {
  background: #e6f0ff;
}

.datasource-item.active {
  background: #e6f0ff;
  border: 1px solid #409EFF;
}

.datasource-item .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

.datasource-item span {
  flex: 1;
  font-size: 14px;
  color: #303133;
}
</style>
