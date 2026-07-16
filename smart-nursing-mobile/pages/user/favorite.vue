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
          :key="item.id"
          class="favorite-card"
          @tap="goDetail(item)"
        >
          <view class="card-left">
            <view class="card-type-icon" :class="'type-' + item.type">
              <text v-if="item.type === 'video'">▶</text>
              <text v-else-if="item.type === 'ppt'">📊</text>
              <text v-else>📄</text>
            </view>
          </view>
          <view class="card-center">
            <text class="card-title text-ellipsis-2">{{ item.title }}</text>
            <view class="card-meta">
              <text class="meta-tag" :class="'tag-' + item.type">{{ getTypeText(item.type) }}</text>
              <text class="meta-time">收藏于 {{ formatTime(item.favoriteTime) }}</text>
            </view>
          </view>
          <view class="card-right" @tap.stop="removeFavorite(item)">
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
    const res = await learn.getFavoriteList({ page: page.value, pageSize })
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
          await learn.removeFavorite(item.contentId || item.id)
          uni.showToast({ title: '已取消收藏', icon: 'none' })
          // 从列表中移除
          list.value = list.value.filter(i => (i.contentId || i.id) !== (item.contentId || item.id))
        } catch (err) {
          uni.showToast({ title: '操作失败', icon: 'none' })
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
  if (item.type === 'video') {
    url = `/pages/learn/video-detail?id=${item.contentId || item.id}`
  } else if (item.type === 'ppt') {
    url = `/pages/learn/ppt-detail?id=${item.contentId || item.id}`
  } else {
    url = `/pages/learn/article-detail?id=${item.contentId || item.id}`
  }
  uni.navigateTo({ url })
}

const getTypeText = (type) => {
  const map = { article: '文章', video: '视频', ppt: '课件' }
  return map[type] || '内容'
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
  background: #f5f5f5;
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

      &.type-video { background: #fce4ec; color: #e91e63; }
      &.type-ppt { background: #fff3e0; color: #ff9800; }
      &.type-article { background: #e8f5e9; color: #4caf50; }
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

        &.tag-video { background: #fce4ec; color: #e91e63; }
        &.tag-ppt { background: #fff3e0; color: #ff9800; }
        &.tag-article { background: #e8f5e9; color: #4caf50; }
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
