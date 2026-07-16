import request from '@/utils/request'

// 类别树
export function categoryTree() {
  return request({
    url: '/category/tree',
    method: 'get'
  })
}

// 新增类别
export function categoryAdd(data) {
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

// 修改类别
export function categoryUpdate(data) {
  return request({
    url: '/category',
    method: 'put',
    data
  })
}

// 删除类别
export function categoryDelete(id) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}
