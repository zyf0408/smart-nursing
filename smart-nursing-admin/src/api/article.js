import request from '@/utils/request'

// 文章列表（后端 POST + @RequestBody ArticleDto）
export function articleList(params) {
  const { pageNum, ...rest } = params
  return request({
    url: '/admin/article/list',
    method: 'post',
    data: { ...rest, pageNo: pageNum || 1 }
  })
}

// 根据ID获取文章
export function articleGetById(id) {
  return request({
    url: `/admin/article/getById/${id}`,
    method: 'get'
  })
}

// 新增文章
export function articleAdd(data) {
  return request({
    url: '/admin/article/add',
    method: 'post',
    data
  })
}

// 修改文章
export function articleUpdate(data) {
  return request({
    url: '/admin/article/update',
    method: 'post',
    data
  })
}

// 删除文章
export function articleDelete(id) {
  return request({
    url: `/admin/article/delete/${id}`,
    method: 'post'
  })
}
