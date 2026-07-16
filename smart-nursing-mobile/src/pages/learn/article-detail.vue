<template>
  <view class="article-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="content" class="article-content">
      <!-- 文章头部 -->
      <view class="article-header">
        <text class="article-title">{{ content.title }}</text>
        <view class="article-meta">
          <text class="meta-item">{{ content.categoryName || '未分类' }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ content.author || '佚名' }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ formatTime(content.createTime) }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ content.viewCount || 0 }}次阅读</text>
        </view>
      </view>

      <!-- 富文本内容 - 使用 uni-app 内置 rich-text 组件 -->
      <view class="article-body">
        <rich-text
          :nodes="content.content || ''"
          :selectable="true"
        />
      </view>

      <!-- 底部进度条 -->
      <view class="progress-section">
        <view class="progress-info">
          <text class="progress-label">学习进度</text>
          <text class="progress-percent">{{ studyPercent }}%</text>
        </view>
        <view class="progress-bar">
          <view class="progress-inner" :style="{ width: studyPercent + '%' }"></view>
        </view>
        <text class="progress-tip">{{ studyPercent >= 100 ? '已完成学习' : '阅读中，自动记录进度...' }}</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-text">内容不存在或已删除</text>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="content" class="bottom-bar">
      <view class="bottom-action" @tap="toggleFavorite">
        <text class="action-icon">{{ isFavorited ? '⭐' : '☆' }}</text>
        <text class="action-text">{{ isFavorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="bottom-action" @tap="shareContent">
        <text class="action-icon">📤</text>
        <text class="action-text">分享</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { learn } from '@/api/index.js'

// 内容数据
const content = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
let contentId = null // 当前内容ID，用于收藏和进度上报

// 学习进度相关
const studyDuration = ref(0) // 已学习时长（秒）
const studyPercent = ref(0) // 学习进度百分比
const reportInterval = ref(null) // 上报定时器
const scrollHandler = ref(null) // 滚动事件处理

// mp-html 标签样式
const tagStyle = reactive({
  p: 'font-size: 30rpx; line-height: 1.8; color: #333; margin-bottom: 20rpx;',
  h1: 'font-size: 40rpx; font-weight: bold; margin: 30rpx 0 20rpx; color: #222;',
  h2: 'font-size: 36rpx; font-weight: bold; margin: 28rpx 0 16rpx; color: #222;',
  h3: 'font-size: 32rpx; font-weight: bold; margin: 24rpx 0 14rpx; color: #333;',
  img: 'max-width: 100%; border-radius: 8rpx; margin: 16rpx 0;',
  code: 'background: #f5f5f5; padding: 2rpx 8rpx; border-radius: 4rpx; font-size: 26rpx; color: #c62828;',
  pre: 'background: #f5f5f5; padding: 20rpx; border-radius: 8rpx; overflow-x: auto; margin: 16rpx 0;',
  blockquote: 'border-left: 6rpx solid #2979ff; padding-left: 20rpx; margin: 16rpx 0; color: #666; background: #f9f9f9; padding: 16rpx 20rpx;',
  table: 'width: 100%; border-collapse: collapse; margin: 16rpx 0;',
  th: 'border: 1rpx solid #ddd; padding: 10rpx; background: #f5f5f5; font-weight: bold;',
  td: 'border: 1rpx solid #ddd; padding: 10rpx;'
})

/**
 * 页面加载
 */
onLoad((options) => {
  const id = options.id
  if (id) {
    contentId = id
    loadContent(id)
  }
})

/**
 * 加载内容详情
 */
const loadContent = async (id) => {
  loading.value = true
  try {
    // contentType=1 为文章
    const res = await learn.getContentDetail(1, id)
    content.value = res
    isFavorited.value = res.isFavorited || false
    
    // 恢复已有进度
    if (res.progress) {
      studyPercent.value = res.progress.percent || 0
      studyDuration.value = res.progress.duration || 0
    }

    // 启动进度上报
    startProgressReport()
  } catch (err) {
    console.error('加载内容详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 启动学习进度定时上报（每15秒上报一次）
 */
const startProgressReport = () => {
  // 清除已有定时器
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
  }

  reportInterval.value = setInterval(async () => {
    studyDuration.value += 15 // 增加15秒学习时长

    // 根据滚动位置计算阅读进度（文章类按时间计算，最多100%）
    const percentByTime = Math.min(100, Math.floor((studyDuration.value / 300) * 100))
    studyPercent.value = Math.max(studyPercent.value, percentByTime)

    // 上报进度
    try {
      await learn.reportProgress({
        contentType: 1,
        contentId: contentId,
        progress: studyPercent.value,
        studyDuration: studyDuration.value
      })
    } catch (err) {
      console.error('进度上报失败:', err)
    }
  }, 15000) // 15秒
}

/**
 * 切换收藏
 */
const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await learn.removeFavorite({ contentType: 1, contentId: contentId })
      isFavorited.value = false
      uni.showToast({ title: '已取消收藏', icon: 'none' })
    } else {
      await learn.addFavorite({ contentType: 1, contentId: contentId })
      isFavorited.value = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (err) {
    console.error('收藏操作失败:', err)
  }
}

/**
 * 分享内容
 */
const shareContent = async () => {
  const shareUrl = window.location.href
  const shareTitle = content.value?.title || '智慧护理学习内容'
  const shareText = `${shareTitle} - 智慧护理学员端`

  // 优先使用 Web Share API（移动端浏览器支持）
  if (navigator.share) {
    try {
      await navigator.share({
        title: shareTitle,
        text: shareText,
        url: shareUrl
      })
    } catch (err) {
      // 用户取消分享，不做处理
    }
  } else {
    // 降级：复制链接到剪贴板
    try {
      await navigator.clipboard.writeText(shareUrl)
      uni.showToast({ title: '链接已复制', icon: 'success' })
    } catch (err) {
      uni.setClipboardData({
        data: shareUrl,
        success: () => {
          uni.showToast({ title: '链接已复制', icon: 'success' })
        }
      })
    }
  }
}

/**
 * mp-html 链接点击
 */
const onLinkTap = (e) => {
  const href = e.detail.href
  if (href) {
    // #ifdef H5
    window.open(href)
    // #endif
    // #ifndef H5
    uni.setClipboardData({
      data: href,
      success: () => {
        uni.showToast({ title: '链接已复制', icon: 'none' })
      }
    })
    // #endif
  }
}

/**
 * mp-html 图片点击
 */
const onImgTap = (e) => {
  const src = e.detail.src
  if (src) {
    uni.previewImage({
      urls: [src],
      current: src
    })
  }
}

/**
 * 格式化时间
 */
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

// 页面卸载时清除定时器
onUnmounted(() => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
    reportInterval.value = null
  }
})
</script>

<style lang="scss" scoped>
.article-detail-page {
  min-height: 100vh;
  background: #fff;
  padding-bottom: 120rpx;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
  color: #999;
}

.article-content {
  .article-header {
    padding: 32rpx 32rpx 20rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .article-title {
      font-size: 40rpx;
      font-weight: bold;
      color: #222;
      line-height: 1.4;
      display: block;
    }

    .article-meta {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      margin-top: 20rpx;

      .meta-item {
        font-size: 24rpx;
        color: #999;
      }

      .meta-divider {
        font-size: 24rpx;
        color: #ddd;
        margin: 0 12rpx;
      }
    }
  }

  .article-body {
    padding: 32rpx;
  }

  .progress-section {
    margin: 20rpx 32rpx 40rpx;
    padding: 24rpx;
    background: #f9f9f9;
    border-radius: 12rpx;

    .progress-info {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .progress-label {
        font-size: 26rpx;
        color: #666;
      }

      .progress-percent {
        font-size: 28rpx;
        color: #2979ff;
        font-weight: bold;
      }
    }

    .progress-bar {
      width: 100%;
      height: 12rpx;
      background: #e0e0e0;
      border-radius: 6rpx;
      overflow: hidden;
      margin: 16rpx 0 10rpx;

      .progress-inner {
        height: 100%;
        background: linear-gradient(90deg, #2979ff, #42a5f5);
        border-radius: 6rpx;
        transition: width 0.5s;
      }
    }

    .progress-tip {
      font-size: 22rpx;
      color: #bbb;
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
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
  padding: 16rpx 0;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  z-index: 100;

  .bottom-action {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

    .action-icon {
      font-size: 40rpx;
    }

    .action-text {
      font-size: 22rpx;
      color: #666;
      margin-top: 6rpx;
    }
  }
}
</style>
