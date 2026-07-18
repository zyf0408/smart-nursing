import request from '@/utils/request'

// 试题列表
export function questionList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/question/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 新增试题
export function questionAdd(data) {
  return request({
    url: '/admin/question/add',
    method: 'post',
    data
  })
}

// 修改试题
export function questionUpdate(data) {
  return request({
    url: '/admin/question/update',
    method: 'post',
    data
  })
}

// 删除试题
export function questionDelete(id) {
  return request({
    url: `/admin/question/delete/${id}`,
    method: 'post'
  })
}
