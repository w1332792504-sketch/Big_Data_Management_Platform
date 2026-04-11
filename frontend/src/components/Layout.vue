<template>
  <div class="layout-wrapper">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-logo">
        <el-icon :size="22"><DataLine /></el-icon>
        <span class="logo-text">数据管理平台</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="#ccc"
        active-text-color="#fff"
        class="sidebar-menu"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/datasource">
          <el-icon><DataLine /></el-icon>
          <span>数据源管理</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><List /></el-icon>
          <span>任务管理</span>
        </el-menu-item>
        <el-menu-item index="/mq">
          <el-icon><ChatDotRound /></el-icon>
          <span>消息队列</span>
        </el-menu-item>
        <el-menu-item index="/quality">
          <el-icon><CircleCheck /></el-icon>
          <span>数据质量</span>
        </el-menu-item>
        <el-menu-item index="/metadata">
          <el-icon><Collection /></el-icon>
          <span>元数据管理</span>
        </el-menu-item>
        <el-menu-item index="/alert">
          <el-icon><Bell /></el-icon>
          <span>监控告警</span>
        </el-menu-item>
        <el-menu-item index="/audit">
          <el-icon><Document /></el-icon>
          <span>审计日志</span>
        </el-menu-item>

        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/permission">权限管理</el-menu-item>
          <el-menu-item index="/system/organization">组织管理</el-menu-item>
          <el-menu-item index="/system/dictionary">字典管理</el-menu-item>
        </el-sub-menu>
      </el-menu>

      <!-- 底部用户信息 -->
      <div class="sidebar-user">
        <div class="user-left">
          <el-avatar :size="32" icon="User" />
          <span class="user-name">{{ currentUser.nickname }}</span>
        </div>
        <div class="user-actions">
          <el-icon class="action-icon"><Bell /></el-icon>
          <el-icon class="action-icon"><Setting /></el-icon>
          <el-dropdown @command="handleCommand" trigger="click">
            <el-icon class="action-icon"><QuestionFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <div class="top-bar">
        <div class="top-bar-right">
          <span class="welcome-text">欢迎回来，{{ currentUser.nickname }}</span>
        </div>
      </div>

      <!-- 标签页导航 -->
      <TabNavigation />

      <!-- 主内容区 -->
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'
import TabNavigation from '@/components/TabNavigation.vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/system/')) return path
  return path
})

const currentUser = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : { nickname: '管理员' }
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) {
      // 忽略错误
    } finally {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  background: #304156;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-logo .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.05) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #409EFF !important;
}

/* 底部用户信息 */
.sidebar-user {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #ccc;
}

.user-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 13px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-icon {
  font-size: 16px;
  cursor: pointer;
  color: #aaa;
  transition: color 0.2s;
}

.action-icon:hover {
  color: #fff;
}

/* 右侧内容区 */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

.top-bar {
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 16px;
  flex-shrink: 0;
}

.top-bar-right {
  margin-left: auto;
}

.welcome-text {
  font-size: 14px;
  color: #606266;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f0f2f5;
}
</style>
