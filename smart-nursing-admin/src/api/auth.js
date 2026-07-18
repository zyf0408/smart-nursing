import request from '@/utils/request'

// 登录（后端 @RequestParam 接收，需以查询参数方式传递）
export function login(data) {
  const query = Object.keys(data)
    .map(k => `${k}=${encodeURIComponent(data[k])}`)
    .join('&')
  return request({
    url: `/login?${query}`,
    method: 'post'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/logout',
    method: 'get'
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

// 获取当前用户菜单列表
export function getMenuList() {
  return request({
    url: '/auth/menus',
    method: 'get'
  })
}
