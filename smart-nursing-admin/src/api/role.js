import request from '@/utils/request'

// 角色列表
export function roleList(params) {
  return request({
    url: '/role/list',
    method: 'get',
    params
  })
}

// 新增角色
export function roleAdd(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

// 修改角色
export function roleUpdate(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

// 删除角色
export function roleDelete(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

// 根据ID获取角色
export function roleGetById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

// 分配菜单
export function assignMenus(data) {
  return request({
    url: '/role/assignMenus',
    method: 'post',
    data
  })
}

// 获取角色的菜单ID列表
export function getRoleMenus(roleId) {
  return request({
    url: `/role/menus/${roleId}`,
    method: 'get'
  })
}
