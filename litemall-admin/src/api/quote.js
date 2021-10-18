import request from '@/utils/request'

export function listQuote(query) {
  return request({
    url: '/quoteBill/list',
    method: 'get',
    params: query
  })
}

export function sign() {
  return request({
    url: '/quoteBill/sign',
    method: 'get'
  })
}
export function listCeo(query) {
  return request({
    url: '/quoteBill/listCeo',
    method: 'get',
    params: query
  })
}
export function listBrowser(query) {
  return request({
    url: '/quoteBill/listBrowser',
    method: 'get',
    params: query
  })
}
export function listBrowserCeo(query) {
  return request({
    url: '/quoteBill/listBrowserCeo',
    method: 'get',
    params: query
  })
}
export function listBrowserOK(query) {
  return request({
    url: '/quoteBill/listBrowserOK',
    method: 'get',
    params: query
  })
}
export function createQuote(data) {
  return request({
    url: '/quoteBill/create',
    method: 'post',
    data
  })
}

export function updateQuote(data) {
  return request({
    url: '/quoteBill/update',
    method: 'post',
    data
  })
}
export function cancleQuote(data) {
  return request({
    url: '/quoteBill/cancel',
    method: 'post',
    data
  })
}
export function submitQuote(data) {
  return request({
    url: '/quoteBill/submit',
    method: 'post',
    data
  })
}
export function deleteQuote(data) {
  return request({
    url: '/quoteBill/delete',
    method: 'post',
    data
  })
}

export function detail(query) {
  return request({
    url: '/quoteBill/detail',
    method: 'get',
    params: query
  })
}

export function listChannel(id) {
  return request({
    url: '/quoteBill/channel',
    method: 'get',
    params: { id }
  })
}
export function find(id, modelId) {
  return request({
    url: '/quoteBill/find',
    method: 'post',
    params: { id, modelId }
  })
}
export function readquote(id) {
  return request({
    url: '/quoteBill/readquote',
    method: 'post',
    params: { id }
  })
}