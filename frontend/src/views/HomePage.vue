<template>
  <div class="home-container">
    <div class="header">
      <h1>大数据管理平台</h1>
      <p>数据抽取 · 任务调度 · 实时监控</p>
    </div>

    <!-- 数据流向图 -->
    <div class="flow-container">
      <svg class="flow-svg" viewBox="0 0 1000 400">
        <defs>
          <pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse">
            <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#f0f0f0" stroke-width="0.5"/>
          </pattern>
          <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
            <polygon points="0 0, 10 3.5, 0 7" fill="#409eff"/>
          </marker>
          <filter id="glow">
            <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
            <feMerge>
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
        </defs>

        <rect width="100%" height="100%" fill="url(#grid)"/>

        <!-- 数据源层 -->
        <g class="layer sources" transform="translate(50, 80)">
          <text x="60" y="-20" class="layer-title">数据源层</text>
          <g class="node source-node" transform="translate(0, 0)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#e6f7ff" stroke="#1890ff" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">MySQL</text>
          </g>
          <g class="node source-node" transform="translate(0, 80)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#e6f7ff" stroke="#1890ff" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">PostgreSQL</text>
          </g>
          <g class="node source-node" transform="translate(0, 160)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#e6f7ff" stroke="#1890ff" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">Oracle</text>
          </g>
          <g class="node source-node" transform="translate(0, 240)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#e6f7ff" stroke="#1890ff" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">Hive</text>
          </g>
        </g>

        <!-- 连接线 -->
        <g class="connections">
          <path d="M 230 110 Q 300 110 320 200" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>
          <path d="M 230 190 Q 300 190 320 200" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>
          <path d="M 230 270 Q 300 270 320 200" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>
          <path d="M 230 350 Q 300 350 320 200" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>
        </g>

        <!-- DataX 引擎 -->
        <g class="node engine-node" transform="translate(320, 170)">
          <rect x="0" y="0" width="140" height="80" rx="10" fill="#fff7e6" stroke="#fa8c16" stroke-width="2" filter="url(#glow)"/>
          <text x="70" y="45" text-anchor="middle" class="node-label-bold">DataX 引擎</text>
          <text x="70" y="65" text-anchor="middle" class="node-sublabel">数据抽取</text>
        </g>

        <!-- 连接线：DataX -> RabbitMQ -->
        <path d="M 460 210 L 520 210" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>

        <!-- 消息队列 -->
        <g class="node mq-node" transform="translate(520, 170)">
          <rect x="0" y="0" width="140" height="80" rx="10" fill="#f0f5ff" stroke="#2f54eb" stroke-width="2" filter="url(#glow)"/>
          <text x="70" y="45" text-anchor="middle" class="node-label-bold">RabbitMQ</text>
          <text x="70" y="65" text-anchor="middle" class="node-sublabel">消息队列</text>
        </g>

        <!-- 连接线：RabbitMQ -> 目标存储 -->
        <path d="M 660 210 L 720 210" stroke="#409eff" stroke-width="2" fill="none" marker-end="url(#arrowhead)"/>

        <!-- 目标存储层 -->
        <g class="layer targets" transform="translate(720, 80)">
          <text x="60" y="-20" class="layer-title">目标存储</text>
          <g class="node target-node" transform="translate(0, 0)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#f9f0ff" stroke="#722ed1" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">数据仓库</text>
          </g>
          <g class="node target-node" transform="translate(0, 80)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#f9f0ff" stroke="#722ed1" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">ClickHouse</text>
          </g>
          <g class="node target-node" transform="translate(0, 160)">
            <rect x="0" y="0" width="120" height="60" rx="8" fill="#f9f0ff" stroke="#722ed1" stroke-width="2"/>
            <text x="60" y="35" text-anchor="middle" class="node-label">报表系统</text>
          </g>
        </g>

        <!-- 监控层 -->
        <g class="layer monitor" transform="translate(350, 320)">
          <text x="150" y="-10" text-anchor="middle" class="layer-title">监控告警</text>
          <g class="node monitor-node" transform="translate(50, 0)">
            <rect x="0" y="0" width="200" height="50" rx="8" fill="#fff1f0" stroke="#ff4d4f" stroke-width="2"/>
            <text x="100" y="30" text-anchor="middle" class="node-label">实时监控 · 状态追踪 · 异常告警</text>
          </g>
        </g>

        <path d="M 390 250 Q 390 300 400 320" stroke="#ff4d4f" stroke-width="1.5" fill="none" stroke-dasharray="5,5"/>
        <path d="M 590 250 Q 590 300 600 320" stroke="#ff4d4f" stroke-width="1.5" fill="none" stroke-dasharray="5,5"/>

        <!-- 动画粒子 -->
        <circle r="4" fill="#409eff">
          <animateMotion dur="3s" repeatCount="indefinite" path="M 230 110 Q 300 110 320 200"/>
        </circle>
        <circle r="4" fill="#409eff">
          <animateMotion dur="3s" repeatCount="indefinite" path="M 230 190 Q 300 190 320 200" begin="0.5s"/>
        </circle>
        <circle r="4" fill="#2f54eb">
          <animateMotion dur="2s" repeatCount="indefinite" path="M 460 210 L 520 210" begin="1s"/>
        </circle>
        <circle r="4" fill="#722ed1">
          <animateMotion dur="2s" repeatCount="indefinite" path="M 660 210 L 720 210" begin="1.5s"/>
        </circle>
      </svg>
    </div>

    <!-- 数据概览 -->
    <div class="stats-overview">
      <h2>数据概览</h2>
      <div class="stats-cards">
        <el-card shadow="hover" class="stat-card" @click="$router.push('/datasource')">
          <div class="stat-number" style="color: #409eff">{{ overviewStats.datasourceCount }}</div>
          <div class="stat-label">数据源</div>
        </el-card>
        <el-card shadow="hover" class="stat-card" @click="$router.push('/task')">
          <div class="stat-number" style="color: #67c23a">{{ overviewStats.taskCount }}</div>
          <div class="stat-label">抽取任务</div>
        </el-card>
        <el-card shadow="hover" class="stat-card">
          <div class="stat-number" style="color: #e6a23c">{{ overviewStats.successCount }}</div>
          <div class="stat-label">成功执行</div>
        </el-card>
        <el-card shadow="hover" class="stat-card">
          <div class="stat-number" style="color: #f56c6c">{{ overviewStats.failedCount }}</div>
          <div class="stat-label">失败执行</div>
        </el-card>
      </div>
    </div>

    <!-- 功能卡片 -->
    <div class="feature-cards">
      <el-card class="feature-card" shadow="hover" @click="$router.push('/datasource')">
        <div class="card-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <el-icon><DataLine /></el-icon>
        </div>
        <div class="card-content">
          <h3>数据源管理</h3>
          <p>MySQL、PostgreSQL、Oracle、Hive 多源接入</p>
        </div>
      </el-card>

      <el-card class="feature-card" shadow="hover" @click="$router.push('/task')">
        <div class="card-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <el-icon><List /></el-icon>
        </div>
        <div class="card-content">
          <h3>任务管理</h3>
          <p>全量/增量抽取，Cron 定时调度</p>
        </div>
      </el-card>

      <el-card class="feature-card" shadow="hover" @click="$router.push('/dashboard')">
        <div class="card-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="card-content">
          <h3>仪表盘</h3>
          <p>实时监控任务执行状态和数据流转</p>
        </div>
      </el-card>

      <el-card class="feature-card" shadow="hover" @click="$router.push('/system/user')">
        <div class="card-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <el-icon><Setting /></el-icon>
        </div>
        <div class="card-content">
          <h3>系统管理</h3>
          <p>用户、角色、权限、组织架构</p>
        </div>
      </el-card>
    </div>

    <!-- 技术栈 -->
    <div class="tech-stack">
      <h2>技术架构</h2>
      <div class="stack-items">
        <el-tag type="primary" size="large">Vue 3</el-tag>
        <el-tag type="success" size="large">Spring Boot 3.3</el-tag>
        <el-tag type="warning" size="large">DataX</el-tag>
        <el-tag type="danger" size="large">MySQL 8.0</el-tag>
        <el-tag color="#8b5cf6" style="color: white" size="large">RabbitMQ</el-tag>
        <el-tag type="info" size="large">Redis 7</el-tag>
        <el-tag size="large">ClickHouse</el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { DataLine, List, Monitor, Setting } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'

