<template>
  <view class="exam-page">
    <!-- 状态分类 Tab -->
    <view class="status-tabs">
      <view
        v-for="item in statusTabs"
        :key="item.value"
        class="status-tab"
        :class="{ active: currentStatus === item.value }"
        @tap="switchStatus(item.value)"
      >
        <text class="tab-text">{{ item.label }}</text>
        <text v-if="item.count !== undefined" class="tab-count">{{ item.count }}</text>
      </view>
    </view>

    <!-- 考试列表 -->
    <scroll-view
      class="exam-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onPullDownRefresh"
    >
      <view v-if="filteredExamList.length > 0" class="exam-list">
        <view
          v-for="item in filteredExamList"
          :key="item.examId"
          class="exam-card"
          @tap="goExamDetail(item)"
        >
          <view class="exam-card-header">
            <view class="exam-icon-wrapper">
              <text class="exam-icon">📝</text>
            </view>
            <view class="exam-info">
              <text class="exam-title text-ellipsis">{{ item.examName }}</text>
              <view class="exam-tags">
                <text class="exam-tag" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</text>
              </view>
            </view>
          </view>

          <view class="exam-card-body">
            <view class="exam-stat">
              <text class="stat-value">{{ item.duration || 0 }}</text>
              <text class="stat-label">分钟</text>
            </view>
            <view class="exam-divider"></view>
            <view class="exam-stat">
              <text class="stat-value">{{ item.totalScore || 0 }}</text>
              <text class="stat-label">总分</text>
            </view>
            <view class="exam-divider"></view>
            <view class="exam-stat">
              <text class="stat-value">{{ item.passScore || 0 }}</text>
              <text class="stat-label">及格分</text>
            </view>
          </view>

          <view class="exam-card-footer">
            <view class="exam-time">
              <text v-if="item.endTime" class="time-text">
                截止: {{ formatExamTime(item.endTime) }}
              </text>
              <text v-else class="time-text">长期有效</text>
            </view>
            <view class="exam-action">
              <text v-if="item.status === 1" class="action-btn btn-start">开始考试</text>
              <text v-else-if="item.status === 0" class="action-btn btn-disabled">未开始</text>
              <text v-else class="action-btn btn-result">已结束</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon-text">📋</text>
        <text class="empty-text">暂无考试</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-more">
        <text class="loading-text">加载中...</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { exam } from '@/api/index.js'

// 状态分类 - 数字值与后端 status 对齐: 0=未开始 1=可考试 2=已结束
const statusTabs = ref([
  { label: '全部', value: '', count: undefined },
  { label: '未开始', value: 0, count: undefined },
  { label: '可考试', value: 1, count: undefined },
  { label: '已结束', value: 2, count: undefined }
])

// 状态
const currentStatus = ref('')
const examList = ref([])
const loading = ref(false)
const isRefreshing = ref(false)

/**
 * 按当前状态过滤后的考试列表
 */
const filteredExamList = computed(() => {
  if (currentStatus.value === '') return examList.value
  return examList.value.filter(item => item.status === currentStatus.value)
})

/**
 * 加载考试列表
 */
