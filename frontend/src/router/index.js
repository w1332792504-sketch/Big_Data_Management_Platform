import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'HomePage',
        component: () => import('@/views/HomePage.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Monitor' }
      },
      {
        path: '/datasource',
        name: 'Datasource',
        component: () => import('@/views/Datasource.vue'),
        meta: { title: '数据源管理', icon: 'DataLine' }
      },
      {
        path: '/task',
        name: 'Task',
        component: () => import('@/views/Task.vue'),
        meta: { title: '任务管理', icon: 'List' }
      },
      {
        path: '/task/:id/executions',
        name: 'TaskExecutions',
        component: () => import('@/views/TaskExecutions.vue'),
        meta: { title: '执行记录', icon: 'Document' }
      },
      {
        path: '/mq',
        name: 'MQMonitor',
        component: () => import('@/views/MQMonitor.vue'),
        meta: { title: '消息队列', icon: 'ChatDotRound' }
      },
      {
        path: '/quality',
        name: 'Quality',
        component: () => import('@/views/Quality.vue'),
        meta: { title: '数据质量', icon: 'CircleCheck' }
      },
      {
        path: '/metadata',
        name: 'Metadata',
        component: () => import('@/views/Metadata.vue'),
        meta: { title: '元数据管理', icon: 'Collection' }
      },
      {
        path: '/alert',
        name: 'Alert',
        component: () => import('@/views/Alert.vue'),
        meta: { title: '监控告警', icon: 'Bell' }
      },
      {
        path: '/audit',
        name: 'Audit',
        component: () => import('@/views/Audit.vue'),
        meta: { title: '审计日志', icon: 'Document' }
      },
      {
        path: '/system',
        name: 'System',
        component: () => import('@/views/system/SystemLayout.vue'),
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/views/system/SystemUser.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('@/views/system/SystemRole.vue'),
            meta: { title: '角色管理' }
          },
          {
            path: 'permission',
            name: 'SystemPermission',
            component: () => import('@/views/system/SystemPermission.vue'),
            meta: { title: '权限管理' }
          },
          {
            path: 'organization',
            name: 'SystemOrganization',
            component: () => import('@/views/system/SystemOrganization.vue'),
            meta: { title: '组织管理' }
          },
          {
            path: 'dictionary',
            name: 'SystemDictionary',
            component: () => import('@/views/system/SystemDictionary.vue'),
            meta: { title: '字典管理' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const isLogin = !!token

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 大数据管理平台'
  }

  // 如果访问登录页，且已登录，直接跳转到首页
  if (to.path === '/login') {
    if (isLogin) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 如果未登录，跳转到登录页
  if (!isLogin) {
    next('/login')
    return
  }

  next()
})

export default router
