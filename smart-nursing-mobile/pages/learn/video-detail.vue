<template>
  <view class="video-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="content">
      <!-- 视频播放器 -->
      <view class="video-player-wrapper">
        <video
          id="videoPlayer"
          ref="videoPlayer"
          class="video-player"
          :src="content.videoUrl"
          :poster="content.coverUrl"
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
          <text class="detail-text">已观看: {{ formatDuration(currentTime) }} / {{ formatDuration(content.duration) }}</text>
          <text class="detail-text">累计学习: {{ formatDuration(totalWatchedDuration) }}</text>
        </view>
      </view>

      <!-- 视频简介 -->
      <view v-if="content.summary || content.description" class="video-desc">
        <text class="desc-title">简介</text>
        <text class="desc-content">{{ content.summary || content.description }}</text>
      </view>

      <!-- 防刷提示 -->
      <view class="anti-cheat-tip">
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
      <view class="bottom-action" @tap="toggleFullscreen">
        <text class="action-icon">⛶</text>
        <text class="action-text">全屏</text>
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

// 视频播放状态
const videoPlayer = ref(null)
const currentTime = ref(0) // 当前播放时间（秒）
const totalTime = ref(0) // 视频总时长（秒）
const totalWatchedDuration = ref(0) // 累计有效观看时长（秒）
const isPlaying = ref(false)
const lastReportTime = ref(0) // 上次上报的播放时间
const reportInterval = ref(null) // 上报定时器
const videoContext = ref(null) // video 上下文

// 学习进度百分比
const studyPercent = ref(0)

/**
 * 页面加载
 */
onLoad((options) => {
  const id = options.id
  if (id) {
    loadContent(id)
  }
})

/**
 * 加载视频内容详情
 */
