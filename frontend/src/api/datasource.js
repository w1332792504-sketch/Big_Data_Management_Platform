import request from './request'

export function getDatasources() {
  return request({
    url: '/datasource/list',
    method: 'get'
  })
}

export function getDataSourceList() {
  return request({
    url: '/datasource/list',
    method: 'get'
  })
}

export function getDataSourceById(id) {
  return request({
    url: `/datasource/${id}`,
    method: 'get'
  })
}

export function saveDataSource(data) {
  return request({
    url: '/datasource/save',
    method: 'post',
    data
  })
}

export function deleteDataSource(id) {
  return request({
    url: `/datasource/${id}`,
    method: 'delete'
  })
}

export function testDataSource(data) {
  return request({
    url: '/datasource/test',
    method: 'post',
    data
  })
}
