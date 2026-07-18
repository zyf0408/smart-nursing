<template>
  <view class="user-page">
    <!-- 顶部用户信息区 -->
    <view class="user-header">
      <view class="header-bg"></view>
      <view class="user-info-wrapper">
        <view class="avatar-wrapper" @tap="goProfile">
          <image
            v-if="userInfo.avatar"
            :src="userInfo.avatar"
            class="avatar"
            mode="aspectFill"
          />
          <view v-else class="avatar avatar-default">
            <text class="avatar-text">{{ userInfo.name ? userInfo.name.charAt(0) : '护' }}</text>
          </view>
        </view>
        <view class="user-detail">
          <text class="user-name">{{ userInfo.name || userInfo.username || '学员' }}</text>
          <view class="user-role">
            <text class="role-tag">{{ getRoleText(userInfo.role) }}</text>
            <text v-if="userInfo.className" class="class-name">{{ userInfo.className }}</text>
          </view>
        </view>
        <view class="edit-icon" @tap="goProfile">
          <text>✏️</text>
        </view>
      </view>
    </view>

    <!-- 学习进度统计 -->
    <view class="stats-section">
      <view class="stats-card">
        <view class="stats-title">
          <text class="title-text">学习进度</text>
          <text class="title-link" @tap="goLearningRecord">查看记录 ></text>
        </view>
        <view class="stats-content">
          <view class="stat-item">
            <text class="stat-value">{{ progress.totalContent || 0 }}</text>
            <text class="stat-label">总内容</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ progress.completedContent || 0 }}</text>
            <text class="stat-label">已完成</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ formatStudyTime(progress.totalDuration) }}</text>
            <text class="stat-label">学习时长</text>
          </view>
        </view>
        <!-- 进度条 -->
        <view class="progress-bar-wrapper">
          <view class="progress-bar">
            <view class="progress-inner" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <text class="progress-text">完成率 {{ progressPercent }}%</text>
        </view>
      </view>
    </view>

    <!-- 功能入口列表 -->
    <view class="menu-section">
      <view class="menu-card">
        <view class="menu-item" @tap="goFavorite">
          <view class="menu-icon icon-favorite">
            <text>⭐</text>
          </view>
          <text class="menu-text">我的收藏</text>
          <text class="menu-arrow">></text>
        </view>

        <view class="menu-item" @tap="goLearningRecord">
          <view class="menu-icon icon-record">
            <text>📖</text>
          </view>
          <text class="menu-text">学习记录</text>
          <text class="menu-arrow">></text>
        </view>

        <view class="menu-item" @tap="goExamRecord">
          <view class="menu-icon icon-exam">
            <text>📊</text>
          </view>
          <text class="menu-text">考试记录</text>
          <text class="menu-arrow">></text>
        </view>
      </view>

      <view class="menu-card">
        <view class="menu-item" @tap="goProfile">
          <view class="menu-icon icon-profile">
            <text>👤</text>
          </view>
          <text class="menu-text">修改个人信息</text>
          <text class="menu-arrow">></text>
        </view>

        <view class="menu-item" @tap="goChangePassword">
          <view class="menu-icon icon-password">
            <text>🔑</text>
          </view>
          <text class="menu-text">修改密码</text>
          <text class="menu-arrow">></text>
        </view>
      </view>

      <view class="menu-card">
        <view class="menu-item menu-logout" @tap="handleLogout">
          <text class="logout-text">退出登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { user } from '@/api/index.js'

// 用户信息
const userInfo = reactive({
  name: '',
  username: '',
  avatar: '',
  role: '',
  className: ''
})

// 学习进度
const progress = reactive({
  totalContent: 0,
  completedContent: 0,
  totalDuration: 0
})

// 完成率
const progressPercent = computed(() => {
  if (!progress.totalContent) return 0
  return Math.round((progress.completedContent / progress.totalContent) * 100)
})

/**
 * 加载用户信息
 */
const loadUserInfo = async () => {
  try {
    const res = await user.getUserInfo()
    Object.assign(userInfo, res)
    // 同步存储
    uni.setStorageSync('userInfo', JSON.stringify(res))
  } catch (err) {
    console.error('加载用户信息失败:', err)
    // 从本地存储恢复
    const stored = uni.getStorageSync('userInfo')
    if (stored) {
      try {
        Object.assign(userInfo, JSON.parse(stored))
      } catch (e) {}
    }
  }
}

/**
 * 加载学习进度
 */
const loadProgress = async () => {
  try {
    const res = await user.getProgress()
    Object.assign(progress, res)
  } catch (err) {
    console.error('加载学习进度失败:', err)
  }
}

/**
 * 格式化学习时长
 */
const formatStudyTime = (minutes) => {
  if (!minutes) return '0分'
  if (minutes < 60) return `${minutes}分`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours < 24) return `${hours}时${mins}分`
  const days = Math.floor(hours / 24)
  return `${days}天${hours % 24}时`
}

/**
 * 获取角色文字
 */
const getRoleText = (role) => {
  const map = {
    student: '学员',
    nurse: '护士',
    senior_nurse: '主管护师',
    head_nurse: '护士长',
    admin: '管理员'
  }
  return map[role] || '学员'
}

// 页面跳转
const goProfile = () => {
  uni.navigateTo({ url: '/pages/user/profile' })
}

