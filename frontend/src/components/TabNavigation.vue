<template>
  <div class="tab-navigation">
    <div
      v-for="tab in tabs"
      :key="tab.path"
      class="tab-item"
      :class="{ active: tab.path === activeTab }"
      @click="switchTab(tab)"
    >
      <span class="tab-title">{{ tab.title }}</span>
      <span
        v-if="tab.closable"
        class="tab-close"
        @click.stop="closeTab(tab)"
      >×</span>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const defaultTab = { title: '首页', path: '/home', closable: false }
const tabs = ref([{ ...defaultTab }])
const activeTab = ref('/home')

const addTab = (to) => {
  const title = to.meta?.title || to.name || '未命名'
  const path = to.path
  const existing = tabs.value.find(t => t.path === path)
  if (!existing) {
    tabs.value.push({ title, path, closable: true })
  }
  activeTab.value = path
}

const switchTab = (tab) => {
  activeTab.value = tab.path
  if (route.path !== tab.path) {
    router.push(tab.path)
  }
}

const closeTab = (tab) => {
  const idx = tabs.value.findIndex(t => t.path === tab.path)
  if (idx === -1) return

  tabs.value.splice(idx, 1)
  if (activeTab.value === tab.path) {
    const next = tabs.value[idx] || tabs.value[idx - 1] || tabs.value[0]
    activeTab.value = next.path
    router.push(next.path)
  }
}

onMounted(() => {
  if (route.path !== '/home') {
    addTab(route)
  }
})

watch(() => route.path, (to) => {
  if (to !== '/login') {
    addTab(route)
  }
})
</script>

<style scoped>
.tab-navigation {
  display: flex;
  align-items: center;
  height: 36px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 8px;
  overflow-x: auto;
  white-space: nowrap;
}

.tab-item {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 14px;
  margin-right: 4px;
  font-size: 13px;
  color: #606266;
  border: 1px solid #e6e6e6;
  border-radius: 3px;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.tab-item:hover {
  color: #409EFF;
}

.tab-item.active {
  background: #409EFF;
  color: #fff;
  border-color: #409EFF;
}

.tab-title {
  margin-right: 6px;
}

.tab-close {
  font-size: 14px;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  transition: all 0.2s;
}

.tab-close:hover {
  background: rgba(0, 0, 0, 0.15);
}

.tab-item.active .tab-close:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>
