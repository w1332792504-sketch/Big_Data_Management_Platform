import request from './request'

// 查询审计日志
export const getAuditLogs = (page = 0, size = 10) => request.get('/audit/logs', { params: { page, size } })

// 按模块查询
export const getAuditLogsByModule = (module, page = 0, size = 10) =>
  request.get(`/audit/logs/module/${module}`, { params: { page, size } })

// 按操作人查询
export const getAuditLogsByOperator = (operator, page = 0, size = 10) =>
  request.get(`/audit/logs/operator/${operator}`, { params: { page, size } })

// 按时间范围查询
export const getAuditLogsByTimeRange = (start, end, page = 0, size = 10) =>
  request.get('/audit/logs/time-range', { params: { start, end, page, size } })

// 统计
export const getTodayAuditCount = () => request.get('/audit/stats/today')
export const getAuditCountByModule = () => request.get('/audit/stats/by-module')
