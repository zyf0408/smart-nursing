/**
 * 请求封装 - 智慧护理学员端
 * 
 * 特性:
 * 1. 条件编译区分 H5 / App / 小程序 的 BASE_URL
 * 2. 401 全局防抖锁 isRedirecting，防止并发请求时重复跳转登录页
 * 3. 统一 Token 注入 Header
 * 4. 统一错误处理，使用 Promise.reject 抛出错误（非 return -1）
 */

// =============================================
// BASE_URL 条件编译
// H5 使用 Vite 环境变量 import.meta.env.VITE_API_BASE_URL
// 非 H5（App / 小程序）使用硬编码地址
// =============================================
let BASE_URL = 'http://localhost:8888'

// #ifdef H5
BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888'
// #endif

// 非H5环境（App、小程序等）直接使用完整地址
// #ifndef H5
BASE_URL = 'http://localhost:8888'
// #endif

/**
 * 401 全局防抖锁
 * 当多个并发请求同时返回 401 时，防止重复跳转登录页
 */
let isRedirecting = false

/**
 * 跳转登录页（带防抖锁）
 */
function redirectToLogin() {
  if (isRedirecting) return
  isRedirecting = true
  // 清除本地存储的 token 和用户信息
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
  uni.showModal({
    title: '提示',
    content: '登录已过期，请重新登录',
    showCancel: false,
    success: () => {
      isRedirecting = false
      uni.reLaunch({
        url: '/pages/login/login'
      })
    },
    fail: () => {
      isRedirecting = false
    }
  })
}

/**
 * 核心请求方法
 * @param {Object} options 请求配置
 * @param {string} options.url 请求路径（相对路径，会拼接 BASE_URL）
 * @param {string} options.method 请求方法 GET/POST/PUT/DELETE
 * @param {Object} options.data 请求参数
 * @param {Object} options.header 自定义请求头
 * @param {boolean} options.hideLoading 是否隐藏 loading 提示
 * @returns {Promise} 返回 Promise 对象
 */
function request(options) {
  const {
    url,
    method = 'GET',
    data = {},
    header = {},
    hideLoading = false
  } = options

  // 拼接完整请求地址
  const fullUrl = url.startsWith('http') ? url : BASE_URL + url

  // 统一注入 Token 到请求头
  const token = uni.getStorageSync('token')
  const requestHeader = {
    'Content-Type': 'application/json',
    ...header
  }
  if (token) {
    requestHeader['token'] = token
    requestHeader['Authorization'] = `Bearer ${token}`
  }

  // 显示 loading
  if (!hideLoading) {
    uni.showLoading({
      title: '加载中...',
      mask: true
    })
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method: method.toUpperCase(),
      data: data,
      header: requestHeader,
      timeout: 30000,
      success: (res) => {
        const statusCode = res.statusCode
        const responseData = res.data

        // 隐藏 loading
        if (!hideLoading) {
          uni.hideLoading()
        }

        // HTTP 状态码处理
        if (statusCode === 200) {
          // 业务状态码处理（假设后端返回 { code, message, data } 格式）
          if (responseData.code === 200 || responseData.code === 0 || responseData.success === true) {
            resolve(responseData.data !== undefined ? responseData.data : responseData)
          } else if (responseData.code === 401) {
            // 业务层 401 未授权
            redirectToLogin()
            reject(new Error(responseData.msg || responseData.message || '登录已过期'))
          } else {
            const errMsg = responseData.msg || responseData.message || '请求失败'
            // 当调用方自行管理 loading 时（hideLoading=true），不在此处弹 toast，
            // 避免与调用方的 showLoading mask 冲突导致灰屏，由 catch 块自行处理
            if (!hideLoading) {
              uni.showToast({
                title: errMsg,
                icon: 'none',
                duration: 2000
              })
            }
            reject(new Error(errMsg))
          }
        } else if (statusCode === 401) {
          // HTTP 401 未授权 - 触发防抖跳转
          redirectToLogin()
          reject(new Error('登录已过期，请重新登录'))
        } else if (statusCode === 403) {
          uni.showToast({
            title: '没有权限访问',
            icon: 'none'
          })
          reject(new Error('没有权限访问'))
        } else if (statusCode === 404) {
          uni.showToast({
            title: '请求资源不存在',
            icon: 'none'
          })
          reject(new Error('请求资源不存在'))
        } else if (statusCode === 500) {
          uni.showToast({
            title: '服务器内部错误',
            icon: 'none'
          })
          reject(new Error('服务器内部错误'))
        } else {
          uni.showToast({
            title: `请求错误(${statusCode})`,
            icon: 'none'
          })
          reject(new Error(`请求错误: ${statusCode}`))
        }
      },
      fail: (err) => {
        if (!hideLoading) {
          uni.hideLoading()
        }
        // 网络错误
        uni.showToast({
          title: '网络连接失败，请检查网络',
          icon: 'none',
          duration: 2000
        })
        reject(new Error(err.errMsg || '网络连接失败'))
      }
    })
  })
}

/**
 * 便捷方法封装
 */
const http = {
  /**
   * GET 请求
   */
  get(url, data = {}, options = {}) {
    return request({ url, method: 'GET', data, ...options })
  },

  /**
   * POST 请求
   */
  post(url, data = {}, options = {}) {
    return request({ url, method: 'POST', data, ...options })
  },

  /**
   * PUT 请求
   */
  put(url, data = {}, options = {}) {
    return request({ url, method: 'PUT', data, ...options })
  },

  /**
   * DELETE 请求
   */
  delete(url, data = {}, options = {}) {
    return request({ url, method: 'DELETE', data, ...options })
  },

  /**
   * 上传文件
   */
  upload(url, filePath, formData = {}, name = 'file') {
    const token = uni.getStorageSync('token')
    const header = {}
    if (token) {
      header['token'] = token
    }
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + url,
        filePath: filePath,
        name: name,
        formData: formData,
        header: header,
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            if (data.code === 200 || data.code === 0) {
              resolve(data.data !== undefined ? data.data : data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } else {
            reject(new Error(`上传失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '上传失败'))
        }
      })
    })
  }
}

export default http
export { BASE_URL, request }
