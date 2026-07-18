import request from '@/utils/request'

// 类别树（公共接口，无需鉴权）
export function categoryTree() {
  return request({
    url: '/common/category/tree',
    method: 'get'
  })
}

// 新增类别
export function categoryAdd(data) {
  return request({
    url: '/admin/category/add',
    method: 'post',
    data
  })
}

// 修改类别
export function categoryUpdate(data) {
  return request({
    url: '/admin/category/update',
    method: 'post',
    data
  })
}

// 删除类别
export function categoryDelete(id) {
  return request({
    url: `/admin/category/delete/${id}`,
    method: 'post'
  })
}
