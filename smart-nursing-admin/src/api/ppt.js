import request from '@/utils/request'

// PPT列表
export function pptList(params) {
  return request({
    url: '/ppt/list',
    method: 'get',
    params
  })
}

// 根据ID获取PPT
export function pptGetById(id) {
  return request({
    url: `/ppt/${id}`,
    method: 'get'
  })
}

// 新增PPT
export function pptAdd(data) {
  return request({
    url: '/ppt',
    method: 'post',
    data
  })
}

// 修改PPT
export function pptUpdate(data) {
  return request({
    url: '/ppt',
    method: 'put',
    data
  })
}

// 删除PPT
export function pptDelete(id) {
  return request({
    url: `/ppt/${id}`,
    method: 'delete'
  })
}
