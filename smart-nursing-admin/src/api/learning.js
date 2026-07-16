import request from '@/utils/request'

// 学习记录列表
export function learningList(params) {
  return request({
    url: '/learning/list',
    method: 'get',
    params
  })
}
