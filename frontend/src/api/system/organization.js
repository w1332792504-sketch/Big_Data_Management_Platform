import request from '../request'

export function getOrganizationList(params = {}) {
  return request({
    url: '/system/organization',
    method: 'get',
    params
  })
}

export function saveOrganization(data) {
  return request({
    url: '/system/organization',
    method: 'post',
    data
  })
}

export function deleteOrganization(id) {
  return request({
    url: `/system/organization/${id}`,
    method: 'delete'
  })
}
