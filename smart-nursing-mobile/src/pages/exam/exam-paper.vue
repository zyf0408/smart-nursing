<template>
  <view class="exam-paper-page">
    <!-- 顶部倒计时栏 -->
    <view class="top-bar">
      <view class="top-info">
        <text class="exam-name text-ellipsis">{{ examTitle }}</text>
      </view>
      <view class="countdown" :class="{ 'countdown-warning': remainingSeconds <= 300 }">
        <text class="countdown-icon">⏰</text>
        <text class="countdown-text">{{ formattedTime }}</text>
      </view>
    </view>

    <!-- 题目内容区 -->
    <scroll-view class="question-scroll" scroll-y :scroll-into-view="'question-' + currentIndex">
      <view v-if="questions.length > 0" class="question-container">
        <!-- 题目进度 -->
        <view class="question-progress">
          <text class="progress-text">第 {{ currentIndex + 1 }} / {{ questions.length }} 题</text>
          <view class="progress-bar-mini">
            <view class="progress-bar-inner" :style="{ width: ((currentIndex + 1) / questions.length * 100) + '%' }"></view>
          </view>
        </view>

        <!-- 当前题目 -->
        <view :id="'question-' + currentIndex" class="question-card">
          <view class="question-header">
            <text class="question-type" :class="getTypeClass(currentQuestion.type)">
              {{ getTypeText(currentQuestion.type) }}
            </text>
            <text class="question-score">（{{ currentQuestion.score || 0 }}分）</text>
          </view>

          <text class="question-title">{{ currentIndex + 1 }}. {{ currentQuestion.title }}</text>

          <!-- 单选题 -->
          <view v-if="currentQuestion.type === 'single'" class="options-list">
            <view
              v-for="option in currentQuestion.options"
              :key="option.key"
              class="option-item"
              :class="{ selected: isSelected(currentQuestion, option.key) }"
              @tap="selectSingle(currentQuestion, option.key)"
            >
              <view class="option-radio" :class="{ checked: isSelected(currentQuestion, option.key) }">
                <text v-if="isSelected(currentQuestion, option.key)" class="radio-dot"></text>
              </view>
              <text class="option-label">{{ option.key }}</text>
              <text class="option-text">{{ option.value }}</text>
            </view>
          </view>

          <!-- 多选题 -->
          <view v-else-if="currentQuestion.type === 'multiple'" class="options-list">
            <view
              v-for="option in currentQuestion.options"
              :key="option.key"
              class="option-item"
              :class="{ selected: isSelected(currentQuestion, option.key) }"
              @tap="selectMultiple(currentQuestion, option.key)"
            >
              <view class="option-checkbox" :class="{ checked: isSelected(currentQuestion, option.key) }">
                <text v-if="isSelected(currentQuestion, option.key)" class="check-mark">✓</text>
              </view>
              <text class="option-label">{{ option.key }}</text>
              <text class="option-text">{{ option.value }}</text>
            </view>
          </view>

          <!-- 判断题 -->
          <view v-else-if="currentQuestion.type === 'judge'" class="options-list">
            <view
              v-for="option in judgeOptions"
              :key="option.key"
              class="option-item"
              :class="{ selected: isSelected(currentQuestion, option.key) }"
              @tap="selectSingle(currentQuestion, option.key)"
            >
              <view class="option-radio" :class="{ checked: isSelected(currentQuestion, option.key) }">
                <text v-if="isSelected(currentQuestion, option.key)" class="radio-dot"></text>
              </view>
              <text class="option-label">{{ option.key }}</text>
              <text class="option-text">{{ option.value }}</text>
            </view>
          </view>

          <!-- 解答题 -->
          <view v-else-if="currentQuestion.type === 'essay'" class="essay-area">
            <textarea
              v-model="userAnswers[currentQuestion.id]"
              class="essay-textarea"
              placeholder="请在此输入你的解答..."
              :maxlength="2000"
              auto-height
            />
            <view class="essay-tip">
              <text>解答题将由AI智能评分，请尽量完整作答</text>
            </view>
          </view>
        </view>

        <!-- 上一题/下一题按钮 -->
        <view class="question-nav">
          <button
            v-if="currentIndex > 0"
            class="nav-btn nav-prev"
            @tap="prevQuestion"
          >上一题</button>
          <view v-else></view>
          <button
            v-if="currentIndex < questions.length - 1"
            class="nav-btn nav-next"
            @tap="nextQuestion"
          >下一题</button>
          <button
            v-else
            class="nav-btn nav-submit"
            @tap="confirmSubmit"
          >提交答卷</button>
        </view>
      </view>

      <!-- 加载中 -->
      <view v-else class="loading-wrapper">
        <text>加载试卷中...</text>
      </view>
    </scroll-view>

    <!-- 底部答题卡 -->
    <view class="answer-card-bar" @tap="toggleAnswerCard">
      <view class="answer-card-summary">
        <text class="answered-count">已答 {{ answeredCount }}/{{ questions.length }}</text>
        <text class="unanswered-count">未答 {{ questions.length - answeredCount }}</text>
      </view>
      <text class="card-toggle-text">答题卡</text>
    </view>

    <!-- 答题卡弹窗 -->
    <view v-if="showAnswerCard" class="answer-card-mask" @tap="toggleAnswerCard">
      <view class="answer-card-popup" @tap.stop>
        <view class="answer-card-header">
          <text class="card-title">答题卡</text>
          <text class="card-close" @tap="toggleAnswerCard">✕</text>
        </view>
        <view class="answer-card-grid">
          <view
            v-for="(q, index) in questions"
            :key="q.id || index"
            class="card-grid-item"
            :class="{
              answered: isAnswered(q),
              current: index === currentIndex
            }"
            @tap="jumpToQuestion(index)"
          >
            <text>{{ index + 1 }}</text>
          </view>
        </view>
        <view class="answer-card-legend">
          <view class="legend-item">
            <view class="legend-dot legend-answered"></view>
            <text>已答</text>
          </view>
          <view class="legend-item">
            <view class="legend-dot legend-unanswered"></view>
            <text>未答</text>
          </view>
          <view class="legend-item">
            <view class="legend-dot legend-current"></view>
            <text>当前</text>
          </view>
        </view>
        <button class="submit-btn" @tap="confirmSubmit">提交答卷</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { exam as examApi } from '@/api/index.js'

