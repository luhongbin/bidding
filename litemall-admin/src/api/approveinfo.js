import request from '@/utils/request'
import Qs from 'qs'

export function listRequote(query) {
  return request({
    url: '/approveinfo/list',
    method: 'get',
    params: query
  })
}

export function detailApprove(query) {
  return request({
    url: '/approveinfo/detail',
    method: 'get',
    params: query
  })
}
export function quoteread(query) {
  return request({
    url: '/approveinfo/quoteread',
    method: 'get',
    params: query
  })
}

export function createApproveinfo(data) {
  return request({
    url: '/approveinfo/create',
    method: 'post',
    data
  })
}

