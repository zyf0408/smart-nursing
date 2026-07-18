import request from '@/utils/request'

// AI 同步对话
export function chatSync(data) {
  return request({
    url: '/ai/chat/sync',
    method: 'post',
    params: { message: data.message }
  })
}

// AI 生成试题
export function generateQuestion(data) {
  return request({
    url: '/ai/question/generate',
    method: 'post',
    params: { examId: data.examId, questionType: data.questionType }
  })
}

// AI 推荐
export function recommend(data) {
  return request({
    url: `/ai/recommend/${data.userId}`,
    method: 'get'
  })
}

// AI 生成图片
export function generateImage(data) {
  return request({
    url: '/ai/image/generate',
    method: 'post',
    params: { prompt: data.prompt }
  })
}

/**
 * AI 流式对话 - 使用 Fetch + SSE
 * 后端 GET /ai/chat/stream?sessionId=xxx&message=xxx
 */
export function chatStream(data, callbacks = {}) {
  const controller = new AbortController()
  const token = localStorage.getItem('token')
  const baseURL = import.meta.env.VITE_API_BASE_URL

  const params = new URLSearchParams({
    sessionId: data.sessionId || data.conversationId || 'default',
    message: data.message
  })

  fetch(`${baseURL}/ai/chat/stream?${params}`, {
    method: 'GET',
    headers: {
      'token': token
    },
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
