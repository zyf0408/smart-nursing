import request from '@/utils/request'

// 菜单树
export function menuTree() {
  return request({
    url: '/admin/menu/tree',
    method: 'get'
  })
}

// 菜单列表
export function menuList(params) {
  return request({
    url: '/admin/menu/list',
    method: 'get',
    params
  })
}

// 新增菜单
export function menuAdd(data) {
  return request({
    url: '/admin/menu/add',
    method: 'post',
    data
  })
}

// 修改菜单
export function menuUpdate(data) {
  return request({
    url: '/admin/menu/update',
    method: 'post',
    data
  })
}

// 删除菜单
export function menuDelete(id) {
  return request({
    url: `/admin/menu/delete/${id}`,
    method: 'post'
  })
}