// 考试信息
const examId = ref('')
const examTitle = ref('')
const duration = ref(0) // 考试时长（分钟）
const startTime = ref(0) // 开始时间戳

// 题目列表
const questions = ref([])

// 当前题目索引
const currentIndex = ref(0)

// 用户答案（key: questionId, value: string | array）
const userAnswers = reactive({})

// 倒计时
const remainingSeconds = ref(0)
const countdownInterval = ref(null)

// 答题卡显示状态
const showAnswerCard = ref(false)

// 提交防重复锁
const isSubmitting = ref(false)

// 判断题选项
const judgeOptions = [
  { key: 'T', value: '正确' },
  { key: 'F', value: '错误' }
]

// 当前题目
const currentQuestion = computed(() => {
  return questions.value[currentIndex.value] || {}
})

// 已答题数
const answeredCount = computed(() => {
  return questions.value.filter(q => isAnswered(q)).length
})

// 格式化倒计时
const formattedTime = computed(() => {
  const min = Math.floor(remainingSeconds.value / 60)
  const sec = remainingSeconds.value % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
})

/**
 * 页面加载
 */
onLoad((options) => {
  examId.value = options.examId
  duration.value = parseInt(options.duration) || 60
  startTime.value = Date.now()

  // 从缓存读取试卷数据
  const paperDataStr = uni.getStorageSync('examPaperData')
  if (paperDataStr) {
    try {
      const paperInfo = JSON.parse(paperDataStr)
      examTitle.value = paperInfo.examTitle || '考试答题'
      // 后端返回的 questionList 格式: { questionId, questionType, content, optionA-D, answer, score }
      // 转换为前端格式: { id, type, title, options, score }
      const rawList = paperInfo.questionList || []
      questions.value = rawList.map(q => transformQuestion(q))
      // 初始化答案对象
      questions.value.forEach(q => {
        if (q.type === 'multiple') {
          userAnswers[q.id] = []
        } else {
          userAnswers[q.id] = ''
        }
      })
      // 开始倒计时
      startCountdown()
    } catch (err) {
      console.error('解析试卷数据失败:', err)
      uni.showToast({ title: '试卷加载失败', icon: 'none' })
    }
  } else {
    uni.showToast({ title: '试卷数据丢失', icon: 'none' })
  }
})

/**
 * 将后端试题格式转换为前端格式
 * 后端: { questionId, questionType(1单选/2多选/3判断), content, optionA-D, answer, score }
 * 前端: { id, type(single/multiple/judge), title, options[{key,value}], score }
 */
