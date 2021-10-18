import request from '@/utils/request'

export function listCategory(query) {
  return request({
    url: '/xmcategory/list',
    method: 'get',
    params: query
  })
}

export function listCatL1() {
  return request({
    url: '/xmcategory/l1',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/xmcategory/create',
    method: 'post',
    data
  })
}

export function readCategory(data) {
  return request({
    url: '/xmcategory/read',
    method: 'get',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/xmcategory/update',
    method: 'post',
    data
  })
}

export function deleteCategory(data) {
  return request({
    url: '/xmcategory/delete',
    method: 'post',
    data
  })
}
