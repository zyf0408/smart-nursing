import request from '@/utils/request'

// 日志列表（后端 POST + @RequestParam）
export function logList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/log/list',
    method: 'post',
    params: { ...rest, pageNo: pageNum || 1 }
  })
}
