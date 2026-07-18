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

    <!-- 横幅 Banner -->
    <view class="hero-banner">
      <image src="/static/learn-banner.jpg" class="banner-img" mode="aspectFill" />
      <view class="banner-overlay">
        <text class="banner-title">智慧护理学习中心</text>
        <text class="banner-subtitle">专业护理知识 · 助力终身学习</text>
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
          <!-- 左侧封面区 -->
          <view class="card-cover" :class="'cover-type-' + item.contentType">
            <image
              v-if="item.coverImage"
              :src="item.coverImage"
              class="cover-img"
              mode="aspectFill"
            />
            <view v-else class="cover-placeholder">
              <text class="cover-icon">{{ getTypeIcon(item.contentType) }}</text>
            </view>
            <!-- 类型标签 -->
            <view class="cover-badge">
              <text>{{ getTypeLabel(item.contentType) }}</text>
            </view>
            <!-- 视频时长角标 -->
            <view v-if="item.contentType === 2 && item.duration" class="cover-duration">
              <text>▶ {{ formatDuration(item.duration) }}</text>
            </view>
          </view>

          <!-- 右侧内容区 -->
          <view class="card-content">
            <!-- 标题 -->
            <text class="card-title text-ellipsis-2">{{ item.title }}</text>
            <!-- 简介 -->
            <text v-if="item.contentType !== 2" class="card-summary text-ellipsis-1">
              {{ item.summary || stripHtml(item.content) }}
            </text>
            <text v-else class="card-summary text-ellipsis-1">点击观看视频课程</text>

            <!-- 底部元数据行（浅灰底色包裹） -->
            <view class="card-meta-bar">
              <view class="meta-item">
                <text class="meta-icon">{{ item.contentType === 1 ? '👁' : '▶' }}</text>
                <text class="meta-text">{{ formatViewCount(item.viewCount) }}次{{ item.contentType === 1 ? '阅读' : '学习' }}</text>
              </view>
              <view v-if="item.createTime" class="meta-item meta-time">
                <text class="meta-icon">🕐</text>
                <text class="meta-text">{{ formatTime(item.createTime) }}</text>
              </view>
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
 * 获取类型图标（用于无封面时的占位）
 */
const getTypeIcon = (type) => {
  const map = { 1: '📄', 2: '🎬', 3: '📊' }
  return map[type] || '📄'
}

/**
 * 获取类型标签文字
 */
const getTypeLabel = (type) => {
  const map = { 1: '文章', 2: '视频', 3: '课件' }
  return map[type] || '内容'
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
/* ==================== 设计变量 ==================== */
$primary: #0EA5E9;           // 医护浅蓝（主色）
$primary-dark: #0284C7;      // 主色深
$primary-light: #E0F2FE;      // 主色浅底
$bg-page: #F8FAFC;           // 全局浅灰背景
$bg-card: #FFFFFF;
$text-title: #1E293B;        // 标题深色
$text-body: #475569;         // 正文（替代纯黑）
$text-muted: #94A3B8;         // 次要文字
$border-light: #E2E8F0;
$shadow-card: 0 8rpx 12rpx -4rpx rgba(15, 23, 42, 0.05);
$shadow-hover: 0 16rpx 32rpx -8rpx rgba(14, 165, 233, 0.18);
$radius-card: 24rpx;         // 12px
$radius-pill: 999rpx;

.learn-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: $bg-page;
}

/* ==================== 搜索栏 ==================== */
.search-bar {
  padding: 20rpx 32rpx 16rpx;
  background: $bg-card;

  .search-input-wrapper {
    display: flex;
    align-items: center;
    background: $bg-page;
    border-radius: $radius-pill;
    padding: 0 28rpx;
    height: 80rpx;
    border: 2rpx solid transparent;
    box-shadow: 0 4rpx 12rpx rgba(15, 23, 42, 0.04);
    transition: all 0.3s ease;

    &:focus-within {
      background: $bg-card;
      border-color: $primary;
      box-shadow: 0 4rpx 20rpx rgba(14, 165, 233, 0.12);
    }

    .search-icon {
      font-size: 30rpx;
      margin-right: 14rpx;
      color: $primary;
      opacity: 0.85;
    }

    .search-input {
      flex: 1;
      height: 80rpx;
      font-size: 28rpx;
      color: $text-body;
    }

    .search-placeholder {
      color: $text-muted;
      font-size: 28rpx;
    }

    .clear-icon {
      font-size: 28rpx;
      color: $text-muted;
      padding: 10rpx;
      transition: color 0.2s;

      &:active {
        color: $primary;
      }
    }
  }
}

/* ==================== 横幅 Banner ==================== */
.hero-banner {
  position: relative;
  width: 100%;
  height: 240rpx;
  overflow: hidden;
  margin: 16rpx 0 8rpx;

  .banner-img {
    width: 100%;
    height: 100%;
  }

  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(2, 132, 199, 0.78), rgba(14, 165, 233, 0.55));
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0 40rpx;

    .banner-title {
      font-size: 40rpx;
      font-weight: 700;
      color: #FFFFFF;
      text-shadow: 0 2rpx 12rpx rgba(2, 132, 199, 0.4);
      letter-spacing: 2rpx;
    }

    .banner-subtitle {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.95);
      margin-top: 12rpx;
      letter-spacing: 1rpx;
    }
  }
}

