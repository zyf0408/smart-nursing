import request from '@/utils/request'

// 用户列表（后端 POST + @RequestBody UserDto，分页字段为 pageNo）
export function userList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/user/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 新增用户
export function userAdd(data) {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

// 修改用户
export function userUpdate(data) {
  return request({
    url: '/admin/user/update',
    method: 'post',
    data
  })
}

// 删除用户
export function userDelete(id) {
  return request({
    url: `/admin/user/delete/${id}`,
    method: 'post'
  })
}

// 根据ID获取用户
export function userGetById(id) {
  return request({
    url: `/admin/user/getById/${id}`,
    method: 'get'
  })
}

// 分配角色（后端 @RequestParam userId + @RequestBody List<Long> roleIds）
export function assignRoles(data) {
  return request({
    url: `/admin/user/assignRoles?userId=${data.userId}`,
    method: 'post',
    data: data.roleIds
  })
}
