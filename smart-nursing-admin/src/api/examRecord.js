import request from '@/utils/request'

// 考试记录列表
export function recordList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/examRecord/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 考试记录详情
export function recordDetail(id) {
  return request({
    url: `/admin/examRecord/detail/${id}`,
    method: 'get'
  })
}
