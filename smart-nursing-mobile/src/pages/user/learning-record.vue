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
          :key="item.id"
          class="record-card"
          @tap="goDetail(item)"
        >
          <view class="record-left">
            <view class="record-type-icon" :class="'type-' + item.type">
              <text v-if="item.type === 'video'">▶</text>
              <text v-else-if="item.type === 'ppt'">📊</text>
              <text v-else>📄</text>
            </view>
          </view>
          <view class="record-center">
            <text class="record-title text-ellipsis">{{ item.title }}</text>
            <view class="record-meta">
              <text class="meta-text">学习时长: {{ formatDuration(item.duration) }}</text>
              <text class="meta-text meta-time">{{ formatTime(item.studyTime || item.createTime) }}</text>
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
  if (item.type === 'video') {
    url = `/pages/learn/video-detail?id=${item.contentId || item.id}`
  } else if (item.type === 'ppt') {
    url = `/pages/learn/ppt-detail?id=${item.contentId || item.id}`
  } else {
    url = `/pages/learn/article-detail?id=${item.contentId || item.id}`
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
.record-page { height: 100vh; background: #f5f5f5; }
.record-scroll { height: 100%; }
.record-list { padding: 20rpx 24rpx; }

.record-card {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  &:active { opacity: 0.85; }

  .record-left { margin-right: 20rpx;
    .record-type-icon {
      width: 80rpx; height: 80rpx; border-radius: 16rpx;
      display: flex; align-items: center; justify-content: center; font-size: 36rpx;
      &.type-video { background: #fce4ec; color: #e91e63; }
      &.type-ppt { background: #fff3e0; color: #ff9800; }
      &.type-article { background: #e8f5e9; color: #4caf50; }
    }
  }

  .record-center { flex: 1; overflow: hidden;
    .record-title { font-size: 28rpx; color: #333; font-weight: 500; }
    .record-meta { display: flex; margin-top: 10rpx;
      .meta-text { font-size: 22rpx; color: #999; margin-right: 20rpx; }
      .meta-time { margin-left: auto; }
    }
    .record-progress { display: flex; align-items: center; margin-top: 12rpx;
      .progress-bar { flex: 1; height: 10rpx; background: #f0f0f0; border-radius: 5rpx; overflow: hidden; margin-right: 12rpx;
        .progress-inner { height: 100%; background: #2979ff; border-radius: 5rpx; transition: width 0.3s; }
      }
      .progress-text { font-size: 22rpx; color: #2979ff; font-weight: 600; }
    }
  }
}

.loading-more { text-align: center; padding: 30rpx 0; .loading-text { font-size: 24rpx; color: #999; } }
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 150rpx 0;
  .empty-icon-text { font-size: 100rpx; margin-bottom: 20rpx; opacity: 0.4; }
  .empty-text { font-size: 28rpx; color: #999; }
}
</style>
