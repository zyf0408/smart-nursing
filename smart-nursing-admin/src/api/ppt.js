import request from '@/utils/request'

// PPT列表
export function pptList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/ppt/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 根据ID获取PPT
export function pptGetById(id) {
  return request({
    url: `/admin/ppt/getById/${id}`,
    method: 'get'
  })
}

// 新增PPT
export function pptAdd(data) {
  return request({
    url: '/admin/ppt/add',
    method: 'post',
    data
  })
}

// 修改PPT
export function pptUpdate(data) {
  return request({
    url: '/admin/ppt/update',
    method: 'post',
    data
  })
}

// 删除PPT
export function pptDelete(id) {
  return request({
    url: `/admin/ppt/delete/${id}`,
    method: 'post'
  })
}