const transformQuestion = (q) => {
  const typeMap = { 1: 'single', 2: 'multiple', 3: 'judge', 4: 'essay' }
  const type = typeMap[q.questionType] || 'single'
  const options = []
  if (q.optionA) options.push({ key: 'A', value: q.optionA })
  if (q.optionB) options.push({ key: 'B', value: q.optionB })
  if (q.optionC) options.push({ key: 'C', value: q.optionC })
  if (q.optionD) options.push({ key: 'D', value: q.optionD })
  return {
    id: q.questionId,
    type: type,
    title: q.content,
    options: options,
    score: q.score || 0,
    correctAnswer: q.answer,
    analysis: q.analysis
  }
}

/**
 * 启动倒计时
 */
const startCountdown = () => {
  remainingSeconds.value = duration.value * 60
  countdownInterval.value = setInterval(() => {
    remainingSeconds.value--
    if (remainingSeconds.value <= 0) {
      // 时间到，自动交卷
      clearInterval(countdownInterval.value)
      countdownInterval.value = null
      uni.showModal({
        title: '时间到',
        content: '考试时间已结束，系统将自动交卷',
        showCancel: false,
        success: () => {
          handleSubmit(true)
        }
      })
    } else if (remainingSeconds.value === 300) {
      // 剩余5分钟提醒
      uni.showToast({ title: '剩余5分钟，请抓紧时间', icon: 'none', duration: 3000 })
    }
  }, 1000)
}

// =============================================
// 答题操作
// =============================================

/**
 * 判断选项是否被选中
 */
const isSelected = (question, optionKey) => {
  const qid = question.id || question.index
  if (question.type === 'multiple') {
    return Array.isArray(userAnswers[qid]) && userAnswers[qid].includes(optionKey)
  }
  return userAnswers[qid] === optionKey
}

/**
 * 单选/判断题选择
 */
const selectSingle = (question, optionKey) => {
  const qid = question.id || question.index
  userAnswers[qid] = optionKey
}

/**
 * 多选题选择
 */
const selectMultiple = (question, optionKey) => {
  const qid = question.id || question.index
  if (!Array.isArray(userAnswers[qid])) {
    userAnswers[qid] = []
  }
  const idx = userAnswers[qid].indexOf(optionKey)
  if (idx > -1) {
    userAnswers[qid].splice(idx, 1)
  } else {
    userAnswers[qid].push(optionKey)
    // 多选题选项排序
    userAnswers[qid].sort()
  }
}

/**
 * 判断题目是否已答
 */
const isAnswered = (question) => {
  const qid = question.id || question.index
  const answer = userAnswers[qid]
  if (Array.isArray(answer)) {
    return answer.length > 0
  }
  return !!answer
}

// =============================================
// 题目导航
// =============================================

/**
 * 上一题
 */
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

/**
 * 下一题
 */
const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
  }
}

/**
 * 跳转到指定题目
 */
const jumpToQuestion = (index) => {
  currentIndex.value = index
  showAnswerCard.value = false
}

/**
 * 切换答题卡显示
 */
const toggleAnswerCard = () => {
  showAnswerCard.value = !showAnswerCard.value
}

// =============================================
// 交卷逻辑
// =============================================

/**
 * 确认交卷
 */
const isConfirming = ref(false)
const confirmSubmit = () => {
  // 防重复点击：如果已经在确认流程中，直接返回
  if (isConfirming.value) return
  isConfirming.value = true

  const unanswered = questions.value.length - answeredCount.value
  let content = '确定要提交答卷吗？'
  if (unanswered > 0) {
    content = `您还有 ${unanswered} 道题未作答，确定要提交吗？`
  }
  uni.showModal({
    title: '确认交卷',
    content: content,
    confirmText: '确认交卷',
    cancelText: '继续答题',
    confirmColor: '#2979ff',
    success: async (res) => {
      if (res.confirm) {
        // 等待 modal 关闭动画完成，避免 H5 模式下遮罩叠加导致灰屏
        await new Promise(r => setTimeout(r, 300))
        handleSubmit(false)
      }
      // 释放锁（无论确认还是取消）
      isConfirming.value = false
    },
    fail: () => {
      // modal 调用失败，释放锁
      isConfirming.value = false
    }
  })
}

/**
 * 提交考试答案
 * @param {boolean} isAuto 是否自动交卷（时间到）
 */
