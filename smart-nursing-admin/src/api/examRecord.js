import request from '@/utils/request'

// 考试记录列表
export function recordList(params) {
  return request({
    url: '/examRecord/list',
    method: 'get',
    params
  })
}

// 考试记录详情
export function recordDetail(id) {
  return request({
    url: `/examRecord/${id}`,
    method: 'get'
  })
}
