<template>
  <div class="system-role-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-info">
        <h2>角色管理</h2>
        <p>管理系统角色及权限分配</p>
      </div>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        新建角色
      </el-button>
    </div>

    <!-- 数据列表 -->
    <el-card class="list-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading" :default-sort="{ prop: 'id', order: 'ascending' }">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="success" @click="openPermissionDialog(row)">分配权限</el-button>
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permDialogVisible"
      :title="'分配权限 - ' + currentRoleName"
      width="500px"
    >
      <el-tree
        ref="permTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        :default-checked-keys="checkedPermIds"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions" :loading="permSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, saveRole, deleteRole, getRolePermissions, updateRolePermissions } from '@/api/system/role'
import { getPermissionList } from '@/api/system/permission'

const loading = ref(false)
const submitting = ref(false)
const permSubmitting = ref(false)
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const dialogTitle = ref('新建角色')
const formRef = ref(null)
const permTreeRef = ref(null)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const permissionTree = ref([])
const checkedPermIds = ref([])
const currentRoleId = ref(null)
const currentRoleName = ref('')

const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList(currentPage.value - 1, pageSize.value)
    tableData.value = res.data.content || res.data.elements || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const loadPermissions = async () => {
  const res = await getPermissionList()
  const allPerms = res.data || []
  // 构建树形结构
  const tree = []
  const map = {}
  allPerms.forEach(p => {
    map[p.id] = { ...p, children: [] }
  })
  allPerms.forEach(p => {
    if (p.parentId && map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    } else {
      tree.push(map[p.id])
    }
  })
  permissionTree.value = tree
}

const openDialog = (row = null) => {
  dialogTitle.value = row ? '编辑角色' : '新建角色'
  dialogVisible.value = true
  if (row) {
    Object.assign(form, row)
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.code = ''
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
      await saveRole(form)
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

const openPermissionDialog = async (row) => {
  currentRoleId.value = row.id
  currentRoleName.value = row.name
  permDialogVisible.value = true

  await loadPermissions()
  // 加载该角色已有的权限
  const res = await getRolePermissions(row.id)
  checkedPermIds.value = res.data || []
}

const handleSavePermissions = async () => {
  permSubmitting.value = true
  try {
    const checkedKeys = permTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    await updateRolePermissions(currentRoleId.value, allKeys)
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
  } catch (e) {
    console.error(e)
  } finally {
    permSubmitting.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-role-container {
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
