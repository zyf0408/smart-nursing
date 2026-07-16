import request from '@/utils/request'

// 菜单树
export function menuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

// 菜单列表
export function menuList(params) {
  return request({
    url: '/menu/list',
    method: 'get',
    params
  })
}

// 新增菜单
export function menuAdd(data) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

// 修改菜单
export function menuUpdate(data) {
  return request({
    url: '/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function menuDelete(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}
