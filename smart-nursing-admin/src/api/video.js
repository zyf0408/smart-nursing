import request from '@/utils/request'

// 视频列表
export function videoList(params) {
  return request({
    url: '/video/list',
    method: 'get',
    params
  })
}

// 根据ID获取视频
export function videoGetById(id) {
  return request({
    url: `/video/${id}`,
    method: 'get'
  })
}

// 新增视频
export function videoAdd(data) {
  return request({
    url: '/video',
    method: 'post',
    data
  })
}

// 修改视频
export function videoUpdate(data) {
  return request({
    url: '/video',
    method: 'put',
    data
  })
}

// 删除视频
export function videoDelete(id) {
  return request({
    url: `/video/${id}`,
    method: 'delete'
  })
}
