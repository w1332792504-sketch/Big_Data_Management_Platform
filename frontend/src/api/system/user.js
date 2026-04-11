import request from '../request'

export function getUserList(page = 0, size = 10, params = {}) {
  return request({
    url: '/system/user',
    method: 'get',
    params: { page, size, ...params }
  })
}

export function saveUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}