const overviewStats = ref({
  datasourceCount: 0,
  taskCount: 0,
  successCount: 0,
  failedCount: 0
})

onMounted(async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data || {}
    overviewStats.value = {
      datasourceCount: data.datasourceCount || 0,
      taskCount: data.taskCount || 0,
      successCount: data.successCount || 0,
      failedCount: data.failedCount || 0
    }
  } catch (e) {
    console.error('Failed to load dashboard stats:', e)
  }
})
</script>

<style scoped>
.home-container {
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header p {
  font-size: 16px;
  color: #909399;
}

/* 数据流向图 */
.flow-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.flow-svg {
  width: 100%;
  height: auto;
  max-height: 400px;
}

.layer-title {
  font-size: 14px;
  font-weight: 600;
  fill: #606266;
}

.node-label {
  font-size: 12px;
  fill: #303133;
  font-weight: 500;
}

.node-label-bold {
  font-size: 14px;
  fill: #303133;
  font-weight: 600;
}

.node-sublabel {
  font-size: 10px;
  fill: #909399;
}

/* 数据概览 */
.stats-overview {
  margin-bottom: 30px;
}

.stats-overview h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

/* 功能卡片 */
.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.feature-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.feature-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
  gap: 16px;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.card-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.card-content p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

/* 技术栈 */
.tech-stack {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.tech-stack h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.stack-items {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
}

.stack-items .el-tag {
  font-size: 14px;
  padding: 8px 16px;
}
</style>
