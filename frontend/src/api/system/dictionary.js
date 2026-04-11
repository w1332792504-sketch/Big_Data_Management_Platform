import request from '../request'

export function getDictionaryList(page = 0, size = 10) {
  return request({
    url: '/system/dictionary',
    method: 'get',
    params: { page, size }
  })
}

export function saveDictionary(data) {
  return request({
    url: '/system/dictionary',
    method: 'post',
    data
  })
}

export function deleteDictionary(id) {
  return request({
    url: `/system/dictionary/${id}`,
    method: 'delete'
  })
}

export function getDictionaryItems(dictionaryId) {
  return request({
    url: `/system/dictionary/${dictionaryId}/items`,
    method: 'get'
  })
}

export function saveDictionaryItem(data) {
  return request({
    url: '/system/dictionary/item',
    method: 'post',
    data
  })
}

export function deleteDictionaryItem(id) {
  return request({
    url: `/system/dictionary/item/${id}`,
    method: 'delete'
  })
}
