<template>
  <view class="record-page">
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
          :key="item.recordId"
          class="record-card"
          @tap="goDetail(item)"
        >
          <view class="record-left">
            <view class="record-type-icon" :class="'type-' + item.contentType">
              <text v-if="item.contentType === 2">▶</text>
              <text v-else-if="item.contentType === 3">📊</text>
              <text v-else>📄</text>
            </view>
          </view>
          <view class="record-center">
            <view class="record-title-row">
              <text class="record-title text-ellipsis">{{ item.title || '未知内容' }}</text>
              <text class="type-badge" :class="'badge-' + item.contentType">{{ item.typeName }}</text>
            </view>
            <view class="record-meta">
              <text class="meta-text">学习时长: {{ formatDuration(item.studyDuration) }}</text>
              <text class="meta-text meta-time">{{ formatTime(item.lastStudyTime) }}</text>
            </view>
            <view class="record-progress">
              <view class="progress-bar">
                <view class="progress-inner" :style="{ width: (item.progress || 0) + '%' }"></view>
              </view>
              <text class="progress-text">{{ item.progress || 0 }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon-text">📖</text>
        <text class="empty-text">暂无学习记录</text>
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
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { learn, user } from '@/api/index.js'

const list = ref([])
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
    const res = await learn.getLearningRecords({ page: page.value, pageSize })
    const items = res.list || res.records || res || []
    if (reset) {
      list.value = items
    } else {
      list.value = [...list.value, ...items]
    }
    const total = res.total || 0
    if (list.value.length >= total || items.length < pageSize) {
      noMore.value = true
    } else {
      page.value++
    }
  } catch (err) {
    console.error('加载学习记录失败:', err)
    if (reset) list.value = []
  } finally {
    loading.value = false
  }
}

const goDetail = (item) => {
  let url = ''
  if (item.contentType === 2) {
    url = `/pages/learn/video-detail?id=${item.contentId}`
  } else if (item.contentType === 3) {
    url = `/pages/learn/ppt-detail?id=${item.contentId}`
  } else {
    url = `/pages/learn/article-detail?id=${item.contentId}`
  }
  uni.navigateTo({ url })
}

const formatDuration = (seconds) => {
  if (!seconds) return '0分'
  if (seconds < 60) return `${seconds}秒`
  const min = Math.floor(seconds / 60)
  if (min < 60) return `${min}分钟`
  const hours = Math.floor(min / 60)
  return `${hours}小时${min % 60}分`
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 86400000) return '今天'
  if (diff < 172800000) return '昨天'
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const onPullDownRefresh = async () => {
  isRefreshing.value = true
  await loadList(true)
  isRefreshing.value = false
  uni.stopPullDownRefresh()
}

const onReachBottom = () => {
  if (!noMore.value && !loading.value) loadList()
}

onShow(() => { loadList(true) })
</script>

<style lang="scss" scoped>
.record-page { height: 100vh; background: #F7F4EF; }
.record-scroll { height: 100%; }
.record-list { padding: 20rpx 32rpx; }

.record-card {
  display: flex;
  background: #FFFFFF;
  border-radius: 24rpx;
  padding: 28rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 16rpx rgba(58, 76, 86, 0.06);
  transition: all 0.4s ease-out;
  &:active { opacity: 0.9; transform: translateY(1rpx); }

  .record-left { margin-right: 20rpx;
    .record-type-icon {
      width: 80rpx; height: 80rpx; border-radius: 20rpx;
      display: flex; align-items: center; justify-content: center; font-size: 36rpx;
      &.type-2 { background: #F5E0E2; color: #D8B7BC; }
      &.type-3 { background: #F5E6DA; color: #D39468; }
      &.type-1 { background: #E0EDE0; color: #95BB92; }
    }
  }

  .record-center { flex: 1; overflow: hidden;
    .record-title-row { display: flex; align-items: center; }
    .record-title { font-size: 28rpx; color: #3A4C56; font-weight: 500; flex: 1; }
    .type-badge {
      font-size: 18rpx; padding: 2rpx 12rpx; border-radius: 8rpx; margin-left: 12rpx; flex-shrink: 0;
      &.badge-1 { background: #E0EDE0; color: #95BB92; }
      &.badge-2 { background: #F5E0E2; color: #D8B7BC; }
      &.badge-3 { background: #F5E6DA; color: #D39468; }
    }
    .record-meta { display: flex; margin-top: 10rpx;
      .meta-text { font-size: 22rpx; color: #989FA6; margin-right: 20rpx; }
      .meta-time { margin-left: auto; }
    }
    .record-progress { display: flex; align-items: center; margin-top: 12rpx;
      .progress-bar { flex: 1; height: 10rpx; background: #D1E4F5; border-radius: 5rpx; overflow: hidden; margin-right: 12rpx;
        .progress-inner { height: 100%; background: #C77A60; border-radius: 5rpx; transition: width 0.4s ease-out; }
      }
      .progress-text { font-size: 22rpx; color: #93B4B8; font-weight: 600; }
    }
  }
}

.loading-more { text-align: center; padding: 30rpx 0; .loading-text { font-size: 24rpx; color: #989FA6; } }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 150rpx 0;
  .empty-icon-text { font-size: 100rpx; margin-bottom: 20rpx; opacity: 0.35; }
  .empty-text { font-size: 28rpx; color: #989FA6; }
}
</style>