const loadContent = async (id) => {
  loading.value = true
  try {
    const res = await learn.getContentDetail(id)
    content.value = res
    isFavorited.value = res.isFavorited || false
    totalTime.value = res.duration || 0

    // 恢复已有进度
    if (res.progress) {
      studyPercent.value = res.progress.percent || 0
      totalWatchedDuration.value = res.progress.duration || 0
      lastReportTime.value = res.progress.currentTime || 0
    }

    // 获取 video 上下文
    videoContext.value = uni.createVideoContext('videoPlayer')

    // 启动进度上报
    startProgressReport(id)
  } catch (err) {
    console.error('加载视频详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 启动进度定时上报（每15秒上报一次）
 * 防刷逻辑：后端校验 currentTime 必须递增
 */
const startProgressReport = (contentId) => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
  }

  reportInterval.value = setInterval(async () => {
    // 只有在播放状态下才上报
    if (!isPlaying.value) return

    // 防刷校验：currentTime 必须大于上次上报的时间
    // 后端会校验 currentTime 递增，防止快进刷时长
    if (currentTime.value <= lastReportTime.value) {
      console.warn('播放进度异常，跳过本次上报')
      return
    }

    // 累计有效观看时长（本次播放时间 - 上次播放时间 = 15秒）
    const watchedThisRound = currentTime.value - lastReportTime.value
    // 正常播放每轮约15秒，如果超过30秒说明可能快进，不做累计
    if (watchedThisRound > 0 && watchedThisRound <= 30) {
      totalWatchedDuration.value += watchedThisRound
    }

    // 更新上次上报时间
    lastReportTime.value = currentTime.value

    // 计算进度百分比
    if (totalTime.value > 0) {
      studyPercent.value = Math.min(100, Math.floor((currentTime.value / totalTime.value) * 100))
    }

    // 上报进度
    try {
      await learn.reportProgress({
        contentId: contentId,
        duration: totalWatchedDuration.value,
        currentTime: currentTime.value,
        progress: studyPercent.value,
        type: 'video'
      })
    } catch (err) {
      console.error('进度上报失败:', err)
    }
  }, 15000) // 每15秒上报一次
}

// =============================================
// 视频事件处理
// =============================================

/**
 * 播放
 */
const onPlay = () => {
  isPlaying.value = true
  // 记录开始播放的时间点
  lastReportTime.value = currentTime.value
}

/**
 * 暂停
 */
const onPause = () => {
  isPlaying.value = false
  // 暂停时也上报一次当前进度
  if (content.value) {
    learn.reportProgress({
      contentId: content.value.id,
      duration: totalWatchedDuration.value,
      currentTime: currentTime.value,
      progress: studyPercent.value,
      type: 'video'
    }).catch(() => {})
  }
}

/**
 * 播放结束
 */
const onEnded = () => {
  isPlaying.value = false
  studyPercent.value = 100
  // 上报完成进度
  if (content.value) {
    learn.reportProgress({
      contentId: content.value.id,
      duration: totalWatchedDuration.value,
      currentTime: totalTime.value,
      progress: 100,
      type: 'video',
      completed: true
    }).catch(() => {})
  }
  uni.showToast({ title: '视频学习完成', icon: 'success' })
}

/**
 * 播放进度更新
 */
const onTimeUpdate = (e) => {
  currentTime.value = Math.floor(e.detail.currentTime || 0)
  totalTime.value = Math.floor(e.detail.duration || totalTime.value)

  // 实时更新进度条
  if (totalTime.value > 0) {
    const percent = Math.min(100, Math.floor((currentTime.value / totalTime.value) * 100))
    // 进度只增不减
    if (percent > studyPercent.value) {
      studyPercent.value = percent
    }
  }
}

/**
 * 全屏状态变化
 */
const onFullscreenChange = (e) => {
  console.log('全屏状态变化:', e.detail.fullScreen)
}

/**
 * 视频错误
 */
const onVideoError = (e) => {
  console.error('视频播放错误:', e)
  uni.showToast({ title: '视频加载失败', icon: 'none' })
}

/**
 * 切换全屏
 */
const toggleFullscreen = () => {
  if (videoContext.value) {
    videoContext.value.enterFullScreen()
  }
}

/**
 * 切换收藏
 */
const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await learn.removeFavorite(content.value.id)
      isFavorited.value = false
      uni.showToast({ title: '已取消收藏', icon: 'none' })
    } else {
      await learn.addFavorite({ contentId: content.value.id })
      isFavorited.value = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (err) {
    console.error('收藏操作失败:', err)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 格式化时长（秒转 mm:ss）
 */
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const min = Math.floor(seconds / 60)
  const sec = seconds % 60
  return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

// 页面卸载时清除定时器
onUnmounted(() => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
    reportInterval.value = null
  }
  // 离开页面前最后上报一次
  if (content.value && isPlaying.value) {
    learn.reportProgress({
      contentId: content.value.id,
      duration: totalWatchedDuration.value,
      currentTime: currentTime.value,
      progress: studyPercent.value,
      type: 'video'
    }).catch(() => {})
  }
})
</script>

<style lang="scss" scoped>
.video-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40vh;
  color: #999;
}

/* 视频播放器 */
.video-player-wrapper {
  width: 100%;
  background: #000;

  .video-player {
    width: 100%;
    height: 420rpx;
  }
}

/* 视频信息 */
.video-info {
  background: #fff;
  padding: 28rpx 32rpx;

  .video-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #222;
    line-height: 1.4;
    display: block;
  }

  .video-meta {
    display: flex;
    align-items: center;
    margin-top: 16rpx;

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

/* 学习进度 */
.progress-section {
  background: #fff;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .progress-label {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;
    }

    .progress-percent {
      font-size: 32rpx;
      color: #2979ff;
      font-weight: bold;
    }
  }

  .progress-bar {
    width: 100%;
    height: 16rpx;
    background: #f0f0f0;
    border-radius: 8rpx;
    overflow: hidden;
    margin: 20rpx 0 16rpx;

    .progress-inner {
      height: 100%;
      background: linear-gradient(90deg, #2979ff, #42a5f5);
      border-radius: 8rpx;
      transition: width 0.5s;
    }
  }

  .progress-detail {
    display: flex;
    justify-content: space-between;

    .detail-text {
      font-size: 22rpx;
      color: #999;
    }
  }
}

/* 视频简介 */
.video-desc {
  background: #fff;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .desc-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333;
    display: block;
    margin-bottom: 16rpx;
  }

  .desc-content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.7;
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
