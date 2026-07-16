<template>
  <view class="login-page">
    <!-- 顶部装饰区 -->
    <view class="login-header">
      <view class="logo-wrapper">
        <view class="logo-icon">
          <text class="iconfont">+</text>
        </view>
      </view>
      <text class="app-title">智慧护理学员端</text>
      <text class="app-subtitle">专业护理学习与考试平台</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">👤</text>
          <input
            v-model="formData.username"
            class="input-field"
            type="text"
            placeholder="请输入用户名"
            placeholder-class="placeholder"
            maxlength="50"
            @confirm="onUsernameConfirm"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">🔒</text>
          <input
            v-model="formData.password"
            class="input-field"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            placeholder-class="placeholder"
            maxlength="32"
            @confirm="handleLogin"
          />
          <text class="password-toggle" @tap="showPassword = !showPassword">
            {{ showPassword ? '🙈' : '👁' }}
          </text>
        </view>
      </view>

      <view class="form-options">
        <view class="remember-me" @tap="rememberMe = !rememberMe">
          <view class="checkbox" :class="{ checked: rememberMe }">
            <text v-if="rememberMe" class="check-icon">✓</text>
          </view>
          <text class="remember-text">记住我</text>
        </view>
      </view>

      <button
        class="login-btn"
        :class="{ 'btn-loading': loading }"
        :disabled="loading"
        @tap="handleLogin"
      >
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <view class="login-tips">
        <text class="tip-text">护士学员账号由管理员统一分配</text>
      </view>
    </view>

    <!-- 底部版权 -->
    <view class="login-footer">
      <text class="copyright">© 2026 智慧护理教育平台</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { auth } from '@/api/index.js'

// 表单数据
const formData = reactive({
  username: '',
  password: ''
})

// 控制状态
const showPassword = ref(false)
const rememberMe = ref(false)
const loading = ref(false)

// 用户名输入确认后聚焦密码
const onUsernameConfirm = () => {
  // uni-app 中自动切换焦点由前端控制
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 表单验证
  if (!formData.username.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!formData.password.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  if (formData.password.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }

  loading.value = true

  try {
    const res = await auth.login({
      username: formData.username.trim(),
      password: formData.password
    })

    // 存储 token 和用户信息
    const token = res.token || res.accessToken
    if (token) {
      uni.setStorageSync('token', token)
    }
    if (res.userInfo || res.user) {
      uni.setStorageSync('userInfo', JSON.stringify(res.userInfo || res.user))
    }

    // 记住用户名
    if (rememberMe.value) {
      uni.setStorageSync('savedUsername', formData.username)
    } else {
      uni.removeStorageSync('savedUsername')
    }

    uni.showToast({ title: '登录成功', icon: 'success' })

    setTimeout(() => {
      uni.switchTab({
        url: '/pages/learn/index'
      })
    }, 800)
  } catch (err) {
    console.error('登录失败:', err)
    uni.showToast({
      title: err.message || '登录失败，请检查用户名和密码',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 页面加载时恢复记住的用户名
const restoreSavedUsername = () => {
  const saved = uni.getStorageSync('savedUsername')
  if (saved) {
    formData.username = saved
    rememberMe.value = true
  }
}

restoreSavedUsername()
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(180deg, #2979ff 0%, #1565c0 40%, #f5f5f5 40%, #f5f5f5 100%);
  padding: 0 60rpx;
}

/* 顶部装饰区 */
.login-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 140rpx;
  padding-bottom: 60rpx;

  .logo-wrapper {
    margin-bottom: 24rpx;

    .logo-icon {
      width: 120rpx;
      height: 120rpx;
      border-radius: 30rpx;
      background: rgba(255, 255, 255, 0.25);
      backdrop-filter: blur(10px);
      display: flex;
      align-items: center;
      justify-content: center;

      .iconfont {
        font-size: 64rpx;
        color: #fff;
        font-weight: bold;
      }
    }
  }

  .app-title {
    font-size: 44rpx;
    font-weight: bold;
    color: #fff;
    letter-spacing: 4rpx;
  }

  .app-subtitle {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.85);
    margin-top: 12rpx;
  }
}

/* 登录表单 */
.login-form {
  width: 100%;
  background: #fff;
  border-radius: 24rpx;
  padding: 50rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.1);
  margin-top: 20rpx;

  .form-item {
    margin-bottom: 36rpx;

    .input-wrapper {
      display: flex;
      align-items: center;
      border-bottom: 2rpx solid #eee;
      padding: 16rpx 0;
      transition: border-color 0.3s;

      &:focus-within {
        border-bottom-color: #2979ff;
      }

      .input-icon {
        font-size: 36rpx;
        margin-right: 16rpx;
        opacity: 0.6;
      }

      .input-field {
        flex: 1;
        height: 72rpx;
        font-size: 30rpx;
        color: #333;
      }

      .password-toggle {
        font-size: 36rpx;
        padding: 0 10rpx;
        opacity: 0.6;
      }

      .placeholder {
        color: #ccc;
        font-size: 30rpx;
      }
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 50rpx;

    .remember-me {
      display: flex;
      align-items: center;

      .checkbox {
        width: 32rpx;
        height: 32rpx;
        border: 2rpx solid #ddd;
        border-radius: 8rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12rpx;
        transition: all 0.3s;

        &.checked {
          background: #2979ff;
          border-color: #2979ff;
        }

        .check-icon {
          color: #fff;
          font-size: 24rpx;
          font-weight: bold;
        }
      }

      .remember-text {
        font-size: 26rpx;
        color: #666;
      }
    }
  }

  .login-btn {
    width: 100%;
    height: 92rpx;
    line-height: 92rpx;
    background: linear-gradient(135deg, #2979ff, #1565c0);
    color: #fff;
    font-size: 34rpx;
    border-radius: 46rpx;
    border: none;
    letter-spacing: 8rpx;
    box-shadow: 0 8rpx 24rpx rgba(41, 121, 255, 0.35);

    &::after {
      border: none;
    }

    &.btn-loading {
      opacity: 0.7;
    }

    &[disabled] {
      background: linear-gradient(135deg, #2979ff, #1565c0);
      color: rgba(255, 255, 255, 0.8);
    }
  }

  .login-tips {
    text-align: center;
    margin-top: 36rpx;

    .tip-text {
      font-size: 24rpx;
      color: #aaa;
    }
  }
}

/* 底部版权 */
.login-footer {
  margin-top: auto;
  padding: 40rpx 0;

  .copyright {
    font-size: 22rpx;
    color: #bbb;
  }
}
</style>
