<template>
  <view class="video-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="content">
      <!-- 视频播放器：外部嵌入视频用 iframe，本地 mp4 用 video -->
      <view class="video-player-wrapper">
        <!-- 外部嵌入视频（B站等） -->
        <iframe
          v-if="isExternalVideo"
          :src="content.videoUrl"
          class="video-iframe"
          frameborder="0"
          allowfullscreen="true"
          scrolling="no"
          allow="autoplay; fullscreen; encrypted-media; picture-in-picture"
        ></iframe>
        <!-- 本地视频文件 -->
        <video
          v-else
          id="videoPlayer"
          ref="videoPlayer"
          class="video-player"
          :src="content.videoUrl"
          :poster="content.coverImage"
          :controls="true"
          :show-center-play-btn="true"
          :enable-progress-gesture="true"
          :show-fullscreen-btn="true"
          :object-fit="'contain'"
          @play="onPlay"
          @pause="onPause"
          @ended="onEnded"
          @timeupdate="onTimeUpdate"
          @fullscreenchange="onFullscreenChange"
          @error="onVideoError"
        ></video>
      </view>

      <!-- 视频信息 -->
      <view class="video-info">
        <text class="video-title">{{ content.title }}</text>
        <view class="video-meta">
          <text class="meta-item">{{ content.categoryName || '未分类' }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ formatDuration(content.duration) }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ content.viewCount || 0 }}次学习</text>
        </view>
      </view>

      <!-- 学习进度信息 -->
      <view class="progress-section">
        <view class="progress-header">
          <text class="progress-label">学习进度</text>
          <text class="progress-percent">{{ studyPercent }}%</text>
        </view>
        <view class="progress-bar">
          <view class="progress-inner" :style="{ width: studyPercent + '%' }"></view>
        </view>
        <view class="progress-detail">
          <text class="detail-text">视频时长: {{ formatDuration(content.duration) }}</text>
          <text class="detail-text">累计学习: {{ formatDuration(totalWatchedDuration) }}</text>
        </view>
      </view>

      <!-- 视频简介 -->
      <view v-if="content.summary || content.description" class="video-desc">
        <text class="desc-title">简介</text>
        <text class="desc-content">{{ content.summary || content.description }}</text>
      </view>

      <!-- 外部视频提示 -->
      <view v-if="isExternalVideo" class="external-tip">
        <text class="tip-icon">📺</text>
        <text class="tip-text">本视频为在线嵌入播放，请在播放器中观看。学习时长将自动记录。</text>
      </view>

      <!-- 防刷提示 -->
      <view v-else class="anti-cheat-tip">
        <text class="tip-icon">🔒</text>
        <text class="tip-text">学习进度由系统自动记录，快进或跳过不影响有效学习时长</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-text">视频不存在或已删除</text>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="content" class="bottom-bar">
      <view class="bottom-action" @tap="toggleFavorite">
        <text class="action-icon">{{ isFavorited ? '⭐' : '☆' }}</text>
        <text class="action-text">{{ isFavorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view v-if="!isExternalVideo" class="bottom-action" @tap="toggleFullscreen">
        <text class="action-icon">⛶</text>
        <text class="action-text">全屏</text>
      </view>
      <view v-if="isExternalVideo" class="bottom-action" @tap="openInNewTab">
        <text class="action-icon">🔗</text>
        <text class="action-text">新窗口</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { learn } from '@/api/index.js'

// 内容数据
const content = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
let contentId = null

// 判断是否为外部嵌入视频（非 .mp4 本地文件）
const isExternalVideo = computed(() => {
  if (!content.value || !content.value.videoUrl) return false
  const url = content.value.videoUrl
  // 本地视频文件以 /upload/ 开头或以 .mp4/.webm 结尾
  return !url.endsWith('.mp4') && !url.endsWith('.webm') && !url.endsWith('.m4v')
})

// 视频播放状态
const videoPlayer = ref(null)
const currentTime = ref(0)
const totalTime = ref(0)
const totalWatchedDuration = ref(0)
const isPlaying = ref(false)
const lastReportTime = ref(0)
const reportInterval = ref(null)
const videoContext = ref(null)

// 学习进度百分比
const studyPercent = ref(0)

/**
 * 页面加载
 */
onLoad((options) => {
  const id = options?.id
  if (id) {
    contentId = id
    loadContent(id)
  } else {
    // #ifdef H5
    const hashParams = window.location.hash.split('?')[1]
    const hashId = hashParams ? new URLSearchParams(hashParams).get('id') : null
    if (hashId) {
      contentId = hashId
      loadContent(hashId)
    } else {
      loading.value = false
      uni.showToast({ title: '参数缺失', icon: 'none' })
    }
    // #endif
    // #ifndef H5
    loading.value = false
    // #endif
  }
})

/**
 * 加载视频内容详情
 */
const loadContent = async (id) => {
  loading.value = true
  try {
    const res = await learn.getContentDetail(2, id)
    if (!res) {
      content.value = null
      return
    }
    content.value = res
    isFavorited.value = res.isFavorited || false
    totalTime.value = res.duration || 0

    // 恢复已有进度
    if (res.progress) {
      studyPercent.value = res.progress.percent || 0
      totalWatchedDuration.value = res.progress.duration || 0
    }

    // 延迟获取 video 上下文（仅本地视频需要）
    if (!isExternalVideo.value) {
      setTimeout(() => {
        videoContext.value = uni.createVideoContext('videoPlayer')
      }, 300)
    }

    // 启动进度上报
    startProgressReport()
  } catch (err) {
    console.error('加载视频详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 启动进度定时上报（每15秒上报一次）
 */
const startProgressReport = () => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
  }

  reportInterval.value = setInterval(async () => {
    if (isExternalVideo.value) {
      // 外部嵌入视频：按页面停留时间累计学习时长
      totalWatchedDuration.value += 15
      // 按累计时长占总时长的比例计算进度
      if (totalTime.value > 0) {
        const percent = Math.min(100, Math.floor((totalWatchedDuration.value / totalTime.value) * 100))
        studyPercent.value = Math.max(studyPercent.value, percent)
      }
      // 上报进度
      try {
        await learn.reportProgress({
          contentType: 2,
          contentId: contentId,
          progress: studyPercent.value,
          studyDuration: totalWatchedDuration.value
        })
      } catch (err) {
        console.error('进度上报失败:', err)
      }
    } else {
      // 本地视频：仅播放时才累计
      if (!isPlaying.value) return

      if (currentTime.value <= lastReportTime.value) {
        return
      }

      const watchedThisRound = currentTime.value - lastReportTime.value
      if (watchedThisRound > 0 && watchedThisRound <= 30) {
        totalWatchedDuration.value += watchedThisRound
      }

      lastReportTime.value = currentTime.value

      if (totalTime.value > 0) {
        studyPercent.value = Math.min(100, Math.floor((currentTime.value / totalTime.value) * 100))
      }

      try {
        await learn.reportProgress({
          contentType: 2,
          contentId: contentId,
          progress: studyPercent.value,
          studyDuration: totalWatchedDuration.value
        })
      } catch (err) {
        console.error('进度上报失败:', err)
      }
    }
  }, 15000)
}

// =============================================
// 本地视频事件处理
// =============================================

const onPlay = () => {
  isPlaying.value = true
  lastReportTime.value = currentTime.value
}

const onPause = () => {
  isPlaying.value = false
  if (contentId) {
    learn.reportProgress({
      contentType: 2,
      contentId: contentId,
      progress: studyPercent.value,
      studyDuration: totalWatchedDuration.value
    }).catch(() => {})
  }
}

const onEnded = () => {
  isPlaying.value = false
  studyPercent.value = 100
  if (contentId) {
    learn.reportProgress({
      contentType: 2,
      contentId: contentId,
      progress: 100,
      studyDuration: totalWatchedDuration.value
    }).catch(() => {})
  }
  uni.showToast({ title: '视频学习完成', icon: 'success' })
}

const onTimeUpdate = (e) => {
  currentTime.value = Math.floor(e.detail.currentTime || 0)
  totalTime.value = Math.floor(e.detail.duration || totalTime.value)

  if (totalTime.value > 0) {
    const percent = Math.min(100, Math.floor((currentTime.value / totalTime.value) * 100))
    if (percent > studyPercent.value) {
      studyPercent.value = percent
    }
  }
}

const onFullscreenChange = (e) => {
  console.log('全屏状态变化:', e.detail.fullScreen)
}

const onVideoError = (e) => {
  console.error('视频播放错误:', e)
  uni.showToast({ title: '视频加载失败', icon: 'none' })
}

const toggleFullscreen = () => {
  if (videoContext.value) {
    videoContext.value.enterFullScreen()
  }
}

/**
 * 外部视频在新窗口打开
 */
const openInNewTab = () => {
  const url = content.value?.videoUrl || ''
  if (!url) return
  // #ifdef H5
  window.open(url, '_blank')
  // #endif
}

/**
 * 切换收藏
 */
const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await learn.removeFavorite({ contentType: 2, contentId: contentId })
      isFavorited.value = false
      uni.showToast({ title: '已取消收藏', icon: 'none' })
    } else {
      await learn.addFavorite({ contentType: 2, contentId: contentId })
      isFavorited.value = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (err) {
    console.error('收藏操作失败:', err)
  }
}

/**
 * 格式化时长（秒转 mm:ss 或 hh:mm:ss）
 */
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const hours = Math.floor(seconds / 3600)
  const min = Math.floor((seconds % 3600) / 60)
  const sec = seconds % 60
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
  }
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

// 页面卸载时清除定时器并上报
onUnload(() => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
    reportInterval.value = null
  }
  // 离开页面前最后上报一次
  if (contentId) {
    learn.reportProgress({
      contentType: 2,
      contentId: contentId,
      progress: studyPercent.value,
      studyDuration: totalWatchedDuration.value
    }).catch(() => {})
  }
})
</script>

