import request from '@/utils/request'

export function listKnowledge(query) {
  return request({
    url: '/knowledge/list',
    method: 'get',
    params: query
  })
}

export function deleteKnowledge(data) {
  return request({
    url: '/knowledge/delete',
    method: 'post',
    data
  })
}

export function publishKnowledge(data) {
  return request({
    url: '/knowledge/create',
    method: 'post',
    data
  })
}

export function detailKnowledge(id) {
  return request({
    url: '/knowledge/detail',
    method: 'get',
    params: { id }
  })
}

export function editKnowledge(data) {
  return request({
    url: '/knowledge/update',
    method: 'post',
    data
  })
}

export function listCatAndBrand() {
  return request({
    url: '/knowledge/catAndBrand',
    method: 'get'
  })
}
