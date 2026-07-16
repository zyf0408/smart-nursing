<template>
  <view class="learn-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          v-model="keyword"
          class="search-input"
          type="text"
          placeholder="搜索学习内容"
          placeholder-class="search-placeholder"
          confirm-type="search"
          @confirm="handleSearch"
        />
        <text v-if="keyword" class="clear-icon" @tap="clearSearch">✕</text>
      </view>
    </view>

    <!-- 分类 Tab 横向滚动 -->
    <scroll-view class="category-tabs" scroll-x show-scrollbar="false">
      <view class="category-list">
        <view
          v-for="(item, index) in categories"
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @tap="switchCategory(item.value)"
        >
          <text class="category-text">{{ item.label }}</text>
          <view v-if="currentCategory === item.value" class="category-underline"></view>
        </view>
      </view>
    </scroll-view>

    <!-- 内容列表 -->
    <scroll-view
      class="content-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onPullDownRefresh"
      @scrolltolower="onReachBottom"
    >
      <!-- 内容列表 -->
      <view v-if="contentList.length > 0" class="content-list">
        <view
          v-for="item in contentList"
          :key="item.contentId"
          class="content-card"
          @tap="goDetail(item)"
        >
          <!-- 视频类型 contentType=2 -->
          <view v-if="item.contentType === 2" class="card-video">
            <view class="video-cover">
              <image v-if="item.coverImage" :src="item.coverImage" class="cover-img" mode="aspectFill" />
              <view v-else class="cover-placeholder">
                <text class="cover-icon">▶</text>
              </view>
              <view class="video-duration" v-if="item.duration">
                <text>{{ formatDuration(item.duration) }}</text>
              </view>
              <view class="video-play-icon">
                <text>▶</text>
              </view>
            </view>
            <view class="card-body">
              <text class="card-title text-ellipsis-2">{{ item.title }}</text>
              <view class="card-meta">
                <text class="meta-tag tag-video">视频</text>
                <text class="meta-text">{{ formatViewCount(item.viewCount) }}次学习</text>
              </view>
            </view>
          </view>

          <!-- 文章类型 contentType=1 -->
          <view v-else-if="item.contentType === 1" class="card-article">
            <view class="article-body">
              <text class="card-title text-ellipsis-2">{{ item.title }}</text>
              <text class="card-summary text-ellipsis-2">{{ item.summary || stripHtml(item.content) }}</text>
              <view class="card-meta">
                <text class="meta-tag tag-article">文章</text>
                <text class="meta-text">{{ formatViewCount(item.viewCount) }}次阅读</text>
                <text class="meta-text meta-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
            <image v-if="item.coverImage" :src="item.coverImage" class="article-thumb" mode="aspectFill" />
          </view>

          <!-- PPT/课件类型 contentType=3 -->
          <view v-else-if="item.contentType === 3" class="card-ppt">
            <view class="ppt-icon-wrapper">
              <text class="ppt-icon">📊</text>
            </view>
            <view class="card-body">
              <text class="card-title text-ellipsis-2">{{ item.title }}</text>
              <view class="card-meta">
                <text class="meta-tag tag-ppt">课件</text>
                <text class="meta-text">{{ formatViewCount(item.viewCount) }}次学习</text>
                <text class="meta-text meta-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
          </view>

          <!-- 默认类型 -->
          <view v-else class="card-default">
            <view class="card-body">
              <text class="card-title text-ellipsis-2">{{ item.title }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-state">
        <text class="empty-icon-text">📚</text>
        <text class="empty-text">暂无学习内容</text>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-more">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-else-if="noMore && contentList.length > 0" class="loading-more">
        <text class="loading-text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { learn } from '@/api/index.js'
import http from '@/api/request.js'

// 分类列表（从后端动态加载）
const categories = ref([
  { label: '全部', value: '' }
])

// 状态
const currentCategory = ref('')
const keyword = ref('')
const contentList = ref([])
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const noMore = ref(false)
const isRefreshing = ref(false)

/**
 * 加载分类列表
 */
const loadCategories = async () => {
  try {
    const res = await http.get('/common/category/tree')
    const list = Array.isArray(res) ? res : (res.list || [])
    categories.value = [{ label: '全部', value: '' }]
    list.forEach(cat => {
      categories.value.push({ label: cat.categoryName, value: cat.categoryId })
      // 加载子分类
      if (cat.children && cat.children.length > 0) {
        cat.children.forEach(child => {
          categories.value.push({ label: '  ' + child.categoryName, value: child.categoryId })
        })
      }
    })
  } catch (err) {
    console.error('加载分类失败:', err)
  }
}

/**
 * 加载内容列表
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
    const params = {}
    if (currentCategory.value) params.categoryId = currentCategory.value
    if (keyword.value) params.keyword = keyword.value

    const res = await learn.getContentList(params)
    const list = Array.isArray(res) ? res : (res.list || res.records || [])
    
    if (reset) {
      contentList.value = list
    } else {
      contentList.value = [...contentList.value, ...list]
    }

    // 后端返回全量数组，无分页
    if (list.length < pageSize) {
      noMore.value = true
    } else {
      page.value++
    }
  } catch (err) {
    console.error('加载列表失败:', err)
    if (reset) contentList.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 切换分类
 */
const switchCategory = (value) => {
  if (currentCategory.value === value) return
  currentCategory.value = value
  loadList(true)
}

/**
 * 搜索
 */
const handleSearch = () => {
  loadList(true)
}

/**
 * 清除搜索
 */
const clearSearch = () => {
  keyword.value = ''
  loadList(true)
}

/**
 * 下拉刷新
 */
const onPullDownRefresh = async () => {
  isRefreshing.value = true
  await loadList(true)
  isRefreshing.value = false
  uni.stopPullDownRefresh()
}

/**
 * 上拉加载更多
 */
const onReachBottom = () => {
  if (!noMore.value && !loading.value) {
    loadList()
  }
}

/**
 * 跳转详情页
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

/**
 * 格式化时长
 */
const formatDuration = (seconds) => {
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min}:${sec.toString().padStart(2, '0')}`
}

/**
 * 格式化浏览次数
 */
const formatViewCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

/**
 * 去除HTML标签
 */
const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').substring(0, 80)
}

// 页面显示时加载数据
onShow(() => {
  loadCategories()
  if (contentList.value.length === 0) {
    loadList(true)
  }
})
</script>

<style lang="scss" scoped>
.learn-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

/* 搜索栏 */
.search-bar {
  padding: 16rpx 24rpx;
  background: #fff;

  .search-input-wrapper {
    display: flex;
    align-items: center;
    background: #f0f2f5;
    border-radius: 36rpx;
    padding: 0 24rpx;
    height: 72rpx;

    .search-icon {
      font-size: 28rpx;
      margin-right: 12rpx;
      opacity: 0.5;
    }

    .search-input {
      flex: 1;
      height: 72rpx;
      font-size: 28rpx;
    }

    .search-placeholder {
      color: #999;
      font-size: 28rpx;
    }

    .clear-icon {
      font-size: 28rpx;
      color: #ccc;
      padding: 10rpx;
    }
  }
}

/* 分类 Tab */
.category-tabs {
  white-space: nowrap;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  height: 88rpx;

  .category-list {
    display: inline-flex;
    align-items: center;
    height: 88rpx;
    padding: 0 24rpx;

    .category-item {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      margin-right: 48rpx;
      position: relative;
      height: 88rpx;
      justify-content: center;

      .category-text {
        font-size: 28rpx;
        color: #666;
        transition: color 0.3s;
      }

      &.active .category-text {
        color: #2979ff;
        font-weight: bold;
        font-size: 30rpx;
      }

      .category-underline {
        position: absolute;
        bottom: 8rpx;
        width: 48rpx;
        height: 6rpx;
        background: #2979ff;
        border-radius: 3rpx;
      }
    }
  }
}

/* 内容滚动区 */
.content-scroll {
  flex: 1;
  overflow: hidden;
}

.content-list {
  padding: 20rpx 24rpx;
}

/* 内容卡片通用 */
.content-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

  &:active {
    opacity: 0.85;
  }
}

/* 视频卡片 */
.card-video {
  .video-cover {
    position: relative;
    width: 100%;
    height: 360rpx;
    background: #000;

    .cover-img {
      width: 100%;
      height: 100%;
    }

    .cover-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #2c3e50, #3498db);

      .cover-icon {
        font-size: 80rpx;
        color: rgba(255, 255, 255, 0.6);
      }
    }

    .video-duration {
      position: absolute;
      right: 16rpx;
      bottom: 16rpx;
      background: rgba(0, 0, 0, 0.7);
      color: #fff;
      font-size: 22rpx;
      padding: 4rpx 12rpx;
      border-radius: 6rpx;
    }

    .video-play-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;

      text {
        color: #fff;
        font-size: 32rpx;
        margin-left: 6rpx;
      }
    }
  }
}

/* 文章卡片 */
.card-article {
  display: flex;
  padding: 24rpx;

  .article-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-right: 20rpx;

    .card-summary {
      font-size: 26rpx;
      color: #888;
      line-height: 1.5;
      margin-top: 10rpx;
    }
  }

  .article-thumb {
    width: 180rpx;
    height: 130rpx;
    border-radius: 12rpx;
    flex-shrink: 0;
  }
}

/* PPT卡片 */
.card-ppt {
  display: flex;
  align-items: center;
  padding: 24rpx;

  .ppt-icon-wrapper {
    width: 100rpx;
    height: 100rpx;
    border-radius: 16rpx;
    background: linear-gradient(135deg, #ff9800, #f57c00);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
    flex-shrink: 0;

    .ppt-icon {
      font-size: 48rpx;
    }
  }
}

/* 卡片内容区域 */
.card-body {
  padding: 24rpx;

  .card-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333;
    line-height: 1.4;
  }
}

/* 卡片元信息 */
.card-meta {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
  flex-wrap: wrap;

  .meta-tag {
    font-size: 20rpx;
    padding: 4rpx 12rpx;
    border-radius: 6rpx;
    margin-right: 12rpx;
    background: #e3f2fd;
    color: #2979ff;

    &.tag-video {
      background: #fce4ec;
      color: #e91e63;
    }

    &.tag-article {
      background: #e8f5e9;
      color: #4caf50;
    }

    &.tag-ppt {
      background: #fff3e0;
      color: #ff9800;
    }
  }

  .meta-text {
    font-size: 22rpx;
    color: #999;
    margin-right: 16rpx;
  }

  .meta-time {
    margin-left: auto;
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
