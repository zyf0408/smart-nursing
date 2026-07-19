<template>
  <view class="exam-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="exam" class="exam-content">
      <!-- 考试信息卡片 -->
      <view class="exam-card">
        <view class="exam-header">
          <text class="exam-title">{{ exam.examName }}</text>
          <view class="exam-tags">
            <text class="exam-tag" :class="getStatusClass(exam.status)">{{ getStatusText(exam.status) }}</text>
          </view>
        </view>

        <!-- 考试关键信息 -->
        <view class="exam-stats">
          <view class="stat-item">
            <text class="stat-value">{{ exam.duration || 0 }}</text>
            <text class="stat-label">考试时长(分)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ exam.totalScore || 0 }}</text>
            <text class="stat-label">总分</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ exam.passScore || 0 }}</text>
            <text class="stat-label">及格分</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ questionCount }}</text>
            <text class="stat-label">题目数</text>
          </view>
        </view>

        <!-- 时间信息 -->
        <view class="exam-time-info">
          <view class="time-row">
            <text class="time-label">开始时间</text>
            <text class="time-value">{{ formatDateTime(exam.startTime) }}</text>
          </view>
          <view class="time-row">
            <text class="time-label">截止时间</text>
            <text class="time-value">{{ formatDateTime(exam.endTime) }}</text>
          </view>
        </view>
      </view>

      <!-- 倒计时（如果考试进行中） -->
      <view v-if="exam.status === 1 && countdownText" class="countdown-section">
        <view class="countdown-wrapper">
          <text class="countdown-label">距考试截止还剩</text>
          <text class="countdown-time">{{ countdownText }}</text>
        </view>
      </view>

      <!-- 考试说明 -->
      <view v-if="exam.description" class="exam-instructions">
        <text class="instructions-title">考试说明</text>
        <text class="instructions-content">{{ exam.description }}</text>
      </view>

      <!-- 考试须知 -->
      <view class="exam-notice">
        <text class="notice-title">考试须知</text>
        <view class="notice-list">
          <view class="notice-item">
            <text class="notice-dot">1.</text>
            <text class="notice-text">考试时长为 {{ exam.duration || 0 }} 分钟，请在规定时间内完成答题</text>
          </view>
          <view class="notice-item">
            <text class="notice-dot">2.</text>
            <text class="notice-text">总分 {{ exam.totalScore || 0 }} 分，及格分 {{ exam.passScore || 0 }} 分</text>
          </view>
          <view class="notice-item">
            <text class="notice-dot">3.</text>
            <text class="notice-text">共 {{ questionCount }} 道题，包含单选、多选、判断题</text>
          </view>
          <view class="notice-item">
            <text class="notice-dot">4.</text>
            <text class="notice-text">答题过程中请勿退出应用，否则可能影响考试结果</text>
          </view>
          <view class="notice-item">
            <text class="notice-dot">5.</text>
            <text class="notice-text">考试时间结束后将自动交卷</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-text">考试不存在或已删除</text>
    </view>

    <!-- 底部操作按钮 -->
    <view v-if="exam" class="bottom-bar">
      <button
        v-if="exam.status === 1"
        class="start-btn"
        @tap="handleStartExam"
      >
        开始考试
      </button>
      <button
        v-else
        class="start-btn"
        disabled
      >
        {{ getStatusText(exam.status) }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { exam as examApi } from '@/api/index.js'

// 考试数据
const exam = ref(null)
const loading = ref(true)
let examId = null

// 题目数量
const questionCount = computed(() => {
  if (!exam.value || !exam.value.questionList) return 0
  return exam.value.questionList.length
})

// 倒计时
const countdownText = ref('')
const countdownInterval = ref(null)
const endTime = ref(0)

/**
 * 页面加载
 */
onLoad((options) => {
  examId = options.id
  if (examId) {
    loadExamDetail(examId)
  }
})

/**
 * 加载考试详情
 */
const loadExamDetail = async (id) => {
  loading.value = true
  try {
    const res = await examApi.getExamDetail(id)
    exam.value = res

    // 如果考试进行中，启动倒计时
    if (res.status === 1 && res.endTime) {
      endTime.value = new Date(res.endTime).getTime()
      startCountdown()
    }
  } catch (err) {
    console.error('加载考试详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 启动倒计时
 */
const startCountdown = () => {
  updateCountdown()
  countdownInterval.value = setInterval(updateCountdown, 1000)
}

/**
 * 更新倒计时
 */
const updateCountdown = () => {
  const now = Date.now()
  const diff = endTime.value - now

  if (diff <= 0) {
    countdownText.value = '已截止'
    if (countdownInterval.value) {
      clearInterval(countdownInterval.value)
      countdownInterval.value = null
    }
    return
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)

  if (days > 0) {
    countdownText.value = `${days}天 ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  } else {
    countdownText.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }
}

/**
 * 开始考试
 */
const handleStartExam = () => {
  uni.showModal({
    title: '确认开始考试',
    content: `考试时长 ${exam.value.duration} 分钟，确认开始吗？`,
    confirmText: '开始',
    confirmColor: '#0EA5E9',
    success: async (res) => {
      if (res.confirm) {
        // 用页面内 loading 代替 uni.showLoading，避免 H5 mask 残留灰屏
        loading.value = true
        let needNavigate = false
        try {
          // 调用开始考试接口（创建考试记录）
          await examApi.startExam(examId)

          // 将试题数据存入缓存
          const paperInfo = JSON.stringify({
            examId: examId,
            examTitle: exam.value.examName,
            duration: exam.value.duration,
            startTime: Date.now(),
            questionList: exam.value.questionList || []
          })
          uni.setStorageSync('examPaperData', paperInfo)
          needNavigate = true
        } catch (err) {
          console.error('开始考试失败:', err)
          uni.showToast({ title: err.message || '开始考试失败', icon: 'none' })
        } finally {
          loading.value = false
          if (needNavigate) {
            uni.navigateTo({
              url: `/pages/exam/exam-paper?examId=${examId}&duration=${exam.value.duration}`
            })
          }
        }
      }
    }
  })
}

/**
 * 获取状态文字
 * 后端 status: 0-未开始 1-进行中 2-已结束
 */
const getStatusText = (status) => {
  const map = {
    0: '未开始',
    1: '可考试',
    2: '已结束'
  }
  return map[status] || '未知'
}

/**
 * 获取状态样式类
 */
const getStatusClass = (status) => {
  const map = {
    0: 'status-not-started',
    1: 'status-available',
    2: 'status-expired'
  }
  return map[status] || 'status-default'
}

/**
 * 格式化日期时间
 */
const formatDateTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
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
.exam-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx 24rpx;
  padding-bottom: 160rpx;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
  color: #999;
}

/* 考试信息卡片 */
.exam-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  .exam-header {
    margin-bottom: 28rpx;

    .exam-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #222;
      line-height: 1.4;
      display: block;
    }

    .exam-tags {
      display: flex;
      margin-top: 16rpx;

      .exam-tag {
        font-size: 22rpx;
        padding: 6rpx 16rpx;
        border-radius: 6rpx;
        margin-right: 12rpx;

        &.status-available { background: #e8f5e9; color: #4caf50; }
        &.status-expired { background: #fafafa; color: #999; }
        &.status-not-started { background: #e0e0e0; color: #757575; }
        &.status-default { background: #f5f5f5; color: #666; }
      }
    }
  }

  .exam-stats {
    display: flex;
    align-items: center;
    justify-content: space-around;
    background: #f9f9f9;
    border-radius: 12rpx;
    padding: 24rpx 0;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .stat-value {
        font-size: 36rpx;
        font-weight: bold;
        color: #0EA5E9;
      }

      .stat-label {
        font-size: 22rpx;
        color: #999;
        margin-top: 6rpx;
      }
    }

    .stat-divider {
      width: 1rpx;
      height: 48rpx;
      background: #e0e0e0;
    }
  }

  .exam-time-info {
    margin-top: 24rpx;

    .time-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12rpx 0;

      .time-label {
        font-size: 26rpx;
        color: #999;
      }

      .time-value {
        font-size: 26rpx;
        color: #333;
      }
    }
  }
}

/* 倒计时 */
.countdown-section {
  margin-top: 20rpx;

  .countdown-wrapper {
    background: linear-gradient(135deg, #ff9800, #f57c00);
    border-radius: 16rpx;
    padding: 28rpx 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;

    .countdown-label {
      font-size: 26rpx;
      color: rgba(255, 255, 255, 0.9);
      margin-right: 16rpx;
    }

    .countdown-time {
      font-size: 36rpx;
      font-weight: bold;
      color: #fff;
      letter-spacing: 2rpx;
    }
  }
}

/* 考试说明 */
.exam-instructions {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;
  margin-top: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  .instructions-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333;
    display: block;
    margin-bottom: 16rpx;
  }

  .instructions-content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.7;
  }
}

/* 考试须知 */
.exam-notice {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 32rpx;
  margin-top: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  .notice-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333;
    display: block;
    margin-bottom: 20rpx;
  }

  .notice-list {
    .notice-item {
      display: flex;
      padding: 8rpx 0;

      .notice-dot {
        font-size: 26rpx;
        color: #0EA5E9;
        margin-right: 12rpx;
        flex-shrink: 0;
      }

      .notice-text {
        font-size: 26rpx;
        color: #666;
        line-height: 1.6;
        flex: 1;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx 0;

  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

/* 底部操作按钮 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
  z-index: 100;

  .start-btn {
    width: 100%;
    height: 92rpx;
    line-height: 92rpx;
    background: linear-gradient(135deg, #0EA5E9, #0284C7);
    color: #fff;
    font-size: 34rpx;
    border-radius: 46rpx;
    border: none;
    letter-spacing: 4rpx;

    &::after {
      border: none;
    }

    &[disabled] {
      background: #e0e0e0;
      color: #999;
    }
  }
}
</style>
