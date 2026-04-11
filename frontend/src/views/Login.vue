<template>
  <div class="login-container" ref="loginContainer" @mousemove="handleMouseMove">
    <!-- 动态背景 -->
    <div class="login-bg">
      <!-- 科技感网格背景 -->
      <div class="bg-grid"></div>
      <div class="bg-overlay"></div>

      <!-- 鼠标跟随线条 -->
      <svg class="lines-svg" ref="linesSvg">
        <line
          v-for="i in lineCount"
          :key="i"
          :x1="lineStartX[i]"
          :y1="lineStartY[i]"
          :x2="lineEndX[i]"
          :y2="lineEndY[i]"
          :stroke="`rgba(64, 169, 255, ${lineOpacity[i]})`"
          :stroke-width="lineWidth[i]"
          class="mouse-line"
        />
      </svg>

      <!-- 漂浮的粒子 -->
      <div class="particle particle-1" :style="particle1Style"></div>
      <div class="particle particle-2" :style="particle2Style"></div>
      <div class="particle particle-3" :style="particle3Style"></div>
      <div class="particle particle-4" :style="particle4Style"></div>
      <div class="particle particle-5" :style="particle5Style"></div>

      <!-- 数据流线条 -->
      <div class="data-line data-line-1"></div>
      <div class="data-line data-line-2"></div>
      <div class="data-line data-line-3"></div>

      <!-- 鼠标光晕 -->
      <div class="bg-mouse-glow" :style="mouseGlowStyle"></div>
    </div>

    <div class="login-box">
      <div class="login-header">
        <div class="logo-icon">
          <div class="logo-ring"></div>
          <div class="logo-core"></div>
        </div>
        <h1>大数据管理平台</h1>
        <p>数据抽取任务管理系统</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <el-icon class="btn-icon"><Connection /></el-icon>
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const loginContainer = ref(null)
const loginFormRef = ref(null)
const linesSvg = ref(null)
const loading = ref(false)

const mouseX = ref(0.5)
const mouseY = ref(0.5)
const time = ref(Date.now())
const lineCount = 12

// 线条数据
const lineStartX = ref(Array(lineCount).fill(0))
const lineStartY = ref(Array(lineCount).fill(0))
const lineEndX = ref(Array(lineCount).fill(0))
const lineEndY = ref(Array(lineCount).fill(0))
const lineOpacity = ref(Array(lineCount).fill(0))
const lineWidth = ref(Array(lineCount).fill(0))

// 更新动画时间
setInterval(() => {
  time.value = Date.now()
}, 50)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleMouseMove = (e) => {
  const rect = loginContainer.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top

  mouseX.value = x / rect.width
  mouseY.value = y / rect.height

  // 更新线条位置 - 从鼠标位置向外辐射
  const centerX = x
  const centerY = y

  for (let i = 0; i < lineCount; i++) {
    const angle = (i / lineCount) * Math.PI * 2
    const lineLength = 80 + Math.sin(time.value * 0.003 + i) * 20

    lineStartX.value[i] = centerX
    lineStartY.value[i] = centerY
    lineEndX.value[i] = centerX + Math.cos(angle) * lineLength
    lineEndY.value[i] = centerY + Math.sin(angle) * lineLength
    lineOpacity.value[i] = 0.3 + Math.sin(time.value * 0.005 + i * 0.5) * 0.2
    lineWidth.value[i] = 1 + Math.sin(time.value * 0.004 + i) * 0.5
  }
}

// 初始化线条从中心辐射
for (let i = 0; i < lineCount; i++) {
  const angle = (i / lineCount) * Math.PI * 2
  lineStartX.value[i] = window.innerWidth / 2
  lineStartY.value[i] = window.innerHeight / 2
  lineEndX.value[i] = lineStartX.value[i] + Math.cos(angle) * 50
  lineEndY.value[i] = lineStartY.value[i] + Math.sin(angle) * 50
  lineOpacity.value[i] = 0.2
  lineWidth.value[i] = 1
}

// 粒子位置计算
const calculateParticlePosition = (baseX, baseY, amplitude, phase, speed) => {
  const t = time.value * speed
  const mouseInfluenceX = (mouseX.value - 0.5) * amplitude * 0.5
  const mouseInfluenceY = (mouseY.value - 0.5) * amplitude * 0.5
  return {
    transform: `translate(${Math.sin(t + phase) * amplitude + mouseInfluenceX}px, ${Math.cos(t + phase * 1.5) * amplitude + mouseInfluenceY}px)`
  }
}

const particle1Style = computed(() => calculateParticlePosition(50, 50, 30, 0, 0.001))
const particle2Style = computed(() => calculateParticlePosition(80, 80, 40, Math.PI / 3, 0.0008))
const particle3Style = computed(() => calculateParticlePosition(60, 70, 35, Math.PI / 2, 0.0012))
const particle4Style = computed(() => calculateParticlePosition(70, 60, 45, Math.PI, 0.0009))
const particle5Style = computed(() => calculateParticlePosition(55, 75, 25, Math.PI * 1.5, 0.0011))

const mouseGlowStyle = computed(() => ({
  left: `${mouseX.value * 100}%`,
  top: `${mouseY.value * 100}%`,
  opacity: 0.3 + mouseX.value * 0.4
}))

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        if (res.code === 200) {
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('user', JSON.stringify(res.data))
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0a1628 0%, #1a365d 50%, #0d2137 100%);
  cursor: crosshair;
}

