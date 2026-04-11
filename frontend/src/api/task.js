import request from './request'

export function getTaskList() {
  return request({
    url: '/task/list',
    method: 'get'
  })
}

export function getTaskById(id) {
  return request({
    url: `/task/${id}`,
    method: 'get'
  })
}

export function saveTask(data) {
  return request({
    url: '/task/save',
    method: 'post',
    data
  })
}

export function deleteTask(id) {
  return request({
    url: `/task/${id}`,
    method: 'delete'
  })
}

export function runTask(id, executionType = 'MANUAL', operator = 'admin') {
  return request({
    url: `/task/${id}/run?executionType=${executionType}&operator=${operator}`,
    method: 'post'
  })
}

export function stopTask(id) {
  return request({
    url: `/task/${id}/stop`,
    method: 'post'
  })
}

export function getTaskExecutions(taskId, page = 0, size = 10) {
  return request({
    url: `/task/${taskId}/executions?page=${page}&size=${size}`,
    method: 'get'
  })
}

export function getLatestExecution(taskId) {
  return request({
    url: `/task/${taskId}/latest-execution`,
    method: 'get'
  })
}