<style lang="scss" scoped>
.video-detail-page {
  min-height: 100vh;
  background: #F8FAFC;
  padding-bottom: 120rpx;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
  color: #989FA6;
}

/* 视频播放器 - 16:9 比例自适应 */
.video-player-wrapper {
  width: 100%;
  background: #000;
  position: relative;
  padding-bottom: 56.25%; /* 16:9 比例 */
  height: 0;
  overflow: hidden;

  .video-player {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
  }

  .video-iframe {
    width: 100%;
    height: 100%;
    border: none;
    display: block;
    position: absolute;
    top: 0;
    left: 0;
  }
}

/* 视频信息 */
.video-info {
  background: #FFFFFF;
  padding: 28rpx 32rpx;

  .video-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #3A4C56;
    line-height: 1.4;
    display: block;
  }

  .video-meta {
    display: flex;
    align-items: center;
    margin-top: 16rpx;

    .meta-item {
      font-size: 24rpx;
      color: #989FA6;
    }

    .meta-divider {
      font-size: 24rpx;
      color: #E8E4DE;
      margin: 0 12rpx;
    }
  }
}

/* 学习进度 */
.progress-section {
  background: #FFFFFF;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .progress-label {
      font-size: 28rpx;
      font-weight: 600;
      color: #3A4C56;
    }

    .progress-percent {
      font-size: 32rpx;
      color: #0EA5E9;
      font-weight: bold;
    }
  }

  .progress-bar {
    width: 100%;
    height: 16rpx;
    background: #E0F2FE;
    border-radius: 8rpx;
    overflow: hidden;
    margin: 20rpx 0 16rpx;

    .progress-inner {
      height: 100%;
      background: #0EA5E9;
      border-radius: 8rpx;
      transition: width 0.4s ease-out;
    }
  }

  .progress-detail {
    display: flex;
    justify-content: space-between;

    .detail-text {
      font-size: 22rpx;
      color: #989FA6;
    }
  }
}

/* 视频简介 */
.video-desc {
  background: #FFFFFF;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .desc-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #3A4C56;
    display: block;
    margin-bottom: 16rpx;
  }

  .desc-content {
    font-size: 28rpx;
    color: #636A70;
    line-height: 1.7;
  }
}

/* 外部视频提示 */
.external-tip {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  margin-top: 16rpx;
  background: #e3f2fd;

  .tip-icon {
    font-size: 28rpx;
    margin-right: 12rpx;
  }

  .tip-text {
    font-size: 22rpx;
    color: #1976d2;
    flex: 1;
  }
}

/* 防刷提示 */
.anti-cheat-tip {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  margin-top: 16rpx;

  .tip-icon {
    font-size: 28rpx;
    margin-right: 12rpx;
  }

  .tip-text {
    font-size: 22rpx;
    color: #bbb;
    flex: 1;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx 0;

  .empty-text {
    font-size: 28rpx;
    color: #989FA6;
  }
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: #FFFFFF;
  border-top: 1rpx solid #E8E4DE;
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
      color: #636A70;
      margin-top: 6rpx;
    }
  }
}
</style>
