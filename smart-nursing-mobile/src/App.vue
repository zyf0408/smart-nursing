<script>
export default {
  onLaunch() {
    console.log('App Launch - 智慧护理学员端')
    // 检查登录状态
    const token = uni.getStorageSync('token')
    if (!token) {
      // 未登录则跳转到登录页
      uni.reLaunch({
        url: '/pages/login/login'
      })
    }

    // #ifdef H5
    // 修复 uni-app H5 模式下 uni.showModal / uni.showLoading 的 mask 残留问题
    // 原因：showModal/showLoading 关闭后 .uni-mask 和 uni-modal 元素不会被移除，
    //       导致页面被灰色遮罩覆盖（灰屏），且每次操作都会叠加更多遮罩。
    // 方案：hook showModal，在回调后自动清理残留的 DOM 元素。
    const originalShowModal = uni.showModal
    uni.showModal = function (options) {
      const cleanup = () => {
        // 延迟 500ms 确保 uni-app 内部关闭动画完成后再清理 DOM
        setTimeout(() => {
          // 直接移除所有 uni-modal 元素（success/fail 回调意味着 modal 已关闭）
          document.querySelectorAll('uni-modal').forEach(el => el.remove())
          // 移除所有孤立的 .uni-mask（没有可见 modal 或 toast 的）
          const hasVisibleModal = Array.from(document.querySelectorAll('uni-modal')).some(el => el.style.display !== 'none')
          const hasVisibleToast = document.querySelector('.uni-toast') && document.querySelector('.uni-toast').style.display !== 'none'
          if (!hasVisibleModal && !hasVisibleToast) {
            document.querySelectorAll('.uni-mask').forEach(el => el.remove())
          }
        }, 500)
      }
      const originalSuccess = options.success
      const originalFail = options.fail
      const originalComplete = options.complete
      options.success = function (res) {
        if (originalSuccess) originalSuccess(res)
        cleanup()
      }
      options.fail = function (err) {
        if (originalFail) originalFail(err)
        cleanup()
      }
      options.complete = function (res) {
        if (originalComplete) originalComplete(res)
        cleanup()
      }
      return originalShowModal.call(uni, options)
    }

    // 路由变化时也清理残留 mask
    window.addEventListener('hashchange', () => {
      setTimeout(() => {
        document.querySelectorAll('uni-modal').forEach(el => el.remove())
        const hasVisibleToast = document.querySelector('.uni-toast') && document.querySelector('.uni-toast').style.display !== 'none'
        if (!hasVisibleToast) {
          document.querySelectorAll('.uni-mask').forEach(el => el.remove())
        }
      }, 500)
    })
    // #endif
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* ===========================
   智慧护理 - 全局设计系统
   互联网+智慧护理线上实训平台
   =========================== */

/* --- 设计令牌（CSS 变量） --- */
page {
  /* 主色调 */
  --color-mist-blue: #D1E4F5;       /* 雾柔医护蓝 - 模块底板 */
  --color-cream-white: #F7F4EF;     /* 米杏柔白 - 全局背景 */
  --color-sage-teal: #93B4B8;       /* 浅医护苔青 - 激活态、主标题 */
  --color-warm-terracotta: #C77A60; /* 暖陶土橙 - 主操作按钮 */
  --color-soft-pink: #D8B7BC;       /* 雾豆沙柔粉 - 次要标签 */

  /* 文字层级 */
  --color-text-h1: #3A4C56;         /* 深苔蓝灰 - 一级标题 */
  --color-text-body: #636A70;       /* 暖调中灰 - 正文 */
  --color-text-small: #989FA6;      /* 浅沙灰 - 备注/小字 */

  /* 状态色 */
  --color-success: #95BB92;         /* 浅苔青 - 完成/正常 */
  --color-warning: #D39468;         /* 浅陶橙 - 警告 */
  --color-info: #84A7C6;            /* 柔雾蓝 - 系统提示 */
  --color-error: #D17575;           /* 柔砖红 - 错误 */

  /* 背景与边框 */
  --bg-page: #F7F4EF;
  --bg-card: #FFFFFF;
  --bg-soft: #F0EDE7;
  --border-soft: #E8E4DE;

  /* 阴影 */
  --shadow-soft: 0 4rpx 20rpx rgba(147, 180, 184, 0.12);
  --shadow-card: 0 2rpx 16rpx rgba(58, 76, 86, 0.06);
  --shadow-hover: 0 8rpx 30rpx rgba(147, 180, 184, 0.15);

  /* 圆角 */
  --radius-sm: 12rpx;
  --radius-md: 20rpx;
  --radius-lg: 28rpx;
  --radius-full: 999rpx;

  /* 动画 */
  --ease-out: cubic-bezier(0.22, 1, 0.36, 1);
  --duration-normal: 400ms;
  --duration-slow: 600ms;

  /* 应用基础样式 */
  background-color: var(--bg-page);
  font-size: 28rpx;
  color: var(--color-text-body);
  font-family: 'PingFang SC', 'Noto Sans CJK SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.8;
}

/* --- 全局通用样式 --- */
.container {
  padding: 20rpx;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.text-primary {
  color: var(--color-sage-teal);
}

.text-success {
  color: var(--color-success);
}

.text-warning {
  color: var(--color-warning);
}

.text-danger {
  color: var(--color-error);
}

.text-gray {
  color: var(--color-text-small);
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--shadow-card);
}

.btn-primary {
  background: linear-gradient(135deg, var(--color-warm-terracotta), #B56748);
  color: #fff;
  border: none;
  border-radius: var(--radius-full);
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  text-align: center;
  box-shadow: 0 4rpx 16rpx rgba(199, 122, 96, 0.3);
  transition: all var(--duration-normal) var(--ease-out);
}

.btn-primary[disabled] {
  background: #D1C5BE;
  color: rgba(255, 255, 255, 0.8);
  box-shadow: none;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
  color: var(--color-text-small);
  font-size: 28rpx;
}

.empty-state .empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
  opacity: 0.4;
}

/* --- 滚动入场动画 --- */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp var(--duration-slow) var(--ease-out) forwards;
}

/* --- 列表项 hover 效果 --- */
.hover-lift {
  transition: all var(--duration-normal) var(--ease-out);
}

/* #ifdef H5 */
.hover-lift:hover {
  transform: translateY(-2rpx);
  box-shadow: var(--shadow-hover);
}
/* #endif */
</style>
