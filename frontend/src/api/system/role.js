import request from '../request'

export function getRoleList(page = 0, size = 10) {
  return request({
    url: '/system/role',
    method: 'get',
    params: { page, size }
  })
}

export function saveRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(id) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'get'
  })
}

export function updateRolePermissions(id, permissionIds) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'put',
    data: { permissionIds }
  })
}
