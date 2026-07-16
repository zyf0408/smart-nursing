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
      <view v-if="examList.length > 0" class="exam-list">
        <view
          v-for="item in examList"
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
                <text class="exam-tag tag-normal">已发布</text>
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
              <text class="action-btn btn-start">开始考试</text>
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
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { exam } from '@/api/index.js'

// 状态分类
const statusTabs = ref([
  { label: '全部', value: '', count: undefined },
  { label: '可考试', value: 'available', count: undefined },
  { label: '进行中', value: 'ongoing', count: undefined },
  { label: '已完成', value: 'completed', count: undefined },
  { label: '已截止', value: 'expired', count: undefined }
])

// 状态
const currentStatus = ref('')
const examList = ref([])
const loading = ref(false)
const isRefreshing = ref(false)

/**
 * 加载考试列表
 */
const loadList = async () => {
  loading.value = true
  try {
    const res = await exam.getExamList()
    examList.value = Array.isArray(res) ? res : (res.list || res.records || [])
  } catch (err) {
    console.error('加载考试列表失败:', err)
    examList.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 更新状态数量
 */
const updateStatusCounts = (res) => {
  const counts = res.counts || {}
  statusTabs.value.forEach(tab => {
    if (tab.value && counts[tab.value] !== undefined) {
      tab.count = counts[tab.value]
    } else if (!tab.value) {
      tab.count = res.total || examList.value.length
    }
  })
}

/**
 * 切换状态
 */
const switchStatus = (value) => {
  if (currentStatus.value === value) return
  currentStatus.value = value
  loadList()
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
 */
const getStatusText = (status) => {
  const map = {
    available: '可考试',
    ongoing: '进行中',
    completed: '已完成',
    expired: '已截止',
    not_started: '未开始'
  }
  return map[status] || '未知'
}

/**
 * 获取状态样式类
 */
const getStatusClass = (status) => {
  const map = {
    available: 'status-available',
    ongoing: 'status-ongoing',
    completed: 'status-completed',
    expired: 'status-expired',
    not_started: 'status-not-started'
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
  background: #f5f5f5;
}

/* 状态分类 Tab */
.status-tabs {
  display: flex;
  background: #fff;
  padding: 0 12rpx;
  border-bottom: 1rpx solid #f0f0f0;

  .status-tab {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24rpx 0;
    position: relative;

    .tab-text {
      font-size: 26rpx;
      color: #666;
    }

    .tab-count {
      font-size: 20rpx;
      color: #999;
      margin-top: 6rpx;
    }

    &.active {
      .tab-text {
        color: #2979ff;
        font-weight: bold;
      }

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        width: 60rpx;
        height: 6rpx;
        background: #2979ff;
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
  padding: 20rpx 24rpx;
}

/* 考试卡片 */
.exam-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  &:active {
    opacity: 0.85;
  }

  .exam-card-header {
    display: flex;
    align-items: flex-start;
    margin-bottom: 24rpx;

    .exam-icon-wrapper {
      width: 80rpx;
      height: 80rpx;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;
      flex-shrink: 0;
      background: #e3f2fd;

      &.status-available { background: #e8f5e9; }
      &.status-ongoing { background: #fff3e0; }
      &.status-completed { background: #f3e5f5; }
      &.status-expired { background: #fafafa; }
      &.status-not-started { background: #e0e0e0; }

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
        color: #333;
        line-height: 1.4;
      }

      .exam-tags {
        display: flex;
        margin-top: 12rpx;

        .exam-tag {
          font-size: 20rpx;
          padding: 4rpx 14rpx;
          border-radius: 6rpx;
          margin-right: 12rpx;
          background: #e3f2fd;
          color: #2979ff;

          &.status-available { background: #e8f5e9; color: #4caf50; }
          &.status-ongoing { background: #fff3e0; color: #ff9800; }
          &.status-completed { background: #f3e5f5; color: #9c27b0; }
          &.status-expired { background: #fafafa; color: #999; }
          &.tag-normal { background: #f5f5f5; color: #666; }
        }
      }
    }
  }

  .exam-card-body {
    display: flex;
    align-items: center;
    justify-content: space-around;
    background: #f9f9f9;
    border-radius: 12rpx;
    padding: 24rpx 0;
    margin-bottom: 20rpx;

    .exam-stat {
      display: flex;
      flex-direction: column;
      align-items: center;

      .stat-value {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
      }

      .stat-label {
        font-size: 22rpx;
        color: #999;
        margin-top: 6rpx;
      }
    }

    .exam-divider {
      width: 1rpx;
      height: 48rpx;
      background: #e0e0e0;
    }
  }

  .exam-card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .exam-time {
      .time-text {
        font-size: 24rpx;

        &.time-available { color: #4caf50; }
        &.time-ongoing { color: #ff9800; }
        &.time-completed { color: #9c27b0; }
        &.time-expired { color: #999; }
      }
    }

    .exam-action {
      .action-btn {
        font-size: 26rpx;
        padding: 12rpx 32rpx;
        border-radius: 30rpx;
        font-weight: 500;

        &.btn-start {
          background: linear-gradient(135deg, #2979ff, #1565c0);
          color: #fff;
        }

        &.btn-continue {
          background: linear-gradient(135deg, #ff9800, #f57c00);
          color: #fff;
        }

        &.btn-result {
          background: #f5f5f5;
          color: #666;
        }

        &.btn-disabled {
          background: #f5f5f5;
          color: #ccc;
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
    color: #999;
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
    opacity: 0.4;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>
