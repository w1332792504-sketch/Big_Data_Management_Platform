import request from './request'

// 数据质量规则
export const getQualityRules = () => request.get('/quality/rules')
export const getQualityRulesByDatasource = (datasourceId) => request.get(`/quality/rules/datasource/${datasourceId}`)
export const getQualityRule = (id) => request.get(`/quality/rules/${id}`)
export const createQualityRule = (data) => request.post('/quality/rules', data)
export const updateQualityRule = (id, data) => request.put(`/quality/rules/${id}`, data)
export const deleteQualityRule = (id) => request.delete(`/quality/rules/${id}`)
export const updateQualityRuleStatus = (id, status) => request.put(`/quality/rules/${id}/status`, null, { params: { status } })

// 执行检查
export const executeQualityCheck = (ruleId) => request.post(`/quality/rules/${ruleId}/execute`)
export const executeAllQualityChecks = () => request.post('/quality/rules/execute-all')

// 检查结果
export const getQualityResults = (datasourceId, page = 0, size = 10) =>
  request.get('/quality/results', { params: { datasourceId, page, size } })
export const getQualityResultsByRule = (ruleId) => request.get(`/quality/results/rule/${ruleId}`)

// 统计
export const getQualityFailCount = () => request.get('/quality/stats/fail-count')
