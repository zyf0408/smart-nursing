import request from '@/utils/request'

// 日志列表
export function logList(params) {
  return request({
    url: '/log/list',
    method: 'get',
    params
  })
}
