import request from '@/utils/request'

// 学习记录列表
export function learningList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/learning/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}