const handleSubmit = async (isAuto = false) => {
  // 防重复提交锁
  if (isSubmitting.value) {
    console.warn('正在提交中，请勿重复操作')
    return
  }
  isSubmitting.value = true

  // 清除倒计时
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
    countdownInterval.value = null
  }

  // 用 isSubmitting 状态代替 uni.showLoading，避免 H5 mask 残留灰屏
  let redirectUrl = ''

  try {
    // 构建答案数据: 后端 @RequestParam String answers，传 JSON 字符串
    // 后端 parseAnswers 期望 Map<String,String> 格式: {"questionId": "userAnswer", ...}
    const answers = {}
    questions.value.forEach(q => {
      const ans = Array.isArray(userAnswers[q.id]) ? userAnswers[q.id].join('') : userAnswers[q.id]
      answers[q.id] = ans || ''
    })

    const res = await examApi.submitExam(examId.value, JSON.stringify(answers))

    // 清除试卷缓存
    uni.removeStorageSync('examPaperData')

    // 跳转到结果页
    const recordId = res.recordId || res.id
    redirectUrl = `/pages/exam/exam-result?recordId=${recordId}`
  } catch (err) {
    console.error('交卷失败:', err)
    isSubmitting.value = false // 提交失败后释放锁，允许重试

    uni.showModal({
      title: '提交失败',
      content: err.message || '交卷失败，是否重试？',
      confirmText: '重试',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          handleSubmit(isAuto)
        }
      }
    })
  } finally {
    isSubmitting.value = false
    if (redirectUrl) {
      uni.redirectTo({ url: redirectUrl })
    }
  }
}

/**
 * 获取题目类型文字
 */
const getTypeText = (type) => {
  const map = {
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
    essay: '解答题'
  }
  return map[type] || '题目'
}

/**
 * 获取题目类型样式
 */
const getTypeClass = (type) => {
  const map = {
    single: 'type-single',
    multiple: 'type-multiple',
    judge: 'type-judge',
    essay: 'type-essay'
  }
  return map[type] || 'type-default'
}

// 页面卸载时清除定时器
onUnmounted(() => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
    countdownInterval.value = null
  }
})
</script>

<style lang="scss" scoped>
.exam-paper-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

/* 顶部倒计时栏 */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 20rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;

  .top-info {
    flex: 1;
    overflow: hidden;

    .exam-name {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;
    }
  }

  .countdown {
    display: flex;
    align-items: center;
    padding: 8rpx 24rpx;
    background: #e3f2fd;
    border-radius: 30rpx;

    .countdown-icon {
      font-size: 28rpx;
      margin-right: 8rpx;
    }

    .countdown-text {
      font-size: 30rpx;
      font-weight: bold;
      color: #2979ff;
    }

    &.countdown-warning {
      background: #fce4ec;

      .countdown-text {
        color: #f44336;
      }
    }
  }
}

/* 题目滚动区 */
.question-scroll {
  flex: 1;
  overflow: hidden;
}

.question-container {
  padding: 20rpx 24rpx;
}

/* 题目进度 */
.question-progress {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;

  .progress-text {
    font-size: 24rpx;
    color: #999;
    margin-right: 20rpx;
  }

  .progress-bar-mini {
    flex: 1;
    height: 8rpx;
    background: #e0e0e0;
    border-radius: 4rpx;
    overflow: hidden;

    .progress-bar-inner {
      height: 100%;
      background: #2979ff;
      border-radius: 4rpx;
      transition: width 0.3s;
    }
  }
}

