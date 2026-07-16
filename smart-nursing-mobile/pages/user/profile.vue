<template>
  <view class="profile-page">
    <!-- 头像区域 -->
    <view class="avatar-section" @tap="changeAvatar">
      <view class="avatar-wrapper">
        <image v-if="formData.avatar" :src="formData.avatar" class="avatar" mode="aspectFill" />
        <view v-else class="avatar avatar-default">
          <text class="avatar-text">{{ formData.name ? formData.name.charAt(0) : '护' }}</text>
        </view>
        <view class="avatar-edit-badge">
          <text>📷</text>
        </view>
      </view>
      <text class="avatar-tip">点击修改头像</text>
    </view>

    <!-- 表单区域 -->
    <view class="form-section">
      <view class="form-card">
        <!-- 姓名 -->
        <view class="form-item">
          <text class="form-label">姓名</text>
          <input
            v-model="formData.name"
            class="form-input"
            type="text"
            placeholder="请输入姓名"
            placeholder-class="placeholder"
            maxlength="20"
          />
        </view>

        <!-- 用户名（不可修改） -->
        <view class="form-item form-item-readonly">
          <text class="form-label">用户名</text>
          <text class="form-value-readonly">{{ formData.username }}</text>
        </view>

        <!-- 角色（不可修改） -->
        <view class="form-item form-item-readonly">
          <text class="form-label">角色</text>
          <text class="form-value-readonly">{{ getRoleText(formData.role) }}</text>
        </view>

        <!-- 手机号 -->
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input
            v-model="formData.phone"
            class="form-input"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="placeholder"
            maxlength="11"
          />
        </view>

        <!-- 邮箱 -->
        <view class="form-item">
          <text class="form-label">邮箱</text>
          <input
            v-model="formData.email"
            class="form-input"
            type="text"
            placeholder="请输入邮箱"
            placeholder-class="placeholder"
            maxlength="50"
          />
        </view>

        <!-- 班级/科室 -->
        <view class="form-item">
          <text class="form-label">班级/科室</text>
          <input
            v-model="formData.className"
            class="form-input"
            type="text"
            placeholder="请输入班级或科室"
            placeholder-class="placeholder"
            maxlength="30"
          />
        </view>

        <!-- 工号 -->
        <view class="form-item form-item-readonly">
          <text class="form-label">工号</text>
          <text class="form-value-readonly">{{ formData.employeeNo || '未设置' }}</text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button class="submit-btn" :disabled="loading" @tap="handleSubmit">
        {{ loading ? '保存中...' : '保存修改' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { user } from '@/api/index.js'
import http from '@/api/request.js'

const formData = reactive({
  name: '',
  username: '',
  role: '',
  avatar: '',
  phone: '',
  email: '',
  className: '',
  employeeNo: ''
})

const loading = ref(false)

onLoad(() => {
  loadUserInfo()
})

onShow(() => {
  // 从本地存储恢复用户信息
  const stored = uni.getStorageSync('userInfo')
  if (stored) {
    try { Object.assign(formData, JSON.parse(stored)) } catch (e) {}
  }
})

/**
 * 加载用户信息
 */
const loadUserInfo = async () => {
  try {
    const res = await user.getUserInfo()
    Object.assign(formData, res)
  } catch (err) {
    console.error('加载用户信息失败:', err)
  }
}

/**
 * 修改头像
 */
const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempPath = res.tempFilePaths[0]
      try {
        uni.showLoading({ title: '上传中...' })
        const uploadRes = await http.upload('/api/user/avatar', tempPath)
        formData.avatar = uploadRes.url || uploadRes
        uni.hideLoading()
        uni.showToast({ title: '头像更新成功', icon: 'success' })
      } catch (err) {
        uni.hideLoading()
        uni.showToast({ title: '头像上传失败', icon: 'none' })
      }
    }
  })
}

/**
 * 提交保存
 */
const handleSubmit = async () => {
  // 验证
  if (!formData.name.trim()) {
    uni.showToast({ title: '请输入姓名', icon: 'none' })
    return
  }
  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return
  }
  if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    uni.showToast({ title: '邮箱格式不正确', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await user.updateProfile({
      name: formData.name.trim(),
      phone: formData.phone,
      email: formData.email,
      className: formData.className,
      avatar: formData.avatar
    })

    // 更新本地存储
    uni.setStorageSync('userInfo', JSON.stringify(formData))
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } catch (err) {
    console.error('保存失败:', err)
    uni.showToast({ title: err.message || '保存失败', icon: 'none' })
  } finally {
    loading.value = false
  }
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
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
  background: #fff;
  margin-bottom: 20rpx;

  .avatar-wrapper {
    position: relative;
    width: 140rpx;
    height: 140rpx;

    .avatar {
      width: 140rpx;
      height: 140rpx;
      border-radius: 50%;

      &.avatar-default {
        background: linear-gradient(135deg, #2979ff, #1565c0);
        display: flex;
        align-items: center;
        justify-content: center;

        .avatar-text {
          font-size: 56rpx;
          color: #fff;
          font-weight: bold;
        }
      }
    }

    .avatar-edit-badge {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 44rpx;
      height: 44rpx;
      border-radius: 50%;
      background: #2979ff;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 3rpx solid #fff;

      text {
        font-size: 24rpx;
      }
    }
  }

  .avatar-tip {
    font-size: 24rpx;
    color: #999;
    margin-top: 16rpx;
  }
}

/* 表单区域 */
.form-section {
  padding: 0 24rpx;

  .form-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 8rpx 32rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

    .form-item {
      display: flex;
      align-items: center;
      padding: 28rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child { border-bottom: none; }

      .form-label {
        font-size: 28rpx;
        color: #333;
        width: 180rpx;
        flex-shrink: 0;
      }

      .form-input {
        flex: 1;
        height: 60rpx;
        font-size: 28rpx;
        color: #333;
        text-align: right;
      }

      .placeholder { color: #ccc; font-size: 28rpx; }

      .form-value-readonly {
        flex: 1;
        font-size: 28rpx;
        color: #999;
        text-align: right;
      }

      &.form-item-readonly {
        .form-label { color: #999; }
      }
    }
  }

  .submit-btn {
    width: 100%;
    height: 92rpx;
    line-height: 92rpx;
    background: linear-gradient(135deg, #2979ff, #1565c0);
    color: #fff;
    font-size: 34rpx;
    border-radius: 46rpx;
    border: none;
    margin-top: 40rpx;
    box-shadow: 0 8rpx 24rpx rgba(41, 121, 255, 0.35);

    &::after { border: none; }
    &[disabled] { opacity: 0.6; }
  }
}
</style>
