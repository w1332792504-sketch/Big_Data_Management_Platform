import request from './request'

// 告警规则
export const getAlertRules = () => request.get('/alert/rules')
export const getActiveAlertRules = () => request.get('/alert/rules/active')
export const getAlertRule = (id) => request.get(`/alert/rules/${id}`)
export const createAlertRule = (data) => request.post('/alert/rules', data)
export const updateAlertRule = (id, data) => request.put(`/alert/rules/${id}`, data)
export const deleteAlertRule = (id) => request.delete(`/alert/rules/${id}`)
export const updateAlertRuleStatus = (id, status) => request.put(`/alert/rules/${id}/status`, null, { params: { status } })

// 发送告警
export const sendAlert = (ruleId, content) => request.post(`/alert/send/${ruleId}`, null, { params: { content } })

// 告警记录
export const getAlertRecords = (page = 0, size = 10) => request.get('/alert/records', { params: { page, size } })
export const getPendingAlertRecords = (page = 0, size = 10) => request.get('/alert/records/pending', { params: { page, size } })

// 统计
export const getPendingAlertCount = () => request.get('/alert/stats/pending-count')
export const getCriticalAlertCount = () => request.get('/alert/stats/critical-count')

// 告警确认和解决
export const acknowledgeAlert = (id, acknowledgedBy) =>
  request.put(`/alert/records/${id}/acknowledge`, null, { params: { acknowledgedBy } })
export const resolveAlert = (id, resolvedBy, remark) =>
  request.put(`/alert/records/${id}/resolve`, null, { params: { resolvedBy, remark } })
