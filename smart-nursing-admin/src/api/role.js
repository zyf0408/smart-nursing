import request from '@/utils/request'

// 角色列表（后端 POST + @RequestParam）
export function roleList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/role/list',
    method: 'post',
    params: { ...rest, pageNo: pageNum || 1 }
  })
}

// 新增角色
export function roleAdd(data) {
  return request({
    url: '/admin/role/add',
    method: 'post',
    data
  })
}

// 修改角色
export function roleUpdate(data) {
  return request({
    url: '/admin/role/update',
    method: 'post',
    data
  })
}

// 删除角色
export function roleDelete(id) {
  return request({
    url: `/admin/role/delete/${id}`,
    method: 'post'
  })
}

// 根据ID获取角色
export function roleGetById(id) {
  return request({
    url: `/admin/role/getById/${id}`,
    method: 'get'
  })
}

// 分配菜单（后端 @RequestParam roleId + @RequestBody List<Long> menuIds）
export function assignMenus(data) {
  return request({
    url: `/admin/role/assignMenus?roleId=${data.roleId}`,
    method: 'post',
    data: data.menuIds
  })
}

// 获取角色的菜单ID列表
export function getRoleMenus(roleId) {
  return request({
    url: `/admin/role/menus/${roleId}`,
    method: 'get'
  })
}
