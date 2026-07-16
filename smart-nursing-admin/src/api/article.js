import request from '@/utils/request'

// 文章列表
export function articleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

// 根据ID获取文章
export function articleGetById(id) {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

// 新增文章
export function articleAdd(data) {
  return request({
    url: '/article',
    method: 'post',
    data
  })
}

// 修改文章
export function articleUpdate(data) {
  return request({
    url: '/article',
    method: 'put',
    data
  })
}

// 删除文章
export function articleDelete(id) {
  return request({
    url: `/article/${id}`,
    method: 'delete'
  })
}
