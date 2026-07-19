/**
 * API 接口统一封装 - 智慧护理学员端
 *
 * 路径说明:
 * - 所有路径不含 /api 前缀，由 BASE_URL 统一拼接
 * - Vite proxy 将 /api 代理到后端 http://localhost:8888 并移除 /api 前缀
 * - 后端实际路径: /login, /logout, /mobile/learn/list, /mobile/user/info 等
 */
import http from './request'

// =============================================
// 认证模块 auth
// =============================================
const auth = {
  /**
   * 用户登录
   * 后端 @RequestParam 接收，需以查询参数方式传递
   * @param {Object} data { username, password }
   */
  login(data) {
    const query = Object.keys(data)
      .map(k => `${k}=${encodeURIComponent(data[k])}`)
      .join('&')
    return http.post(`/login?${query}`, {}, { hideLoading: false })
  },

  /**
   * 退出登录
   */
  logout() {
    return http.get('/logout')
  }
}

// =============================================
// 学习模块 learn
// =============================================
const learn = {
  /**
   * 获取内容列表
   * @param {Object} params { categoryId, contentType, keyword }
   */
  getContentList(params) {
    return http.get('/mobile/learn/list', params)
  },

  /**
   * 获取内容详情
   * @param {number|string} contentType 内容类型 1文章 2视频 3PPT
   * @param {number|string} contentId 内容ID
   */
  getContentDetail(contentType, contentId) {
    return http.get(`/mobile/learn/detail/${contentType}/${contentId}`)
  },

  /**
   * 上报学习进度
   * 后端 @RequestParam 接收，需以查询参数方式传递
   * @param {Object} data { contentType, contentId, progress, studyDuration }
   */
  reportProgress(data) {
    const query = Object.keys(data)
      .filter(k => data[k] !== undefined && data[k] !== null)
      .map(k => `${k}=${encodeURIComponent(data[k])}`)
      .join('&')
    return http.post(`/mobile/learn/progress?${query}`, {}, { hideLoading: true })
  },

  /**
   * 搜索学习内容
   * @param {Object} params { keyword }
   */
  search(params) {
    return http.get('/mobile/learn/search', params)
  },

  /**
   * 添加收藏
   * 后端 @RequestParam 接收，需以查询参数方式传递
   * @param {Object} data { contentType, contentId }
   */
  addFavorite(data) {
    const query = `contentType=${data.contentType}&contentId=${data.contentId}`
    return http.post(`/mobile/learn/favorite/add?${query}`, {}, { hideLoading: true })
  },

  /**
   * 取消收藏
   * 后端 @RequestParam 接收，需以查询参数方式传递
   * @param {Object} data { contentType, contentId }
   */
  removeFavorite(data) {
    const query = `contentType=${data.contentType}&contentId=${data.contentId}`
    return http.post(`/mobile/learn/favorite/remove?${query}`, {}, { hideLoading: true })
  },

  /**
   * 获取收藏列表
   */
  getFavoriteList() {
    return http.get('/mobile/learn/favorite/list')
  },

  /**
   * 获取学习记录列表
   * @param {Object} params { pageNo, pageSize }
   */
  getLearningRecords(params) {
    return http.get('/mobile/user/records', params)
  }
}

// =============================================
// 考试模块 exam
// =============================================
const exam = {
  /**
   * 获取考试列表
   */
  getExamList() {
    return http.get('/mobile/exam/list')
  },

  /**
   * 获取考试详情
   * @param {number|string} id 考试ID
   */
  getExamDetail(id) {
    return http.get(`/mobile/exam/detail/${id}`)
  },

  /**
   * 开始考试
   * @param {number|string} id 考试ID
   */
  startExam(id) {
    return http.post(`/mobile/exam/start/${id}`, {}, { hideLoading: true })
  },

  /**
   * 提交考试答案
   * 后端 @RequestParam 接收 examId 和 answers（JSON 字符串）
   * @param {number|string} examId 考试ID
   * @param {string} answersJson 答案 JSON 字符串
   */
  submitExam(examId, answersJson) {
    const query = `examId=${examId}&answers=${encodeURIComponent(answersJson)}`
    return http.post(`/mobile/exam/submit?${query}`, {}, { hideLoading: true })
  },

  /**
   * 获取考试结果
   * @param {number|string} recordId 考试记录ID
   */
  getExamResult(recordId) {
    return http.get(`/mobile/exam/result/${recordId}`)
  },

  /**
   * 获取考试记录列表
   * @param {Object} params { pageNo, pageSize }
   */
  getExamRecords(params) {
    return http.get('/mobile/user/exams', params)
  }
}

// =============================================
// 用户模块 user
// =============================================
const user = {
  /**
   * 获取用户信息
   */
  getUserInfo() {
    return http.get('/mobile/user/info')
  },

  /**
   * 修改个人信息
   * @param {Object} data { realName, phone, email, avatar }
   */
  updateProfile(data) {
    return http.post('/mobile/user/updateProfile', data)
  },

  /**
   * 修改密码
   * @param {Object} data { oldPassword, newPassword }
   */
  changePassword(data) {
    const params = new URLSearchParams()
    params.append('oldPassword', data.oldPassword || '')
    params.append('newPassword', data.newPassword || '')
    return http.post(`/mobile/user/changePassword?${params.toString()}`)
  },

  /**
   * 获取学习进度统计
   */
  getProgress() {
    return http.get('/mobile/user/progress')
  },

  /**
   * 获取学习记录
   * @param {Object} params { pageNo, pageSize }
   */
  getRecords(params) {
    return http.get('/mobile/user/records', params)
  },

  /**
   * 获取考试记录
   * @param {Object} params { pageNo, pageSize }
   */
  getExamRecords(params) {
    return http.get('/mobile/user/exams', params)
  }
}

// =============================================
// 统一导出
// =============================================
export default {
  auth,
  learn,
  exam,
  user
}

export { auth, learn, exam, user }
