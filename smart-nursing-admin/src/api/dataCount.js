import request from '@/utils/request'

// 首页统计数据
export function dashboardData() {
  return request({
    url: '/admin/dataCount/dashboard',
    method: 'get'
  })
}
