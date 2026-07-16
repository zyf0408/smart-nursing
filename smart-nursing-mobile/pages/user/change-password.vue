<template>
  <view class="change-password-page">
    <view class="form-section">
      <view class="form-card">
        <!-- 当前密码 -->
        <view class="form-item">
          <text class="form-label">当前密码</text>
          <view class="input-wrapper">
            <input
              v-model="formData.oldPassword"
              class="form-input"
              :type="showOld ? 'text' : 'password'"
              placeholder="请输入当前密码"
              placeholder-class="placeholder"
              maxlength="32"
            />
            <text class="toggle-icon" @tap="showOld = !showOld">{{ showOld ? '🙈' : '👁' }}</text>
          </view>
        </view>

        <!-- 新密码 -->
        <view class="form-item">
          <text class="form-label">新密码</text>
          <view class="input-wrapper">
            <input
              v-model="formData.newPassword"
              class="form-input"
              :type="showNew ? 'text' : 'password'"
              placeholder="请输入新密码（至少6位）"
              placeholder-class="placeholder"
              maxlength="32"
            />
            <text class="toggle-icon" @tap="showNew = !showNew">{{ showNew ? '🙈' : '👁' }}</text>
          </view>
        </view>

        <!-- 确认新密码 -->
        <view class="form-item">
          <text class="form-label">确认新密码</text>
          <view class="input-wrapper">
            <input
              v-model="formData.confirmPassword"
              class="form-input"
              :type="showConfirm ? 'text' : 'password'"
              placeholder="请再次输入新密码"
              placeholder-class="placeholder"
              maxlength="32"
            />
            <text class="toggle-icon" @tap="showConfirm = !showConfirm">{{ showConfirm ? '🙈' : '👁' }}</text>
          </view>
        </view>
      </view>

      <!-- 密码强度提示 -->
      <view v-if="formData.newPassword" class="password-strength">
        <view class="strength-bars">
          <view class="strength-bar" :class="{ active: strengthLevel >= 1, 'strength-weak': strengthLevel === 1 }"></view>
          <view class="strength-bar" :class="{ active: strengthLevel >= 2, 'strength-medium': strengthLevel === 2 }"></view>
          <view class="strength-bar" :class="{ active: strengthLevel >= 3, 'strength-strong': strengthLevel === 3 }"></view>
        </view>
        <text class="strength-text">{{ strengthText }}</text>
      </view>

      <!-- 安全提示 -->
      <view class="security-tips">
        <text class="tips-title">安全提示：</text>
        <text class="tips-item">• 密码长度至少6位，建议包含大小写字母、数字和特殊字符</text>
        <text class="tips-item">• 不要使用过于简单的密码，如123456、password等</text>
        <text class="tips-item">• 修改密码后需要重新登录</text>
      </view>

      <!-- 提交按钮 -->
      <button class="submit-btn" :disabled="loading" @tap="handleSubmit">
        {{ loading ? '提交中...' : '确认修改' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { user } from '@/api/index.js'

const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)
const loading = ref(false)

// 密码强度等级
const strengthLevel = computed(() => {
  const pwd = formData.newPassword
  if (!pwd) return 0
  let level = 0
  if (pwd.length >= 6) level++
  if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) level++
  if (/\d/.test(pwd) && /[^A-Za-z0-9]/.test(pwd)) level++
  return level
})

const strengthText = computed(() => {
  const texts = ['', '弱', '中', '强']
  return texts[strengthLevel.value] || ''
})

/**
 * 提交修改密码
 */
const handleSubmit = async () => {
  // 表单验证
  if (!formData.oldPassword.trim()) {
    uni.showToast({ title: '请输入当前密码', icon: 'none' })
    return
  }
  if (!formData.newPassword.trim()) {
    uni.showToast({ title: '请输入新密码', icon: 'none' })
    return
  }
  if (formData.newPassword.length < 6) {
    uni.showToast({ title: '新密码至少6位', icon: 'none' })
    return
  }
  if (formData.newPassword === formData.oldPassword) {
    uni.showToast({ title: '新密码不能与旧密码相同', icon: 'none' })
    return
  }
  if (formData.newPassword !== formData.confirmPassword) {
    uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await user.changePassword({
      oldPassword: formData.oldPassword,
      newPassword: formData.newPassword
    })

    uni.showModal({
      title: '修改成功',
      content: '密码已修改，请重新登录',
      showCancel: false,
      success: () => {
        // 清除登录状态
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        uni.reLaunch({ url: '/pages/login/login' })
      }
    })
  } catch (err) {
    console.error('修改密码失败:', err)
    uni.showToast({ title: err.message || '修改失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.change-password-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx 24rpx;
}

.form-section {
  .form-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 8rpx 32rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

    .form-item {
      padding: 24rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child { border-bottom: none; }

      .form-label {
        font-size: 28rpx;
        color: #333;
        font-weight: 500;
        display: block;
        margin-bottom: 16rpx;
      }

      .input-wrapper {
        display: flex;
        align-items: center;

        .form-input {
          flex: 1;
          height: 72rpx;
          font-size: 30rpx;
          color: #333;
        }

        .placeholder { color: #ccc; font-size: 30rpx; }

        .toggle-icon {
          font-size: 36rpx;
          padding: 10rpx;
          opacity: 0.6;
        }
      }
    }
  }

  .password-strength {
    display: flex;
    align-items: center;
    padding: 20rpx 8rpx;

    .strength-bars {
      display: flex;
      gap: 8rpx;

      .strength-bar {
        width: 80rpx;
        height: 8rpx;
        background: #e0e0e0;
        border-radius: 4rpx;
        transition: all 0.3s;

        &.strength-weak { background: #f44336; }
        &.strength-medium { background: #ff9800; }
        &.strength-strong { background: #4caf50; }
      }
    }

    .strength-text {
      font-size: 24rpx;
      color: #999;
      margin-left: 16rpx;
    }
  }

  .security-tips {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx 32rpx;
    margin-top: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

    .tips-title {
      font-size: 26rpx;
      font-weight: 600;
      color: #ff9800;
      display: block;
      margin-bottom: 12rpx;
    }

    .tips-item {
      font-size: 24rpx;
      color: #999;
      line-height: 1.8;
      display: block;
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