/* 科技感网格背景 */
.login-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.bg-grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(64, 169, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(64, 169, 255, 0.05) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% { transform: perspective(800px) rotateX(50deg) translateY(0) scale(1.5); }
  100% { transform: perspective(800px) rotateX(50deg) translateY(60px) scale(1.5); }
}

.bg-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, transparent 0%, rgba(10, 22, 40, 0.85) 100%);
}

/* SVG 线条层 */
.lines-svg {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.mouse-line {
  filter: drop-shadow(0 0 5px rgba(64, 169, 255, 0.8));
  transition: opacity 0.1s ease;
}

/* 漂浮粒子 */
.particle {
  position: absolute;
  border-radius: 50%;
  filter: blur(1px);
  transition: transform 0.2s ease-out;
  z-index: 2;
}

.particle-1 {
  width: 8px;
  height: 8px;
  top: 20%;
  left: 15%;
  background: #40a9ff;
  box-shadow: 0 0 20px #40a9ff, 0 0 40px #1890ff;
}

.particle-2 {
  width: 6px;
  height: 6px;
  top: 35%;
  left: 75%;
  background: #1890ff;
  box-shadow: 0 0 15px #1890ff, 0 0 30px #096dd9;
}

.particle-3 {
  width: 10px;
  height: 10px;
  top: 60%;
  left: 25%;
  background: #096dd9;
  box-shadow: 0 0 25px #096dd9, 0 0 50px #0050b3;
}

.particle-4 {
  width: 7px;
  height: 7px;
  top: 75%;
  left: 65%;
  background: #40a9ff;
  box-shadow: 0 0 20px #40a9ff, 0 0 40px #1890ff;
}

.particle-5 {
  width: 5px;
  height: 5px;
  top: 45%;
  left: 50%;
  background: #69c0ff;
  box-shadow: 0 0 15px #69c0ff, 0 0 30px #40a9ff;
}

/* 数据流线条 */
.data-line {
  position: absolute;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(64, 169, 255, 0.6), transparent);
  animation: dataFlow 8s ease-in-out infinite;
  z-index: 1;
}

.data-line-1 {
  width: 200px;
  top: 25%;
  left: -200px;
  animation-delay: 0s;
}

.data-line-2 {
  width: 300px;
  top: 55%;
  left: -300px;
  animation-delay: 2s;
}

.data-line-3 {
  width: 250px;
  top: 75%;
  left: -250px;
  animation-delay: 4s;
}

@keyframes dataFlow {
  0% { transform: translateX(0); opacity: 0; }
  20% { opacity: 1; }
  80% { opacity: 1; }
  100% { transform: translateX(calc(100vw + 300px)); opacity: 0; }
}

/* 鼠标光晕 */
.bg-mouse-glow {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(64, 169, 255, 0.12) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  transition: opacity 0.15s ease;
  filter: blur(40px);
  z-index: 0;
}

/* 登录框 */
.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(13, 31, 53, 0.9);
  border-radius: 20px;
  border: 1px solid rgba(64, 169, 255, 0.25);
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 0 80px rgba(64, 169, 255, 0.08),
    0 0 100px rgba(64, 169, 255, 0.1);
  position: relative;
  z-index: 10;
  backdrop-filter: blur(30px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-box::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(135deg, rgba(64, 169, 255, 0.4), transparent, rgba(64, 169, 255, 0.4));
  border-radius: 20px;
  z-index: -1;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.login-box:hover {
  transform: translateY(-8px) scale(1.01);
  box-shadow:
    0 30px 100px rgba(0, 0, 0, 0.6),
    inset 0 0 100px rgba(64, 169, 255, 0.15),
    0 0 80px rgba(64, 169, 255, 0.3);
  border-color: rgba(64, 169, 255, 0.5);
}

.login-box:hover::before {
  opacity: 1;
}

/* Logo 动画 */
.logo-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-top-color: #40a9ff;
  border-right-color: #1890ff;
  border-radius: 50%;
  animation: spin 3s linear infinite;
}

.logo-core {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #40a9ff, #1890ff);
  border-radius: 50%;
  box-shadow: 0 0 40px rgba(64, 169, 255, 0.8);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.15); opacity: 0.8; }
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-header h1 {
  color: #fff;
  font-size: 26px;
  margin: 15px 0 8px;
  font-weight: 600;
  letter-spacing: 2px;
  text-shadow: 0 2px 15px rgba(64, 169, 255, 0.5);
}

.login-header p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  letter-spacing: 1px;
}

/* 输入框样式 */
.login-form {
  margin-top: 25px;
}

.login-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(64, 169, 255, 0.3);
  box-shadow: none;
  border-radius: 8px;
}

.login-form :deep(.el-input__wrapper:hover),
.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #40a9ff;
  box-shadow: 0 0 20px rgba(64, 169, 255, 0.3);
}

.login-form :deep(.el-input__inner) {
  color: #fff;
}

.login-form :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.7);
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #40a9ff, #1890ff);
  border: none;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transition: left 0.6s ease;
}

.login-btn:hover::before {
  left: 100%;
}

.btn-icon {
  margin-right: 6px;
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.login-btn:hover {
  background: linear-gradient(135deg, #69c0ff, #40a9ff);
  box-shadow: 0 8px 30px rgba(64, 169, 255, 0.6);
  transform: translateY(-3px);
}
</style>
