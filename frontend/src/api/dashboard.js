import request from './request'

export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

export function getRecentExecutions() {
  return request({
    url: '/dashboard/recent-executions',
    method: 'get'
  })
}
