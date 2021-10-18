import request from '@/utils/request'

export function listReQuote(query) {
  return request({
    url: '/requote/list',
    method: 'get',
    params: query
  })
}
export function listReCeo(query) {
  return request({
    url: '/requote/listCeo',
    method: 'get',
    params: query
  })
}
export function find(id, modelId) {
  return request({
    url: '/requote/find',
    method: 'post',
    params: { id, modelId }
  })
}
export function updateQuote(data) {
  return request({
    url: '/quoteBill/update',
    method: 'post',
    data
  })
}
export function updateRequote(data) {
  return request({
    url: '/requote/update',
    method: 'post',
    data
  })
}
export function deleteQuote(data) {
  return request({
    url: '/requote/delete',
    method: 'post',
    data
  })
}
export function listBrowser(query) {
  return request({
    url: '/requote/listBrowser',
    method: 'get',
    params: query
  })
}
export function listBrowserCeo(query) {
  return request({
    url: '/requote/listBrowserCeo',
    method: 'get',
    params: query
  })
}
export function listBrowserOK(query) {
  return request({
    url: '/requote/listBrowserOK',
    method: 'get',
    params: query
  })
}
export function myRead(id) {
  return request({
    url: '/requote/myRead',
    method: 'get',
    params: { id }
  })
}

export function readQuote(query) {
  return request({
    url: '/requote/readQuote',
    method: 'get',
    params: query
  })
}
