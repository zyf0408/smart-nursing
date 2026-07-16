import request from '@/utils/request'

// 用户列表
export function userList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 新增用户
export function userAdd(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 修改用户
export function userUpdate(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

// 删除用户
export function userDelete(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 根据ID获取用户
export function userGetById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 分配角色
export function assignRoles(data) {
  return request({
    url: '/user/assignRoles',
    method: 'post',
    data
  })
}
