import request from '../request'

export function getPermissionList(params = {}) {
  return request({
    url: '/system/permission',
    method: 'get',
    params
  })
}

export function savePermission(data) {
  return request({
    url: '/system/permission',
    method: 'post',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/system/permission/${id}`,
    method: 'delete'
  })
}
