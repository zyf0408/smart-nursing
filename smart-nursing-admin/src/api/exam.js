import request from '@/utils/request'

// 考试列表
export function examList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/exam/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 根据ID获取考试
export function examGetById(id) {
  return request({
    url: `/admin/exam/getById/${id}`,
    method: 'get'
  })
}

// 新增考试
export function examAdd(data) {
  return request({
    url: '/admin/exam/add',
    method: 'post',
    data
  })
}

// 修改考试
export function examUpdate(data) {
  return request({
    url: '/admin/exam/update',
    method: 'post',
    data
  })
}

// 删除考试
export function examDelete(id) {
  return request({
    url: `/admin/exam/delete/${id}`,
    method: 'post'
  })
}

// 发布考试
export function examPublish(id) {
  return request({
    url: `/admin/exam/publish/${id}`,
    method: 'post'
  })
}
