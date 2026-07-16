import request from '@/utils/request'

// 全部标签
export function tagAll() {
  return request({
    url: '/tag/all',
    method: 'get'
  })
}

// 标签列表
export function tagList(params) {
  return request({
    url: '/tag/list',
    method: 'get',
    params
  })
}

// 新增标签
export function tagAdd(data) {
  return request({
    url: '/tag',
    method: 'post',
    data
  })
}

// 修改标签
export function tagUpdate(data) {
  return request({
    url: '/tag',
    method: 'put',
    data
  })
}

// 删除标签
export function tagDelete(id) {
  return request({
    url: `/tag/${id}`,
    method: 'delete'
  })
}
