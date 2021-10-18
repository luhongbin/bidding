import request from '@/utils/request'

export function listTeu(query) {
  return request({
    url: '/teu/list',
    method: 'get',
    params: query
  })
}

export function listFile(query) {
  return request({
    url: '/file/list',
    method: 'get',
    params: query
  })
}

export function detailTeu(id) {
  return request({
    url: '/teu/detail',
    method: 'get',
    params: { id }
  })
}

export function deleteTeu(data) {
  return request({
    url: '/teu/delete',
    method: 'post',
    data
  })
}

export function publishTeu(data) {
  return request({
    url: '/teu/create',
    method: 'post',
    data
  })
}

export function publishFile(data) {
  return request({
    url: '/file/create',
    method: 'post',
    data
  })
}
// 附件上传
export function uploadFile(file) {
  return request({
    url: '/uploadFile',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data; charset=utf-8'
    },
    data: file
  })
}
export function editTeu(data) {
  return request({
    url: '/teu/update',
    method: 'post',
    data
  })
}
export function editFile(data) {
  return request({
    url: '/file/update',
    method: 'post',
    data
  })
}
export function listCatAndBrand() {
  return request({
    url: '/teu/catAndBrand',
    method: 'get'
  })
}
