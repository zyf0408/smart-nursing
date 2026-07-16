/**
 * API 接口统一封装 - 智慧护理学员端
 * 
 * 模块划分:
 * - auth:     认证相关（登录）
 * - learn:    学习相关（内容列表/详情/进度上报/搜索/收藏）
 * - exam:     考试相关（考试列表/详情/开始/提交/结果）
 * - user:     用户相关（个人信息/修改密码/进度/记录）
 */
import http from './request'

// =============================================
// 认证模块 auth
// =============================================
const auth = {
  /**
   * 用户登录
   * @param {Object} data { username, password }
   */
  login(data) {
    return http.post('/api/auth/login', data, { hideLoading: false })
  },

  /**
   * 退出登录
   */
  logout() {
    return http.post('/api/auth/logout')
  }
}

// =============================================
// 学习模块 learn
// =============================================
const learn = {
  /**
   * 获取内容列表
   * @param {Object} params { page, pageSize, category, keyword, type }
   */
  getContentList(params) {
    return http.get('/api/learn/contents', params)
  },

  /**
   * 获取内容详情
   * @param {number|string} id 内容ID
   */
  getContentDetail(id) {
    return http.get(`/api/learn/contents/${id}`)
  },

  /**
   * 上报学习进度
   * @param {Object} data { contentId, duration, currentTime, progress }
   */
  reportProgress(data) {
    return http.post('/api/learn/progress', data, { hideLoading: true })
  },

  /**
   * 搜索学习内容
   * @param {Object} params { keyword, page, pageSize }
   */
  search(params) {
    return http.get('/api/learn/search', params)
  },

  /**
   * 添加收藏
   * @param {Object} data { contentId }
   */
  addFavorite(data) {
    return http.post('/api/learn/favorites', data)
  },

  /**
   * 取消收藏
   * @param {number|string} contentId 内容ID
   */
  removeFavorite(contentId) {
    return http.delete(`/api/learn/favorites/${contentId}`)
  },

  /**
   * 获取收藏列表
   * @param {Object} params { page, pageSize }
   */
  getFavoriteList(params) {
    return http.get('/api/learn/favorites', params)
  },

  /**
   * 获取学习记录列表
   * @param {Object} params { page, pageSize }
   */
  getLearningRecords(params) {
    return http.get('/api/learn/records', params)
  }
}

// =============================================
// 考试模块 exam
// =============================================
const exam = {
  /**
   * 获取考试列表
   * @param {Object} params { page, pageSize, status }
   */
  getExamList(params) {
    return http.get('/api/exams', params)
  },

  /**
   * 获取考试详情
   * @param {number|string} id 考试ID
   */
  getExamDetail(id) {
    return http.get(`/api/exams/${id}`)
  },

  /**
   * 开始考试（获取试卷题目）
   * @param {number|string} id 考试ID
   */
  startExam(id) {
    return http.post(`/api/exams/${id}/start`)
  },

  /**
   * 提交考试答案
   * @param {Object} data { examId, answers, startTime, endTime }
   */
  submitExam(data) {
    return http.post('/api/exams/submit', data)
  },

  /**
   * 获取考试结果
   * @param {number|string} recordId 考试记录ID
   */
  getExamResult(recordId) {
    return http.get(`/api/exams/result/${recordId}`)
  },

  /**
   * 获取考试记录列表
   * @param {Object} params { page, pageSize }
   */
  getExamRecords(params) {
    return http.get('/api/exams/records', params)
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
    return http.get('/api/user/info')
  },

  /**
   * 修改个人信息
   * @param {Object} data { name, avatar, phone, ... }
   */
  updateProfile(data) {
    return http.put('/api/user/profile', data)
  },

  /**
   * 修改密码
   * @param {Object} data { oldPassword, newPassword }
   */
  changePassword(data) {
    return http.post('/api/user/change-password', data)
  },

  /**
   * 获取学习进度统计
   */
  getProgress() {
    return http.get('/api/user/progress')
  },

  /**
   * 获取学习记录
   * @param {Object} params { page, pageSize }
   */
  getRecords(params) {
    return http.get('/api/user/records', params)
  },

  /**
   * 获取考试记录
   * @param {Object} params { page, pageSize }
   */
  getExamRecords(params) {
    return http.get('/api/user/exam-records', params)
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