const goChangePassword = () => {
  uni.navigateTo({ url: '/pages/user/change-password' })
}

const goFavorite = () => {
  uni.navigateTo({ url: '/pages/user/favorite' })
}

const goLearningRecord = () => {
  uni.navigateTo({ url: '/pages/user/learning-record' })
}

const goExamRecord = () => {
  uni.navigateTo({ url: '/pages/user/exam-record' })
}

/**
 * 退出登录
 */
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          // 尝试通知后端退出
          await import('@/api/index.js').then(m => m.auth.logout())
        } catch (e) {
          // 即使后端请求失败也清除本地状态
        }
        // 清除本地存储
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
    }
  })
}

// 页面显示时加载数据
onShow(() => {
  loadUserInfo()
  loadProgress()
})
</script>

<style lang="scss" scoped>
.user-page {
  min-height: 100vh;
  background: #F7F4EF;
  padding-bottom: 40rpx;
}

/* 顶部用户信息区 */
.user-header {
  position: relative;
  overflow: hidden;

  .header-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 320rpx;
    background: linear-gradient(135deg, #93B4B8, #84A7C6);
  }

  .user-info-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    padding: 120rpx 40rpx 60rpx;

    .avatar-wrapper {
      .avatar {
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
        border: 4rpx solid rgba(255, 255, 255, 0.5);
        box-shadow: 0 4rpx 16rpx rgba(58, 76, 86, 0.15);

        &.avatar-default {
          background: rgba(255, 255, 255, 0.3);
          display: flex;
          align-items: center;
          justify-content: center;

          .avatar-text {
            font-size: 48rpx;
            color: #fff;
            font-weight: bold;
          }
        }
      }
    }

    .user-detail {
      flex: 1;
      margin-left: 28rpx;

      .user-name {
        font-size: 36rpx;
        font-weight: bold;
        color: #fff;
        text-shadow: 0 2rpx 6rpx rgba(58, 76, 86, 0.2);
      }

      .user-role {
        display: flex;
        align-items: center;
        margin-top: 12rpx;

        .role-tag {
          font-size: 22rpx;
          color: #fff;
          background: rgba(255, 255, 255, 0.25);
          padding: 4rpx 18rpx;
          border-radius: 20rpx;
          margin-right: 12rpx;
        }

        .class-name {
          font-size: 22rpx;
          color: rgba(255, 255, 255, 0.85);
        }
      }
    }

    .edit-icon {
      padding: 16rpx;

      text {
        font-size: 32rpx;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 学习进度统计 */
.stats-section {
  padding: 0 32rpx;
  margin-top: -30rpx;
  position: relative;
  z-index: 2;

  .stats-card {
    background: #FFFFFF;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 20rpx rgba(58, 76, 86, 0.08);

    .stats-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;

      .title-text {
        font-size: 30rpx;
        font-weight: bold;
        color: #3A4C56;
      }

      .title-link {
        font-size: 24rpx;
        color: #D8B7BC;
      }
    }

    .stats-content {
      display: flex;
      align-items: center;
      justify-content: space-around;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .stat-value {
          font-size: 40rpx;
          font-weight: bold;
          color: #C77A60;
        }

        .stat-label {
          font-size: 24rpx;
          color: #989FA6;
          margin-top: 8rpx;
        }
      }

      .stat-divider {
        width: 1rpx;
        height: 56rpx;
        background: #E8E4DE;
      }
    }

    .progress-bar-wrapper {
      margin-top: 28rpx;

      .progress-bar {
        width: 100%;
        height: 16rpx;
        background: #D1E4F5;
        border-radius: 8rpx;
        overflow: hidden;

        .progress-inner {
          height: 100%;
          background: linear-gradient(90deg, #C77A60, #D39468);
          border-radius: 8rpx;
          transition: width 0.5s ease-out;
        }
      }

      .progress-text {
        font-size: 22rpx;
        color: #989FA6;
        margin-top: 12rpx;
        display: block;
        text-align: right;
      }
    }
  }
}

/* 功能入口列表 */
.menu-section {
  padding: 20rpx 32rpx;

  .menu-card {
    background: #FFFFFF;
    border-radius: 24rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 16rpx rgba(58, 76, 86, 0.06);

    .menu-item {
      display: flex;
      align-items: center;
      padding: 28rpx;
      border-bottom: 1rpx solid #F0EDE7;
      transition: background 0.3s ease-out;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background: #F7F4EF;
      }

      .menu-icon {
        width: 64rpx;
        height: 64rpx;
        border-radius: 16rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 24rpx;
        font-size: 30rpx;

        &.icon-favorite { background: #F5E0E2; }
        &.icon-record { background: #D1E4F5; }
        &.icon-exam { background: #E0EDE0; }
        &.icon-profile { background: #F5E6DA; }
        &.icon-password { background: #F5E0E2; }
      }

      .menu-text {
        flex: 1;
        font-size: 30rpx;
        color: #3A4C56;
      }

      .menu-arrow {
        font-size: 28rpx;
        color: #D8B7BC;
      }

      &.menu-logout {
        justify-content: center;

        .logout-text {
          font-size: 30rpx;
          color: #D17575;
          font-weight: 500;
        }
      }
    }
  }
}
</style>