/* 题目卡片 */
.question-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  .question-header {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .question-type {
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 6rpx;
      margin-right: 12rpx;

      &.type-single { background: #e3f2fd; color: #2979ff; }
      &.type-multiple { background: #fff3e0; color: #ff9800; }
      &.type-judge { background: #e8f5e9; color: #4caf50; }
      &.type-essay { background: #f3e5f5; color: #9c27b0; }
    }

    .question-score {
      font-size: 24rpx;
      color: #999;
    }
  }

  .question-title {
    font-size: 32rpx;
    color: #333;
    line-height: 1.6;
    margin-bottom: 28rpx;
    display: block;
  }
}

/* 选项列表 */
.options-list {
  .option-item {
    display: flex;
    align-items: center;
    padding: 24rpx 20rpx;
    border: 2rpx solid #f0f0f0;
    border-radius: 12rpx;
    margin-bottom: 16rpx;
    transition: all 0.2s;

    &:active {
      background: #f9f9f9;
    }

    &.selected {
      border-color: #2979ff;
      background: #f0f7ff;
    }

    .option-radio {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #ddd;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;
      flex-shrink: 0;
      transition: all 0.2s;

      &.checked {
        border-color: #2979ff;
        background: #2979ff;

        .radio-dot {
          width: 16rpx;
          height: 16rpx;
          border-radius: 50%;
          background: #fff;
        }
      }
    }

    .option-checkbox {
      width: 40rpx;
      height: 40rpx;
      border: 3rpx solid #ddd;
      border-radius: 8rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;
      flex-shrink: 0;
      transition: all 0.2s;

      &.checked {
        border-color: #2979ff;
        background: #2979ff;

        .check-mark {
          color: #fff;
          font-size: 24rpx;
          font-weight: bold;
        }
      }
    }

    .option-label {
      font-size: 28rpx;
      font-weight: bold;
      color: #666;
      margin-right: 12rpx;
    }

    .option-text {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      line-height: 1.5;
    }
  }
}

/* 解答题文本域 */
.essay-area {
  .essay-textarea {
    width: 100%;
    min-height: 300rpx;
    padding: 24rpx;
    font-size: 28rpx;
    line-height: 1.6;
    color: #333;
    background: #f9f9f9;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    box-sizing: border-box;
  }

  .essay-tip {
    margin-top: 16rpx;
    font-size: 24rpx;

    text {
      color: #9c27b0;
    }
  }
}

/* 题目导航按钮 */
.question-nav {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;

  .nav-btn {
    height: 80rpx;
    line-height: 80rpx;
    padding: 0 48rpx;
    font-size: 28rpx;
    border-radius: 40rpx;
    border: none;

    &::after {
      border: none;
    }

    &.nav-prev {
      background: #f5f5f5;
      color: #666;
    }

    &.nav-next {
      background: linear-gradient(135deg, #2979ff, #1565c0);
      color: #fff;
    }

    &.nav-submit {
      background: linear-gradient(135deg, #4caf50, #388e3c);
      color: #fff;
    }
  }
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
  color: #999;
}

/* 底部答题卡栏 */
.answer-card-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid #f0f0f0;

  .answer-card-summary {
    display: flex;

    .answered-count {
      font-size: 26rpx;
      color: #4caf50;
      margin-right: 24rpx;
    }

    .unanswered-count {
      font-size: 26rpx;
      color: #f44336;
    }
  }

  .card-toggle-text {
    font-size: 28rpx;
    color: #2979ff;
    font-weight: 500;
  }
}

/* 答题卡弹窗 */
.answer-card-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;

  .answer-card-popup {
    width: 100%;
    background: #fff;
    border-radius: 24rpx 24rpx 0 0;
    padding: 32rpx;
    padding-bottom: calc(32rpx + env(safe-area-inset-bottom));

    .answer-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 28rpx;

      .card-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }

      .card-close {
        font-size: 32rpx;
        color: #999;
        padding: 10rpx;
      }
    }

    .answer-card-grid {
      display: flex;
      flex-wrap: wrap;

      .card-grid-item {
        width: 80rpx;
        height: 80rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 2rpx solid #e0e0e0;
        border-radius: 12rpx;
        margin-right: 20rpx;
        margin-bottom: 20rpx;
        font-size: 30rpx;
        color: #999;

        &:nth-child(7n) {
          margin-right: 0;
        }

        &.answered {
          background: #2979ff;
          border-color: #2979ff;
          color: #fff;
        }

        &.current {
          border-color: #2979ff;
          border-width: 4rpx;
          color: #2979ff;
          font-weight: bold;

          &.answered {
            color: #fff;
          }
        }
      }
    }

    .answer-card-legend {
      display: flex;
      justify-content: center;
      padding: 20rpx 0;

      .legend-item {
        display: flex;
        align-items: center;
        margin: 0 24rpx;

        .legend-dot {
          width: 24rpx;
          height: 24rpx;
          border-radius: 6rpx;
          margin-right: 8rpx;

          &.legend-answered { background: #2979ff; }
          &.legend-unanswered { background: #fff; border: 2rpx solid #e0e0e0; }
          &.legend-current { background: #fff; border: 3rpx solid #2979ff; }
        }

        text {
          font-size: 24rpx;
          color: #666;
        }
      }
    }

    .submit-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      background: linear-gradient(135deg, #4caf50, #388e3c);
      color: #fff;
      font-size: 32rpx;
      border-radius: 44rpx;
      border: none;
      margin-top: 20rpx;

      &::after {
        border: none;
      }
    }
  }
}
</style>
