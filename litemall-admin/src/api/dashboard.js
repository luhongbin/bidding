import request from '@/utils/request'

export function info(query) {
  return request({
    url: '/dashboard/info',
    method: 'get',
    params: query
  })
}
export function quote(query) {
  return request({
    url: '/dashboard/quote',
    method: 'get',
    params: query
  })
}
