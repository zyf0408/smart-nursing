import request from '@/utils/request'

// AI 同步对话
export function chatSync(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}

// AI 生成试题
export function generateQuestion(data) {
  return request({
    url: '/ai/generateQuestion',
    method: 'post',
    data
  })
}

// AI 推荐
export function recommend(data) {
  return request({
    url: '/ai/recommend',
    method: 'post',
    data
  })
}

// AI 生成图片
export function generateImage(data) {
  return request({
    url: '/ai/generateImage',
    method: 'post',
    data
  })
}

/**
 * AI 流式对话 - 使用 Fetch + SSE
 * @param {Object} data - { message, conversationId }
 * @param {Object} callbacks - { onMessage, onError, onClose }
 * @returns {AbortController} 用于中断请求
 */
export function chatStream(data, callbacks = {}) {
  const controller = new AbortController()
  const token = localStorage.getItem('token')
  const baseURL = import.meta.env.VITE_API_BASE_URL

  fetch(`${baseURL}/ai/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(data),
    signal: controller.signal
  })
    .then(async (response) => {
      if (response.status === 401) {
        callbacks.onError && callbacks.onError(new Error('登录已过期，请重新登录'))
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('roleCode')
        localStorage.removeItem('menuList')
        window.location.href = '/'
        return
      }
      if (!response.ok) {
        callbacks.onError && callbacks.onError(new Error('请求失败'))
        return
      }
      const reader = response.body.getReader()
      const decoder = new TextDecoder('utf-8')
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''
        for (const line of lines) {
          const trimmed = line.trim()
          if (trimmed.startsWith('data:')) {
            const dataStr = trimmed.slice(5)
            if (dataStr === '[DONE]') {
              callbacks.onClose && callbacks.onClose()
              return
            }
            try {
              const parsed = JSON.parse(dataStr)
              callbacks.onMessage && callbacks.onMessage(parsed)
            } catch {
              callbacks.onMessage && callbacks.onMessage({ content: dataStr })
            }
          }
        }
      }
      callbacks.onClose && callbacks.onClose()
    })
    .catch((err) => {
      if (err.name !== 'AbortError') {
        callbacks.onError && callbacks.onError(err)
      }
    })

  return controller
}
