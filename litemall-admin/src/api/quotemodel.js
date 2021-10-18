import request from '@/utils/request'

export function list(query) {
  return request({
    url: '/quoteModel/list',
    method: 'get',
    params: query
  })
}
export function listAll() {
  return request({
    url: '/quoteModel/options',
    method: 'get'
  })
}

export function create(data) {
  return request({
    url: '/quoteModel/create',
    method: 'post',
    data
  })
}

export function read(id) {
  return request({
    url: '/quoteModel/read',
    method: 'get',
    params: { id }
  })
}

export function update(data) {
  return request({
    url: '/quoteModel/update',
    method: 'post',
    data
  })
}

export function quotedelete(data) {
  return request({
    url: '/quoteModel/delete',
    method: 'post',
    data
  })
}
