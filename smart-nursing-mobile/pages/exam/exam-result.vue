<template>
  <view class="exam-result-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="result" class="result-content">
      <!-- 成绩展示区 -->
      <view class="score-section" :class="{ passed: isPassed, failed: !isPassed }">
        <view class="score-circle">
          <text class="score-value">{{ result.score || 0 }}</text>
          <text class="score-unit">分</text>
        </view>
        <text class="score-status">{{ isPassed ? '恭喜通过' : '未通过' }}</text>
        <view class="score-info">
          <view class="info-item">
            <text class="info-label">总分</text>
            <text class="info-value">{{ result.totalScore || 100 }}</text>
          </view>
          <view class="info-divider"></view>
          <view class="info-item">
            <text class="info-label">及格分</text>
            <text class="info-value">{{ result.passScore || 60 }}</text>
          </view>
          <view class="info-divider"></view>
          <view class="info-item">
            <text class="info-label">用时</text>
            <text class="info-value">{{ formatDuration(result.duration) }}</text>
          </view>
        </view>
      </view>

      <!-- 答题统计 -->
      <view class="stats-section">
        <view class="stats-card">
          <view class="stats-row">
            <view class="stats-item stats-correct">
              <text class="stats-num">{{ correctCount }}</text>
              <text class="stats-label">答对</text>
            </view>
            <view class="stats-item stats-wrong">
              <text class="stats-num">{{ wrongCount }}</text>
              <text class="stats-label">答错</text>
            </view>
            <view class="stats-item stats-unanswered">
              <text class="stats-num">{{ unansweredCount }}</text>
              <text class="stats-label">未答</text>
            </view>
            <view class="stats-item stats-total">
              <text class="stats-num">{{ totalCount }}</text>
              <text class="stats-label">总题数</text>
            </view>
          </view>
          <view class="accuracy-bar">
            <view class="accuracy-label">
              <text class="label-text">正确率</text>
              <text class="label-value">{{ accuracy }}%</text>
            </view>
            <view class="accuracy-progress">
              <view class="accuracy-inner" :style="{ width: accuracy + '%' }"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 考试信息 -->
      <view class="exam-info-section">
        <view class="info-card">
          <view class="info-row">
            <text class="info-key">考试名称</text>
            <text class="info-val">{{ result.examTitle || result.title || '考试' }}</text>
          </view>
          <view class="info-row">
            <text class="info-key">提交时间</text>
            <text class="info-val">{{ formatDateTime(result.submitTime || result.endTime) }}</text>
          </view>
          <view v-if="result.rank" class="info-row">
            <text class="info-key">排名</text>
            <text class="info-val">第 {{ result.rank }} 名</text>
          </view>
        </view>
      </view>

      <!-- 答题详情 -->
      <view class="answer-detail-section">
        <view class="section-header">
          <text class="section-title">答题详情</text>
          <view class="filter-tabs">
            <text
              class="filter-tab"
              :class="{ active: filterType === 'all' }"
              @tap="filterType = 'all'"
            >全部</text>
            <text
              class="filter-tab"
              :class="{ active: filterType === 'wrong' }"
              @tap="filterType = 'wrong'"
            >仅看错题</text>
          </view>
        </view>

        <view class="answer-list">
          <view
            v-for="(item, index) in filteredAnswers"
            :key="item.questionId || index"
            class="answer-item"
          >
            <view class="answer-item-header">
              <text class="answer-index">{{ item.displayIndex }}.</text>
              <text class="answer-type" :class="getTypeClass(item.type)">{{ getTypeText(item.type) }}</text>
              <view class="answer-result" :class="{ correct: item.isCorrect, wrong: !item.isCorrect }">
                <text>{{ item.isCorrect ? '✓ 正确' : '✗ 错误' }}</text>
              </view>
              <text class="answer-score" :class="{ 'score-got': item.isCorrect, 'score-lost': !item.isCorrect }">
                {{ item.isCorrect ? '+' + item.score : '0' }}分
              </text>
            </view>

            <text class="answer-question">{{ item.title }}</text>

            <!-- 选项列表 -->
            <view v-if="item.options && item.options.length" class="answer-options">
              <view
                v-for="option in item.options"
                :key="option.key"
                class="answer-option"
                :class="{
                  'option-correct': isCorrectOption(item, option.key),
                  'option-wrong': isWrongOption(item, option.key),
                  'option-selected': isUserSelected(item, option.key)
                }"
              >
                <text class="option-label">{{ option.key }}</text>
                <text class="option-text">{{ option.value }}</text>
                <text v-if="isCorrectOption(item, option.key)" class="option-tag tag-correct">正确答案</text>
                <text v-if="isWrongOption(item, option.key)" class="option-tag tag-wrong">你的选择</text>
              </view>
            </view>

            <!-- 判断题展示 -->
            <view v-else-if="item.type === 'judge'" class="judge-answer">
              <view class="judge-row">
                <text class="judge-label">正确答案：</text>
                <text class="judge-value judge-correct">{{ item.correctAnswer === 'T' ? '正确' : '错误' }}</text>
              </view>
              <view class="judge-row">
                <text class="judge-label">你的答案：</text>
                <text class="judge-value" :class="item.isCorrect ? 'judge-correct' : 'judge-wrong'">
                  {{ item.userAnswer === 'T' ? '正确' : item.userAnswer === 'F' ? '错误' : '未作答' }}
                </text>
              </view>
            </view>

            <!-- 解析 -->
            <view v-if="item.analysis || item.explanation" class="answer-analysis">
              <view class="analysis-header">
                <text class="analysis-icon">💡</text>
                <text class="analysis-title">解析</text>
              </view>
              <text class="analysis-content">{{ item.analysis || item.explanation }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-text">结果不存在</text>
    </view>

    <!-- 底部操作 -->
    <view v-if="result" class="bottom-bar">
      <button class="action-btn btn-back" @tap="goBack">返回考试列表</button>
      <button class="action-btn btn-retry" @tap="goExamDetail">查看考试</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { exam as examApi } from '@/api/index.js'

// 结果数据
const result = ref(null)
const loading = ref(true)
const filterType = ref('all') // all | wrong

// 计算属性
const isPassed = computed(() => {
  if (!result.value) return false
  return (result.value.score || 0) >= (result.value.passScore || 60)
})

const answers = computed(() => {
  if (!result.value) return []
  return result.value.answers || result.value.details || []
})

const filteredAnswers = computed(() => {
  let list = answers.value
  if (filterType.value === 'wrong') {
    list = list.filter(a => !a.isCorrect)
  }
  // 添加显示索引
  return list.map((item, idx) => ({
    ...item,
    displayIndex: filterType.value === 'wrong' ? idx + 1 : (answers.value.indexOf(item) + 1)
  }))
})

const totalCount = computed(() => answers.value.length)
const correctCount = computed(() => answers.value.filter(a => a.isCorrect).length)
const wrongCount = computed(() => answers.value.filter(a => !a.isCorrect && a.userAnswer).length)
const unansweredCount = computed(() => answers.value.filter(a => !a.userAnswer || (Array.isArray(a.userAnswer) && a.userAnswer.length === 0)).length)
const accuracy = computed(() => {
  if (!totalCount.value) return 0
  return Math.round((correctCount.value / totalCount.value) * 100)
})

/**
 * 页面加载
 */
onLoad((options) => {
  const recordId = options.recordId
  if (recordId) {
    loadResult(recordId)
  }
})

/**
 * 加载考试结果
 */
const loadResult = async (recordId) => {
  loading.value = true
  try {
    const res = await examApi.getExamResult(recordId)
    result.value = res
  } catch (err) {
    console.error('加载考试结果失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 判断是否为正确答案选项
 */
const isCorrectOption = (item, optionKey) => {
  const correct = item.correctAnswer
  if (Array.isArray(correct)) {
    return correct.includes(optionKey)
  }
  return correct === optionKey
}

/**
 * 判断是否为用户错选选项
 */
const isWrongOption = (item, optionKey) => {
  if (item.isCorrect) return false
  const userAns = item.userAnswer
  if (Array.isArray(userAns)) {
    return userAns.includes(optionKey) && !isCorrectOption(item, optionKey)
  }
  return userAns === optionKey && !isCorrectOption(item, optionKey)
}

/**
 * 判断是否为用户选择的选项
 */
const isUserSelected = (item, optionKey) => {
  const userAns = item.userAnswer
  if (Array.isArray(userAns)) {
    return userAns.includes(optionKey)
  }
  return userAns === optionKey
}

/**
 * 格式化用时
 */
const formatDuration = (seconds) => {
  if (!seconds) return '未知'
  const min = Math.floor(seconds / 60)
  return `${min}分钟`
}

/**
 * 格式化日期时间
 */
const formatDateTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

/**
 * 获取题目类型文字
 */
const getTypeText = (type) => {
  const map = { single: '单选', multiple: '多选', judge: '判断' }
  return map[type] || '题目'
}

/**
 * 获取题目类型样式
 */
const getTypeClass = (type) => {
  const map = {
    single: 'type-single',
    multiple: 'type-multiple',
    judge: 'type-judge'
  }
  return map[type] || 'type-default'
}

/**
 * 返回考试列表
 */
const goBack = () => {
  uni.switchTab({ url: '/pages/exam/index' })
}

/**
 * 查看考试详情
 */
const goExamDetail = () => {
  if (result.value && result.value.examId) {
    uni.navigateTo({ url: `/pages/exam/exam-detail?id=${result.value.examId}` })
  } else {
    goBack()
  }
}
</script>

<style lang="scss" scoped>
.exam-result-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 140rpx;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
  color: #999;
}

/* 成绩展示区 */
.score-section {
  padding: 60rpx 32rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;

  &.passed {
    background: linear-gradient(180deg, #4caf50, #66bb6a);
  }

  &.failed {
    background: linear-gradient(180deg, #f44336, #ef5350);
  }

  .score-circle {
    display: flex;
    align-items: baseline;
    justify-content: center;
    width: 240rpx;
    height: 240rpx;
    border-radius: 50%;
    border: 8rpx solid rgba(255, 255, 255, 0.4);
    background: rgba(255, 255, 255, 0.15);
    margin-bottom: 20rpx;

    .score-value {
      font-size: 80rpx;
      font-weight: bold;
      color: #fff;
    }

    .score-unit {
      font-size: 28rpx;
      color: rgba(255, 255, 255, 0.85);
      margin-left: 8rpx;
    }
  }

  .score-status {
    font-size: 32rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 30rpx;
  }

  .score-info {
    display: flex;
    align-items: center;

    .info-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 0 30rpx;

      .info-label {
        font-size: 22rpx;
        color: rgba(255, 255, 255, 0.75);
      }

      .info-value {
        font-size: 30rpx;
        color: #fff;
        font-weight: 600;
        margin-top: 6rpx;
      }
    }

    .info-divider {
      width: 1rpx;
      height: 48rpx;
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

/* 答题统计 */
.stats-section {
  padding: 0 24rpx;
  margin-top: -30rpx;
  position: relative;
  z-index: 2;

  .stats-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 28rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);

    .stats-row {
      display: flex;
      justify-content: space-around;

      .stats-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .stats-num {
          font-size: 40rpx;
          font-weight: bold;
        }

        .stats-label {
          font-size: 22rpx;
          color: #999;
          margin-top: 6rpx;
        }

        &.stats-correct .stats-num { color: #4caf50; }
        &.stats-wrong .stats-num { color: #f44336; }
        &.stats-unanswered .stats-num { color: #999; }
        &.stats-total .stats-num { color: #2979ff; }
      }
    }

    .accuracy-bar {
      margin-top: 24rpx;

      .accuracy-label {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10rpx;

        .label-text {
          font-size: 24rpx;
          color: #666;
        }

        .label-value {
          font-size: 24rpx;
          color: #2979ff;
          font-weight: bold;
        }
      }

      .accuracy-progress {
        width: 100%;
        height: 12rpx;
        background: #f0f0f0;
        border-radius: 6rpx;
        overflow: hidden;

        .accuracy-inner {
          height: 100%;
          background: linear-gradient(90deg, #4caf50, #66bb6a);
          border-radius: 6rpx;
          transition: width 0.5s;
        }
      }
    }
  }
}

/* 考试信息 */
.exam-info-section {
  padding: 20rpx 24rpx 0;

  .info-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 28rpx 32rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 12rpx 0;

      .info-key {
        font-size: 26rpx;
        color: #999;
      }

      .info-val {
        font-size: 26rpx;
        color: #333;
      }
    }
  }
}

/* 答题详情 */
.answer-detail-section {
  padding: 20rpx 24rpx 0;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }

    .filter-tabs {
      display: flex;

      .filter-tab {
        font-size: 24rpx;
        padding: 8rpx 20rpx;
        border-radius: 20rpx;
        background: #fff;
        color: #999;
        margin-left: 12rpx;

        &.active {
          background: #2979ff;
          color: #fff;
        }
      }
    }
  }

  .answer-list {
    .answer-item {
      background: #fff;
      border-radius: 16rpx;
      padding: 28rpx;
      margin-bottom: 20rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

      .answer-item-header {
        display: flex;
        align-items: center;
        margin-bottom: 16rpx;

        .answer-index {
          font-size: 28rpx;
          font-weight: bold;
          color: #333;
          margin-right: 12rpx;
        }

        .answer-type {
          font-size: 20rpx;
          padding: 4rpx 12rpx;
          border-radius: 6rpx;
          margin-right: 12rpx;

          &.type-single { background: #e3f2fd; color: #2979ff; }
          &.type-multiple { background: #fff3e0; color: #ff9800; }
          &.type-judge { background: #e8f5e9; color: #4caf50; }
        }

        .answer-result {
          font-size: 22rpx;
          padding: 4rpx 12rpx;
          border-radius: 6rpx;

          &.correct { background: #e8f5e9; color: #4caf50; }
          &.wrong { background: #ffebee; color: #f44336; }
        }

        .answer-score {
          font-size: 24rpx;
          margin-left: auto;
          font-weight: 600;

          &.score-got { color: #4caf50; }
          &.score-lost { color: #f44336; }
        }
      }

      .answer-question {
        font-size: 30rpx;
        color: #333;
        line-height: 1.6;
        display: block;
        margin-bottom: 20rpx;
      }

      .answer-options {
        .answer-option {
          display: flex;
          align-items: center;
          padding: 16rpx 20rpx;
          border-radius: 10rpx;
          margin-bottom: 12rpx;
          background: #f9f9f9;

          &.option-correct {
            background: #e8f5e9;
            border: 2rpx solid #4caf50;
          }

          &.option-wrong {
            background: #ffebee;
            border: 2rpx solid #f44336;
          }

          .option-label {
            font-size: 26rpx;
            font-weight: bold;
            color: #666;
            margin-right: 12rpx;
          }

          .option-text {
            flex: 1;
            font-size: 26rpx;
            color: #333;
          }

          .option-tag {
            font-size: 20rpx;
            padding: 2rpx 10rpx;
            border-radius: 4rpx;

            &.tag-correct { background: #4caf50; color: #fff; }
            &.tag-wrong { background: #f44336; color: #fff; }
          }
        }
      }

      .judge-answer {
        background: #f9f9f9;
        border-radius: 10rpx;
        padding: 16rpx 20rpx;

        .judge-row {
          display: flex;
          align-items: center;
          padding: 6rpx 0;

          .judge-label {
            font-size: 26rpx;
            color: #666;
            margin-right: 12rpx;
          }

          .judge-value {
            font-size: 26rpx;
            font-weight: 600;

            &.judge-correct { color: #4caf50; }
            &.judge-wrong { color: #f44336; }
          }
        }
      }

      .answer-analysis {
        margin-top: 20rpx;
        padding: 20rpx;
        background: #fffde7;
        border-radius: 10rpx;
        border-left: 6rpx solid #ff9800;

        .analysis-header {
          display: flex;
          align-items: center;
          margin-bottom: 10rpx;

          .analysis-icon {
            font-size: 28rpx;
            margin-right: 8rpx;
          }

          .analysis-title {
            font-size: 26rpx;
            font-weight: 600;
            color: #ff9800;
          }
        }

        .analysis-content {
          font-size: 26rpx;
          color: #666;
          line-height: 1.7;
        }
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

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
  z-index: 100;
  gap: 20rpx;

  .action-btn {
    flex: 1;
    height: 88rpx;
    line-height: 88rpx;
    font-size: 30rpx;
    border-radius: 44rpx;
    border: none;

    &::after {
      border: none;
    }

    &.btn-back {
      background: #f5f5f5;
      color: #666;
    }

    &.btn-retry {
      background: linear-gradient(135deg, #2979ff, #1565c0);
      color: #fff;
    }
  }
}
</style>
