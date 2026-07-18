<template>
  <view class="favorite-page">
    <scroll-view
      class="favorite-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onPullDownRefresh"
      @scrolltolower="onReachBottom"
    >
      <view v-if="list.length > 0" class="favorite-list">
        <view
          v-for="item in list"
          :key="item.favoriteId"
          class="favorite-card"
          @tap="goDetail(item)"
        >
          <view class="card-left">
            <view class="card-type-icon" :class="'type-' + item.contentType">
              <text v-if="item.contentType === 2">▶</text>
              <text v-else-if="item.contentType === 3">📊</text>
              <text v-else>📄</text>
            </view>
          </view>
          <view class="card-center">
            <text class="card-title text-ellipsis-2">{{ item.title }}</text>
            <view class="card-meta">
              <text class="meta-tag" :class="'tag-' + item.contentType">{{ getTypeText(item.contentType) }}</text>
              <text class="meta-time">收藏于 {{ formatTime(item.favoriteTime) }}</text>
            </view>
          </view>
          <view class="card-right" @tap.stop.prevent="removeFavorite(item)">
            <text class="remove-icon">✕</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon-text">⭐</text>
        <text class="empty-text">暂无收藏内容</text>
        <text class="empty-tip">去学习中心收藏感兴趣的内容吧</text>
      </view>

      <!-- 加载状态 -->
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
import { learn } from '@/api/index.js'

const list = ref([])
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const noMore = ref(false)
const isRefreshing = ref(false)

/**
 * 加载收藏列表
 */
const loadList = async (reset = false) => {
  if (loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
  }
  if (noMore.value && !reset) return

  loading.value = true
  try {
    const res = await learn.getFavoriteList()
    const items = Array.isArray(res) ? res : (res.list || res.records || [])
    if (reset) {
      list.value = items
    } else {
      list.value = [...list.value, ...items]
    }
    // 后端返回全量列表
    noMore.value = true
  } catch (err) {
    console.error('加载收藏列表失败:', err)
    if (reset) list.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 移除收藏
 */
const removeFavorite = (item) => {
  uni.showModal({
    title: '提示',
    content: `确定取消收藏"${item.title}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await learn.removeFavorite({ contentType: item.contentType, contentId: item.contentId })
          uni.showToast({ title: '已取消收藏', icon: 'none' })
          // 从列表中移除
          list.value = list.value.filter(i => i.favoriteId !== item.favoriteId)
        } catch (err) {
          console.error('取消收藏失败:', err)
        }
      }
    }
  })
}

/**
 * 跳转详情
 */
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

const getTypeText = (contentType) => {
  const map = { 1: '文章', 2: '视频', 3: '课件' }
  return map[contentType] || '内容'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
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

onShow(() => {
  loadList(true)
})
</script>

<style lang="scss" scoped>
.favorite-page {
  height: 100vh;
  background: #F8FAFC;
}

.favorite-scroll {
  height: 100%;
}

.favorite-list {
  padding: 20rpx 24rpx;
}

.favorite-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  &:active { opacity: 0.85; }

  .card-left {
    margin-right: 20rpx;

    .card-type-icon {
      width: 80rpx;
      height: 80rpx;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 36rpx;

      &.type-video, &.type-2 { background: #fce4ec; color: #e91e63; }
      &.type-ppt, &.type-3 { background: #fff3e0; color: #ff9800; }
      &.type-article, &.type-1 { background: #e8f5e9; color: #4caf50; }
    }
  }

  .card-center {
    flex: 1;
    overflow: hidden;

    .card-title {
      font-size: 28rpx;
      color: #333;
      line-height: 1.4;
    }

    .card-meta {
      display: flex;
      align-items: center;
      margin-top: 10rpx;

      .meta-tag {
        font-size: 20rpx;
        padding: 2rpx 10rpx;
        border-radius: 4rpx;
        margin-right: 12rpx;

        &.tag-video, &.tag-2 { background: #fce4ec; color: #e91e63; }
        &.tag-ppt, &.tag-3 { background: #fff3e0; color: #ff9800; }
        &.tag-article, &.tag-1 { background: #e8f5e9; color: #4caf50; }
      }

      .meta-time {
        font-size: 22rpx;
        color: #999;
      }
    }
  }

  .card-right {
    padding: 16rpx;

    .remove-icon {
      font-size: 28rpx;
      color: #ccc;
    }
  }
}

.loading-more {
  text-align: center;
  padding: 30rpx 0;
  .loading-text { font-size: 24rpx; color: #999; }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 150rpx 0;

  .empty-icon-text { font-size: 100rpx; margin-bottom: 20rpx; opacity: 0.4; }
  .empty-text { font-size: 28rpx; color: #999; }
  .empty-tip { font-size: 24rpx; color: #ccc; margin-top: 12rpx; }
}
</style>
