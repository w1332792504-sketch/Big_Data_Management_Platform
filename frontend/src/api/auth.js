import request from './request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'get'
  })
}

export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

export function checkAuth() {
  return request({
    url: '/auth/check',
    method: 'post'
  })
}
