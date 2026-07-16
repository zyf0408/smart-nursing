import request from '@/utils/request'

// 首页统计数据
export function dashboardData() {
  return request({
    url: '/dataCount/dashboard',
    method: 'get'
  })
}
