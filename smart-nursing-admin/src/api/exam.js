import request from '@/utils/request'

// 考试列表
export function examList(params) {
  return request({
    url: '/exam/list',
    method: 'get',
    params
  })
}

// 根据ID获取考试
export function examGetById(id) {
  return request({
    url: `/exam/${id}`,
    method: 'get'
  })
}

// 新增考试
export function examAdd(data) {
  return request({
    url: '/exam',
    method: 'post',
    data
  })
}

// 修改考试
export function examUpdate(data) {
  return request({
    url: '/exam',
    method: 'put',
    data
  })
}

// 删除考试
export function examDelete(id) {
  return request({
    url: `/exam/${id}`,
    method: 'delete'
  })
}

// 发布考试
export function examPublish(id) {
  return request({
    url: `/exam/publish/${id}`,
    method: 'post'
  })
}