const loadList = async () => {
  loading.value = true
  try {
    const res = await exam.getExamList()
    examList.value = Array.isArray(res) ? res : (res.list || res.records || [])
    updateCounts()
  } catch (err) {
    console.error('加载考试列表失败:', err)
    examList.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 更新各状态数量
 */
const updateCounts = () => {
  statusTabs.value.forEach(tab => {
    if (tab.value === '') {
      tab.count = examList.value.length
    } else {
      tab.count = examList.value.filter(e => e.status === tab.value).length
    }
  })
}

/**
 * 切换状态（前端过滤，无需重新请求）
 */
const switchStatus = (value) => {
  currentStatus.value = value
}

/**
 * 下拉刷新
 */
const onPullDownRefresh = async () => {
  isRefreshing.value = true
  await loadList()
  isRefreshing.value = false
  uni.stopPullDownRefresh()
}

/**
 * 跳转考试详情
 */
const goExamDetail = (item) => {
  uni.navigateTo({
    url: `/pages/exam/exam-detail?id=${item.examId}`
  })
}

/**
 * 获取状态文字
 * 后端 status: 0-未开始 1-可考试 2-已结束
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
 * 格式化考试时间
 */
const formatExamTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 页面显示时加载数据
onShow(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.exam-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #F8FAFC;
}

/* 状态分类 Tab */
.status-tabs {
  display: flex;
  background: #FFFFFF;
  padding: 0 12rpx;
  border-bottom: 1rpx solid #E8E4DE;

  .status-tab {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24rpx 0;
    position: relative;

    .tab-text {
      font-size: 26rpx;
      color: #636A70;
      transition: color 0.4s ease-out;
    }

    .tab-count {
      font-size: 20rpx;
      color: #989FA6;
      margin-top: 6rpx;
    }

    &.active {
      .tab-text {
        color: #0EA5E9;
        font-weight: bold;
      }

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        width: 60rpx;
        height: 6rpx;
        background: #0EA5E9;
        border-radius: 3rpx;
      }
    }
  }
}

/* 考试列表 */
.exam-scroll {
  flex: 1;
  overflow: hidden;
}

.exam-list {
  padding: 20rpx 32rpx;
}

/* 考试卡片 */
.exam-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  margin-bottom: 20rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 16rpx rgba(58, 76, 86, 0.06);
  transition: all 0.5s ease-out;

  &:active {
    opacity: 0.9;
    transform: translateY(1rpx);
  }

  .exam-card-header {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24rpx;

    .exam-icon-wrapper {
      width: 80rpx;
      height: 80rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;
      flex-shrink: 0;
      background: #D1E4F5;

      .exam-icon {
        font-size: 36rpx;
      }
    }

    .exam-info {
      flex: 1;
      overflow: hidden;

      .exam-title {
        font-size: 32rpx;
        font-weight: 600;
        color: #3A4C56;
        line-height: 1.4;
      }

      .exam-tags {
        display: flex;
        margin-top: 12rpx;

        .exam-tag {
          font-size: 20rpx;
          padding: 4rpx 14rpx;
          border-radius: 8rpx;
          margin-right: 12rpx;

          &.status-available { background: #E0F2FE; color: #0EA5E9; }
          &.status-not-started { background: #F0EDE7; color: #989FA6; }
          &.status-expired { background: #FEE2E2; color: #EF4444; }
          &.status-default { background: #F5F5F5; color: #999; }
        }
      }
    }
  }

  .exam-card-body {
    display: flex;
    align-items: center;
    justify-content: space-around;
    background: #F0F9FF;
    border-radius: 16rpx;
    padding: 28rpx 0;
    margin-bottom: 20rpx;

    .exam-stat {
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
        color: #989FA6;
        margin-top: 6rpx;
      }
    }

    .exam-divider {
      width: 1rpx;
      height: 48rpx;
      background: #E8E4DE;
    }
  }

  .exam-card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .exam-time {
      .time-text {
        font-size: 24rpx;
        color: #94A3B8;
      }
    }

    .exam-action {
      .action-btn {
        font-size: 26rpx;
        padding: 14rpx 36rpx;
        border-radius: 32rpx;
        font-weight: 500;
        transition: all 0.4s ease-out;

        &.btn-start {
          background: linear-gradient(135deg, #0EA5E9, #0284C7);
          color: #fff;
          box-shadow: 0 4rpx 12rpx rgba(14, 165, 233, 0.25);
        }

        &.btn-result {
          background: #F0EDE7;
          color: #636A70;
        }

        &.btn-disabled {
          background: #F0EDE7;
          color: #D1C5BE;
        }
      }
    }
  }
}

/* 加载与空状态 */
.loading-more {
  text-align: center;
  padding: 30rpx 0;

  .loading-text {
    font-size: 24rpx;
    color: #989FA6;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;

  .empty-icon-text {
    font-size: 100rpx;
    margin-bottom: 20rpx;
    opacity: 0.35;
  }

  .empty-text {
    font-size: 28rpx;
    color: #989FA6;
  }
}
</style>
