<template>
  <div class="ai-container">
    <!-- 左侧对话列表 -->
    <div class="ai-sidebar">
      <div class="sidebar-header">
        <el-button type="primary" :icon="Plus" class="new-chat-btn" @click="handleNewChat">新建对话</el-button>
      </div>
      <el-scrollbar class="conversation-list">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          :class="['conversation-item', { active: currentConversationId === conv.id }]"
          @click="handleSelectConversation(conv)"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span class="conv-title">{{ conv.title }}</span>
          <el-icon class="conv-delete" @click.stop="handleDeleteConversation(conv)"><Delete /></el-icon>
        </div>
        <el-empty v-if="conversations.length === 0" description="暂无对话" :image-size="80" />
      </el-scrollbar>
    </div>

    <!-- 右侧聊天窗口 -->
    <div class="ai-main">
      <!-- 消息列表 -->
      <div class="chat-window" ref="chatWindowRef">
        <div v-if="messages.length === 0" class="welcome-box">
          <el-icon size="64" color="#409eff"><ChatLineSquare /></el-icon>
          <h2>智慧护理 AI 助手</h2>
          <p>我可以帮你解答护理相关问题、生成试题、推荐学习内容</p>
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role]"
        >
          <div class="message-avatar">
            <el-avatar :size="36" v-if="msg.role === 'user'">
              <el-icon><User /></el-icon>
            </el-avatar>
            <el-avatar :size="36" v-else style="background: #409eff">
              <el-icon><ChatDotRound /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
          </div>
        </div>
        <!-- 加载中 -->
        <div v-if="loading" class="message-item assistant">
          <div class="message-avatar">
            <el-avatar :size="36" style="background: #409eff">
              <el-icon><ChatDotRound /></el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息，按 Enter 发送，Shift+Enter 换行..."
          resize="none"
          @keydown.enter="handleEnter"
          :disabled="loading"
        />
        <div class="input-actions">
          <el-button
            v-if="loading"
            type="danger"
            :icon="VideoPause"
            @click="handleStop"
          >停止生成</el-button>
          <el-button
            v-else
            type="primary"
            :icon="Promotion"
            :disabled="!inputMessage.trim()"
            @click="handleSend"
          >发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Delete,
  User,
  ChatDotRound,
  ChatLineSquare,
  Promotion,
  VideoPause
} from '@element-plus/icons-vue'
import { chatStream } from '@/api/ai'

const router = useRouter()

const conversations = ref([])
const currentConversationId = ref(null)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const chatWindowRef = ref()

let abortController = null

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatWindowRef.value) {
      chatWindowRef.value.scrollTop = chatWindowRef.value.scrollHeight
    }
  })
}

// 格式化消息（简单的换行处理）
const formatMessage = (content) => {
  if (!content) return ''
  return content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>')
}

// 新建对话
const handleNewChat = () => {
  currentConversationId.value = null
  messages.value = []
  inputMessage.value = ''
}

// 选择对话
const handleSelectConversation = (conv) => {
  currentConversationId.value = conv.id
  messages.value = conv.messages || []
  scrollToBottom()
}

// 删除对话
const handleDeleteConversation = (conv) => {
  ElMessageBox.confirm('确定删除该对话吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      const index = conversations.value.findIndex((c) => c.id === conv.id)
      if (index > -1) {
        conversations.value.splice(index, 1)
      }
      if (currentConversationId.value === conv.id) {
        handleNewChat()
      }
    })
    .catch(() => {})
}

// 回车发送
const handleEnter = (e) => {
  if (e.shiftKey) return
  e.preventDefault()
  handleSend()
}

// 发送消息
const handleSend = () => {
  const content = inputMessage.value.trim()
  if (!content || loading.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content })
  // 添加助手占位消息
  messages.value.push({ role: 'assistant', content: '' })

  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  // 如果没有对话ID，创建新对话
  if (!currentConversationId.value) {
    const newId = Date.now()
    currentConversationId.value = newId
    conversations.value.unshift({
      id: newId,
      title: content.substring(0, 20),
      messages: messages.value
    })
  }

  abortController = chatStream(
    {
      message: content,
      conversationId: currentConversationId.value
    },
    {
      onMessage: (data) => {
        const assistantMsg = messages.value[messages.value.length - 1]
        if (assistantMsg && assistantMsg.role === 'assistant') {
          assistantMsg.content += data.content || data.message || ''
          scrollToBottom()
        }
      },
      onError: (err) => {
        console.error('AI 对话错误', err)
        const assistantMsg = messages.value[messages.value.length - 1]
        if (assistantMsg && assistantMsg.role === 'assistant' && !assistantMsg.content) {
          assistantMsg.content = '抱歉，发生了错误，请稍后重试。'
        }
        loading.value = false
        abortController = null
      },
      onClose: () => {
        loading.value = false
        abortController = null
        // 更新对话标题（首次回复完成）
        const conv = conversations.value.find((c) => c.id === currentConversationId.value)
        if (conv) {
          conv.messages = [...messages.value]
        }
      }
    }
  )
}

// 停止生成
const handleStop = () => {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  loading.value = false
}

// 组件卸载时中断请求，防止内存泄漏
onUnmounted(() => {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
})

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped lang="scss">
.ai-container {
  display: flex;
  height: calc(100vh - 60px);
  background: #f0f2f5;
}

.ai-sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;

  .sidebar-header {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;

    .new-chat-btn {
      width: 100%;
    }
  }

  .conversation-list {
    flex: 1;
    overflow-y: auto;

    .conversation-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      cursor: pointer;
      border-bottom: 1px solid #f5f5f5;
      transition: background 0.2s;

      &:hover {
        background: #f5f7fa;
      }

      &.active {
        background: #ecf5ff;
        color: #409eff;
      }

      .conv-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 14px;
      }

      .conv-delete {
        opacity: 0;
        transition: opacity 0.2s;
        color: #f56c6c;
      }

      &:hover .conv-delete {
        opacity: 1;
      }
    }
  }
}

.ai-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .chat-window {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    max-width: 900px;
    width: 100%;
    margin: 0 auto;

    .welcome-box {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: #909399;

      h2 {
        margin: 16px 0 8px 0;
        color: #303133;
      }

      p {
        color: #909399;
      }
    }

    .message-item {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;

      .message-content {
        flex: 1;
        max-width: 80%;
      }

      .message-text {
        padding: 12px 16px;
        border-radius: 8px;
        font-size: 14px;
        line-height: 1.6;
        word-break: break-word;
      }

      &.user {
        .message-content {
          display: flex;
          justify-content: flex-end;
        }

        .message-text {
          background: #409eff;
          color: #fff;
          border-radius: 8px 8px 0 8px;
        }
      }

      &.assistant {
        .message-text {
          background: #fff;
          color: #303133;
          border-radius: 8px 8px 8px 0;
          border: 1px solid #ebeef5;
        }
      }
    }
  }

  .chat-input-area {
    padding: 16px 20px;
    background: #fff;
    border-top: 1px solid #e6e6e6;
    max-width: 900px;
    width: 100%;
    margin: 0 auto;

    .input-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 10px;
    }
  }
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 14px 16px;
  background: #fff;
  border-radius: 8px 8px 8px 0;
  border: 1px solid #ebeef5;
  width: fit-content;

  span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #c0c4cc;
    animation: typing 1.4s infinite ease-in-out;

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}
</style>