/* ==================== 分类标签（药丸状按钮） ==================== */
.category-tabs {
  white-space: nowrap;
  background: $bg-card;
  height: 96rpx;

  .category-list {
    display: inline-flex;
    align-items: center;
    height: 96rpx;
    padding: 0 32rpx;

    .category-item {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      margin-right: 20rpx;
      position: relative;
      height: 64rpx;
      justify-content: center;
      padding: 0 28rpx;
      border-radius: $radius-pill;
      background: #F1F5F9;
      transition: all 0.3s ease;

      .category-text {
        font-size: 26rpx;
        color: $text-body;
        transition: all 0.3s ease;
      }

      /* 选中态：主色底白字 */
      &.active {
        background: $primary;
        box-shadow: 0 6rpx 16rpx rgba(14, 165, 233, 0.3);

        .category-text {
          color: #FFFFFF;
          font-weight: 600;
          font-size: 28rpx;
        }
      }

      /* hover 过渡（H5） */
      &:not(.active):active {
        background: #E2E8F0;
      }

      .category-underline {
        display: none;
      }
    }
  }
}

/* ==================== 内容滚动区 ==================== */
.content-scroll {
  flex: 1;
  overflow: hidden;
}

.content-list {
  padding: 20rpx 32rpx 40rpx;
}

/* ==================== 课程卡片（核心 - 左图右文统一布局） ==================== */
.content-card {
  display: flex;
  background: $bg-card;
  border-radius: $radius-card;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: $shadow-card;
  transition: all 0.3s ease;
  cursor: pointer;

  /* H5 悬停效果 */
  &:hover {
    transform: translateY(-8rpx);
    box-shadow: $shadow-hover;
  }

  /* 移动端按下效果 */
  &:active {
    opacity: 0.95;
    transform: translateY(-4rpx);
    box-shadow: $shadow-hover;
  }
}

/* 左侧封面区 */
.card-cover {
  position: relative;
  width: 220rpx;
  height: 200rpx;
  flex-shrink: 0;
  background: #1E293B;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
  }

  /* 无封面时的渐变占位 */
  .cover-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0EA5E9, #0284C7);

    .cover-icon {
      font-size: 72rpx;
      opacity: 0.85;
    }
  }

  /* 不同类型封面渐变色 */
  &.cover-type-1 .cover-placeholder {
    background: linear-gradient(135deg, #0EA5E9, #0284C7);
  }

  &.cover-type-2 .cover-placeholder {
    background: linear-gradient(135deg, #6366F1, #4F46E5);
  }

  &.cover-type-3 .cover-placeholder {
    background: linear-gradient(135deg, #14B8A6, #0D9488);
  }

  /* 类型标签角标 */
  .cover-badge {
    position: absolute;
    top: 12rpx;
    left: 12rpx;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(4rpx);
    padding: 4rpx 14rpx;
    border-radius: $radius-pill;

    text {
      font-size: 20rpx;
      color: #1E293B;
      font-weight: 600;
    }
  }

  /* 视频时长角标 */
  .cover-duration {
    position: absolute;
    bottom: 12rpx;
    right: 12rpx;
    background: rgba(15, 23, 42, 0.72);
    backdrop-filter: blur(4rpx);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;

    text {
      font-size: 20rpx;
      color: #fff;
    }
  }
}

/* 右侧内容区 */
.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 24rpx 28rpx;
  min-width: 0;
  min-height: 200rpx;

  .card-title {
    font-size: 32rpx;
    font-weight: 600;
    color: $text-title;
    line-height: 1.4;
    word-break: break-all;
  }

  .card-summary {
    font-size: 26rpx;
    color: $text-muted;
    line-height: 1.5;
    margin-top: 10rpx;
  }
}

/* ==================== 元信息行（浅灰底色包裹，配线性图标） ==================== */
.card-meta-bar {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
  padding: 10rpx 16rpx;
  background: #F1F5F9;
  border-radius: 12rpx;

  .meta-item {
    display: flex;
    align-items: center;
    margin-right: 20rpx;

    .meta-icon {
      font-size: 22rpx;
      margin-right: 6rpx;
      opacity: 0.7;
    }

    .meta-text {
      font-size: 22rpx;
      color: $text-muted;
    }

    &.meta-time {
      margin-left: auto;
      margin-right: 0;

      .meta-text {
        color: #CBD5E1;
      }
    }
  }
}

/* ==================== 加载与空状态 ==================== */
.loading-more {
  text-align: center;
  padding: 40rpx 0;

  .loading-text {
    font-size: 24rpx;
    color: $text-muted;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 140rpx 0;

  .empty-icon-text {
    font-size: 100rpx;
    margin-bottom: 24rpx;
    opacity: 0.3;
  }

  .empty-text {
    font-size: 28rpx;
    color: $text-muted;
  }
}

/* ==================== 文本省略工具类 ==================== */
.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.text-ellipsis-1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
