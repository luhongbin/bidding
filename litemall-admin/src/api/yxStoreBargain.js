import request from '@/utils/request'

export function listBargain(query) {
  return request({
    url: 'api/yxStoreBargain',
    method: 'get',
    params: query
  })
}

export function add(data) {
  return request({
    url: 'api/yxStoreBargain',
    method: 'post',
    data
  })
}

export function del(id) {
  return request({
    url: 'api/yxStoreBargain/' + id,
    method: 'delete'
  })
}

export function edit(data) {
  return request({
    url: 'api/yxStoreBargain',
    method: 'put',
    data
  })
}
