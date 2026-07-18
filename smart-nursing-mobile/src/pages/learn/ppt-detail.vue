<template>
  <view class="ppt-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrapper">
      <text>加载中...</text>
    </view>

    <view v-else-if="content" class="ppt-content">
      <!-- PPT 预览区 -->
      <view class="ppt-preview">
        <view class="ppt-cover">
          <view class="ppt-icon-large">
            <text class="icon-text">📊</text>
          </view>
          <text class="ppt-page-count">{{ content.pageCount || 0 }} 页</text>
        </view>
        <view class="ppt-actions">
          <button class="action-btn btn-view" @tap="viewOnline">
            <text class="btn-icon">👁</text>
            <text class="btn-text">在线查看</text>
          </button>
          <button class="action-btn btn-download" @tap="downloadPpt" :disabled="downloading">
            <text class="btn-icon">⬇</text>
            <text class="btn-text">{{ downloading ? '下载中...' : '下载课件' }}</text>
          </button>
        </view>
      </view>

      <!-- PPT 信息 -->
      <view class="ppt-info">
        <text class="ppt-title">{{ content.title }}</text>
        <view class="ppt-meta">
          <text class="meta-item">{{ content.categoryName || '未分类' }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ content.author || '佚名' }}</text>
          <text class="meta-divider">|</text>
          <text class="meta-item">{{ formatTime(content.createTime) }}</text>
        </view>
        <view class="ppt-stats">
          <view class="stat-item">
            <text class="stat-value">{{ content.pageCount || 0 }}</text>
            <text class="stat-label">页数</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ formatFileSize(content.fileSize) }}</text>
            <text class="stat-label">大小</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ content.viewCount || 0 }}</text>
            <text class="stat-label">学习次数</text>
          </view>
        </view>
      </view>

      <!-- PPT 描述 -->
      <view v-if="content.summary || content.description" class="ppt-desc">
        <text class="desc-title">课件简介</text>
        <text class="desc-content">{{ content.summary || content.description }}</text>
      </view>

      <!-- 学习进度 -->
      <view class="progress-section">
        <view class="progress-info">
          <text class="progress-label">学习进度</text>
          <text class="progress-percent">{{ studyPercent }}%</text>
        </view>
        <view class="progress-bar">
          <view class="progress-inner" :style="{ width: studyPercent + '%' }"></view>
        </view>
        <text class="progress-tip">{{ studyPercent >= 100 ? '已完成学习' : '查看课件将自动记录学习进度' }}</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-text">课件不存在或已删除</text>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="content" class="bottom-bar">
      <view class="bottom-action" @tap="toggleFavorite">
        <text class="action-icon">{{ isFavorited ? '⭐' : '☆' }}</text>
        <text class="action-text">{{ isFavorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="bottom-action" @tap="viewOnline">
        <text class="action-icon">📖</text>
        <text class="action-text">查看</text>
      </view>
      <view class="bottom-action" @tap="downloadPpt">
        <text class="action-icon">⬇</text>
        <text class="action-text">下载</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { learn } from '@/api/index.js'

// 内容数据
const content = ref(null)
const loading = ref(true)
const isFavorited = ref(false)
const downloading = ref(false)
let contentId = null // 当前内容ID

// 学习进度
const studyPercent = ref(0)
const studyDuration = ref(0)
const reportInterval = ref(null)

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
 * 加载 PPT 内容详情
 */
const loadContent = async (id) => {
  loading.value = true
  try {
    // contentType=3 为课件
    const res = await learn.getContentDetail(3, id)
    if (!res) {
      content.value = null
      return
    }
    content.value = res
    isFavorited.value = res.isFavorited || false

    if (res.progress) {
      studyPercent.value = res.progress.percent || 0
      studyDuration.value = res.progress.duration || 0
    }

    // 启动进度上报
    startProgressReport()
  } catch (err) {
    console.error('加载课件详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 启动进度定时上报（每15秒）
 */
const startProgressReport = () => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
  }

  reportInterval.value = setInterval(async () => {
    studyDuration.value += 15

    // PPT 类内容按时间计算进度，30分钟为100%
    const percentByTime = Math.min(100, Math.floor((studyDuration.value / 1800) * 100))
    studyPercent.value = Math.max(studyPercent.value, percentByTime)

    try {
      await learn.reportProgress({
        contentType: 3,
        contentId: contentId,
        progress: studyPercent.value,
        studyDuration: studyDuration.value
      })
    } catch (err) {
      console.error('进度上报失败:', err)
    }
  }, 15000)
}

/**
 * 在线查看 PPT
 */
const viewOnline = () => {
  const fileUrl = content.value.fileUrl || content.value.viewUrl
  if (!fileUrl) {
    uni.showToast({ title: '暂无在线查看地址', icon: 'none' })
    return
  }

  // #ifdef H5
  window.open(fileUrl, '_blank')
  // #endif

  // #ifndef H5
  // App 端使用 web-view 或文档预览
  uni.navigateTo({
    url: `/pages/learn/ppt-detail?viewUrl=${encodeURIComponent(fileUrl)}`
  })
  // 也可以使用 uni.openDocument 打开
  uni.showLoading({ title: '打开中...' })
  uni.downloadFile({
    url: fileUrl,
    success: (res) => {
      uni.hideLoading()
      if (res.statusCode === 200) {
        uni.openDocument({
          filePath: res.tempFilePath,
          success: () => {},
          fail: () => {
            uni.showToast({ title: '无法打开此格式文件', icon: 'none' })
          }
        })
      }
    },
    fail: () => {
      uni.hideLoading()
      uni.showToast({ title: '打开失败', icon: 'none' })
    }
  })
  // #endif

  // 标记为已开始学习
  if (studyPercent.value === 0) {
    studyPercent.value = 10
  }
}

/**
 * 下载 PPT
 */
const downloadPpt = () => {
  const fileUrl = content.value.fileUrl || content.value.downloadUrl
  if (!fileUrl) {
    uni.showToast({ title: '暂无下载地址', icon: 'none' })
    return
  }

  if (downloading.value) return
  downloading.value = true

  // #ifdef H5
  // H5 环境直接通过链接下载
  const link = document.createElement('a')
  link.href = fileUrl
  link.download = content.value.title + '.pptx'
  link.click()
  downloading.value = false
  uni.showToast({ title: '开始下载', icon: 'success' })
  // #endif

  // #ifndef H5
  // App / 小程序环境使用 downloadFile
  uni.showLoading({ title: '下载中...', mask: true })
  uni.downloadFile({
    url: fileUrl,
    success: (res) => {
      uni.hideLoading()
      if (res.statusCode === 200) {
        // 保存文件
        uni.saveFile({
          tempFilePath: res.tempFilePath,
          success: (saveRes) => {
            uni.showToast({ title: '下载成功', icon: 'success' })
            // 可选：打开文件
            uni.openDocument({
              filePath: saveRes.savedFilePath,
              showMenu: true
            })
          },
          fail: () => {
            // 保存失败，直接打开临时文件
            uni.openDocument({
              filePath: res.tempFilePath,
              showMenu: true
            })
          }
        })
      }
    },
    fail: () => {
      uni.hideLoading()
      uni.showToast({ title: '下载失败', icon: 'none' })
    },
    complete: () => {
      downloading.value = false
    }
  })
  // #endif
}

/**
 * 切换收藏
 */
const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await learn.removeFavorite({ contentType: 3, contentId: contentId })
      isFavorited.value = false
      uni.showToast({ title: '已取消收藏', icon: 'none' })
    } else {
      await learn.addFavorite({ contentType: 3, contentId: contentId })
      isFavorited.value = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (err) {
    console.error('收藏操作失败:', err)
  }
}

/**
 * 格式化文件大小
 */
const formatFileSize = (bytes) => {
  if (!bytes) return '未知'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + 'GB'
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
onUnload(() => {
  if (reportInterval.value) {
    clearInterval(reportInterval.value)
    reportInterval.value = null
  }
})
</script>

<style lang="scss" scoped>
.ppt-detail-page {
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

/* PPT 预览区 */
.ppt-preview {
  background: #FFFFFF;
  padding: 40rpx 32rpx;

  .ppt-cover {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 60rpx 0;

    .ppt-icon-large {
      width: 160rpx;
      height: 160rpx;
      border-radius: 24rpx;
      background: linear-gradient(135deg, #ff9800, #f57c00);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 24rpx;

      .icon-text {
        font-size: 72rpx;
      }
    }

    .ppt-page-count {
      font-size: 26rpx;
      color: #989FA6;
    }
  }

  .ppt-actions {
    display: flex;
    gap: 20rpx;
    margin-top: 20rpx;

    .action-btn {
      flex: 1;
      height: 80rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 40rpx;
      font-size: 28rpx;
      border: none;

      &::after {
        border: none;
      }

      .btn-icon {
        margin-right: 10rpx;
        font-size: 30rpx;
      }

      &.btn-view {
        background: #E0F2FE;
        color: #0EA5E9;
      }

      &.btn-download {
        background: linear-gradient(135deg, #0EA5E9, #0284C7);
        color: #FFFFFF;

        &[disabled] {
          opacity: 0.6;
        }
      }
    }
  }
}

/* PPT 信息 */
.ppt-info {
  background: #FFFFFF;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .ppt-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #3A4C56;
    line-height: 1.4;
    display: block;
  }

  .ppt-meta {
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

  .ppt-stats {
    display: flex;
    align-items: center;
    justify-content: space-around;
    background: #f9f9f9;
    border-radius: 24rpx;
    padding: 24rpx 0;
    margin-top: 24rpx;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .stat-value {
        font-size: 36rpx;
        font-weight: bold;
        color: #3A4C56;
      }

      .stat-label {
        font-size: 22rpx;
        color: #989FA6;
        margin-top: 6rpx;
      }
    }

    .stat-divider {
      width: 1rpx;
      height: 48rpx;
      background: #E8E4DE;
    }
  }
}

/* PPT 描述 */
.ppt-desc {
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

/* 学习进度 */
.progress-section {
  background: #FFFFFF;
  margin-top: 16rpx;
  padding: 28rpx 32rpx;

  .progress-info {
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
    margin: 20rpx 0 10rpx;

    .progress-inner {
      height: 100%;
      background: #0EA5E9;
      border-radius: 8rpx;
      transition: width 0.4s ease-out;
    }
  }

  .progress-tip {
    font-size: 22rpx;
    color: #bbb;
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
