import request from '@/utils/request'

// 全部标签
export function tagAll() {
  return request({
    url: '/admin/tag/all',
    method: 'get'
  })
}

// 标签列表（后端 POST + @RequestParam）
export function tagList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/tag/list',
    method: 'post',
    params: { ...rest, pageNo: pageNum || 1 }
  })
}

// 新增标签
export function tagAdd(data) {
  return request({
    url: '/admin/tag/add',
    method: 'post',
    data
  })
}

// 修改标签
export function tagUpdate(data) {
  return request({
    url: '/admin/tag/update',
    method: 'post',
    data
  })
}

// 删除标签
export function tagDelete(id) {
  return request({
    url: `/admin/tag/delete/${id}`,
    method: 'post'
  })
}
