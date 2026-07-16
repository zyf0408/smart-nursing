<template>
  <view class="exam-record-page">
    <!-- 统计概览 -->
    <view class="stats-overview">
      <view class="overview-item">
        <text class="overview-value">{{ stats.total || 0 }}</text>
        <text class="overview-label">总考试</text>
      </view>
      <view class="overview-item">
        <text class="overview-value overview-pass">{{ stats.passed || 0 }}</text>
        <text class="overview-label">通过</text>
      </view>
      <view class="overview-item">
        <text class="overview-value overview-fail">{{ stats.failed || 0 }}</text>
        <text class="overview-label">未通过</text>
      </view>
      <view class="overview-item">
        <text class="overview-value overview-avg">{{ stats.avgScore || 0 }}</text>
        <text class="overview-label">平均分</text>
      </view>
    </view>

    <scroll-view
      class="record-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onPullDownRefresh"
      @scrolltolower="onReachBottom"
    >
      <view v-if="list.length > 0" class="record-list">
        <view
          v-for="item in list"
          :key="item.id"
          class="record-card"
          @tap="goResult(item)"
        >
          <view class="card-header">
            <text class="card-title text-ellipsis">{{ item.examTitle || item.title }}</text>
            <view class="card-score" :class="{ passed: item.score >= item.passScore, failed: item.score < item.passScore }">
              <text class="score-value">{{ item.score }}</text>
              <text class="score-label">分</text>
            </view>
          </view>
          <view class="card-body">
            <view class="card-info">
              <text class="info-item">总分: {{ item.totalScore }}</text>
              <text class="info-item">及格分: {{ item.passScore }}</text>
              <text class="info-item">用时: {{ formatDuration(item.duration) }}</text>
            </view>
            <view class="card-status">
              <text class="status-tag" :class="{ 'tag-pass': item.score >= item.passScore, 'tag-fail': item.score < item.passScore }">
                {{ item.score >= item.passScore ? '通过' : '未通过' }}
              </text>
              <text class="exam-time">{{ formatDateTime(item.submitTime) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon-text">📋</text>
        <text class="empty-text">暂无考试记录</text>
      </view>

      <view v-if="loading" class="loading-more">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-else-if="noMore && list.length > 0" class="loading-more">
        <text class="loading-text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { exam } from '@/api/index.js'

const list = ref([])
const stats = reactive({ total: 0, passed: 0, failed: 0, avgScore: 0 })
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const noMore = ref(false)
const isRefreshing = ref(false)

const loadList = async (reset = false) => {
  if (loading.value) return
  if (reset) { page.value = 1; noMore.value = false }
  if (noMore.value && !reset) return

  loading.value = true
  try {
    const res = await exam.getExamRecords({ page: page.value, pageSize })
    const items = res.list || res.records || res || []
    if (reset) { list.value = items } else { list.value = [...list.value, ...items] }
    const total = res.total || 0
    if (list.value.length >= total || items.length < pageSize) { noMore.value = true } else { page.value++ }

    // 更新统计
    if (res.stats) Object.assign(stats, res.stats)
    if (reset && list.value.length > 0 && !res.stats) {
      stats.total = total || list.value.length
      stats.passed = list.value.filter(i => i.score >= i.passScore).length
      stats.failed = list.value.filter(i => i.score < i.passScore).length
      const sum = list.value.reduce((acc, i) => acc + (i.score || 0), 0)
      stats.avgScore = list.value.length > 0 ? Math.round(sum / list.value.length) : 0
    }
  } catch (err) {
    console.error('加载考试记录失败:', err)
    if (reset) list.value = []
  } finally {
    loading.value = false
  }
}

const goResult = (item) => {
  uni.navigateTo({ url: `/pages/exam/exam-result?recordId=${item.id || item.recordId}` })
}

const formatDuration = (seconds) => {
  if (!seconds) return '未知'
  const min = Math.floor(seconds / 60)
  return `${min}分钟`
}

const formatDateTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const onPullDownRefresh = async () => {
  isRefreshing.value = true
  await loadList(true)
  isRefreshing.value = false
  uni.stopPullDownRefresh()
}

const onReachBottom = () => { if (!noMore.value && !loading.value) loadList() }

onShow(() => { loadList(true) })
</script>

<style lang="scss" scoped>
.exam-record-page { height: 100vh; background: #f5f5f5; display: flex; flex-direction: column; }

.stats-overview {
  display: flex; background: #fff; padding: 28rpx 0; border-bottom: 1rpx solid #f0f0f0;
  .overview-item { flex: 1; display: flex; flex-direction: column; align-items: center;
    .overview-value { font-size: 36rpx; font-weight: bold; color: #2979ff; }
    .overview-pass { color: #4caf50; }
    .overview-fail { color: #f44336; }
    .overview-avg { color: #ff9800; }
    .overview-label { font-size: 22rpx; color: #999; margin-top: 6rpx; }
  }
}

.record-scroll { flex: 1; overflow: hidden; }
.record-list { padding: 20rpx 24rpx; }

.record-card {
  background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  &:active { opacity: 0.85; }

  .card-header { display: flex; justify-content: space-between; align-items: flex-start;
    .card-title { flex: 1; font-size: 30rpx; font-weight: 600; color: #333; margin-right: 20rpx; }
    .card-score { display: flex; align-items: baseline;
      .score-value { font-size: 40rpx; font-weight: bold; }
      .score-label { font-size: 22rpx; margin-left: 4rpx; }
      &.passed .score-value, &.passed .score-label { color: #4caf50; }
      &.failed .score-value, &.failed .score-label { color: #f44336; }
    }
  }

  .card-body { margin-top: 16rpx;
    .card-info { display: flex; flex-wrap: wrap;
      .info-item { font-size: 24rpx; color: #999; margin-right: 24rpx; }
    }
    .card-status { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx;
      .status-tag { font-size: 22rpx; padding: 4rpx 14rpx; border-radius: 6rpx;
        &.tag-pass { background: #e8f5e9; color: #4caf50; }
        &.tag-fail { background: #ffebee; color: #f44336; }
      }
      .exam-time { font-size: 22rpx; color: #ccc; }
    }
  }
}

.loading-more { text-align: center; padding: 30rpx 0; .loading-text { font-size: 24rpx; color: #999; } }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 150rpx 0;
  .empty-icon-text { font-size: 100rpx; margin-bottom: 20rpx; opacity: 0.4; }
  .empty-text { font-size: 28rpx; color: #999; }
}
</style>
