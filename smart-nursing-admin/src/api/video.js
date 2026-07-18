import request from '@/utils/request'

// 视频列表
export function videoList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/video/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 根据ID获取视频
export function videoGetById(id) {
  return request({
    url: `/admin/video/getById/${id}`,
    method: 'get'
  })
}

// 新增视频
export function videoAdd(data) {
  return request({
    url: '/admin/video/add',
    method: 'post',
    data
  })
}

// 修改视频
export function videoUpdate(data) {
  return request({
    url: '/admin/video/update',
    method: 'post',
    data
  })
}

// 删除视频
export function videoDelete(id) {
  return request({
    url: `/admin/video/delete/${id}`,
    method: 'post'
  })
}
