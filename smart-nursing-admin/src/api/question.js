import request from '@/utils/request'

// 试题列表
export function questionList(params) {
  return request({
    url: '/question/list',
    method: 'get',
    params
  })
}

// 新增试题
export function questionAdd(data) {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

// 修改试题
export function questionUpdate(data) {
  return request({
    url: '/question',
    method: 'put',
    data
  })
}

// 删除试题
export function questionDelete(id) {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
}
