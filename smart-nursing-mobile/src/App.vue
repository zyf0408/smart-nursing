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
    // 不 hook uni.showModal，不直接移除 uni-modal DOM（会破坏 uni-app 内部状态）
    // mask/toast 残留问题通过以下方式解决：
    // 1. 各页面在使用 showToast 后主动 hideToast
    // 2. 全局 CSS 确保 uni-modal 正确显示
    // 3. 路由变化时用 CSS 隐藏残留遮罩（而非移除 DOM）
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

/* 修复 uni-app H5 模式下 uni-modal 组件 CSS 未加载导致 modal 不显示的问题
   症状1: 点击按钮调用 uni.showModal 后，只有灰色遮罩(.uni-mask)显示，
   modal 内容(.uni-modal)虽存在于 DOM 但因 uni-modal 元素 display:inline 而不可见，
   看起来像"灰屏"。
   症状2: 即使 modal 内容可见，按钮也无法点击，因为 .uni-mask 的 z-index 高于
   .uni-modal 内容，拦截了所有点击事件。
   原因: uni-modal 是自定义元素，浏览器默认 display:inline，需要 uni-app 的 CSS
   设置为 display:flex。同时 .uni-mask 和 .uni-modal 内容的 z-index 层级关系
   也需要 uni-app 的 CSS 来保证内容在遮罩之上。但某些情况下该 CSS 未被正确加载。
   方案: 在全局样式中强制 uni-modal 及其子元素使用正确的层级关系。

   重要: 不直接移除 uni-modal DOM 元素（会破坏 uni-app 内部状态，
   导致后续 uni.showModal 调用被静默忽略）。
   残留的空 uni-modal（没有 .uni-modal 子内容的）通过 CSS :has() 隐藏。 */
/* 残留的空 uni-modal（只有遮罩没有内容框的）自动隐藏，不阻挡交互 */
uni-modal:not(:has(.uni-modal)) {
  display: none !important;
}
uni-modal {
  display: flex !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100% !important;
  height: 100% !important;
  z-index: 999 !important;
  align-items: center !important;
  justify-content: center !important;
}
/* 遮罩层 z-index 较低 */
uni-modal .uni-mask {
  z-index: 1 !important;
  /* 遮罩更柔和，带轻微模糊（毛玻璃感） */
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}
/* === 内容容器：极浅灰白渐变 + 大圆角 + 柔和大范围阴影 === */
uni-modal .uni-modal {
  position: relative !important;
  z-index: 2 !important;
  /* 纯白到极浅灰的渐变背景，干净有质感 */
  background: linear-gradient(180deg, #ffffff 0%, #fafbfc 100%) !important;
  /* 大圆角 */
  border-radius: 32rpx !important;
  /* 柔和、大范围阴影：分层投影模拟微拟物 */
  box-shadow:
    0 4rpx 8rpx rgba(15, 23, 42, 0.04),
    0 16rpx 40rpx rgba(15, 23, 42, 0.12),
    0 32rpx 80rpx rgba(15, 23, 42, 0.08) !important;
  overflow: hidden !important;
  width: 620rpx !important;
  max-width: 86vw !important;
  /* 去除生硬边框 */
  border: none !important;
  padding: 8rpx 0 0 !important;
}
/* === 标题：居中，深灰护眼 === */
uni-modal .uni-modal__hd {
  padding: 48rpx 40rpx 12rpx !important;
  text-align: center !important;
}
uni-modal .uni-modal__title {
  font-size: 34rpx !important;
  font-weight: 600 !important;
  color: #1f2937 !important;
  letter-spacing: 1rpx;
}
/* === 正文：居中，字重500，深灰护眼 === */
uni-modal .uni-modal__bd {
  color: #4b5563 !important;
  font-size: 30rpx !important;
  font-weight: 500 !important;
  text-align: center !important;
  padding: 8rpx 48rpx 48rpx !important;
  line-height: 1.6 !important;
}
/* === 按钮区域：flex + 间距，移除传统分隔线 === */
uni-modal .uni-modal__ft {
  display: flex !important;
  gap: 24rpx !important;
  padding: 0 48rpx 48rpx !important;
  border-top: none !important;
}
/* === 按钮基础：胶囊形 + 过渡动效 === */
uni-modal .uni-modal__btn {
  flex: 1 !important;
  height: 92rpx !important;
  line-height: 92rpx !important;
  padding: 0 !important;
  border-radius: 46rpx !important;
  font-size: 32rpx !important;
  font-weight: 500 !important;
  text-align: center !important;
  cursor: pointer !important;
  user-select: none !important;
  letter-spacing: 2rpx;
  transition: all 0.28s cubic-bezier(0.4, 0, 0.2, 1) !important;
}
/* === 次按钮（取消）：极简幽灵按钮，浅灰文字 === */
uni-modal .uni-modal__btn_default {
  background: transparent !important;
  color: #9ca3af !important;
  box-shadow: none !important;
}
/* 次按钮 Hover：极淡灰底，绝不喧宾夺主 */
uni-modal .uni-modal__btn_default:hover {
  background-color: #f3f4f6 !important;
  color: #6b7280 !important;
  transform: translateY(-1rpx);
  box-shadow: none !important;
}
/* === 主按钮（开始）：青蓝渐变 + 内发光 + 柔和投影（微拟物）=== */
uni-modal .uni-modal__btn_primary {
  background: linear-gradient(135deg, #0EA5E9 0%, #2563EB 100%) !important;
  color: #ffffff !important;
  /* 细腻内发光 + 底部柔和投影，营造微拟物精致感 */
  box-shadow:
    inset 0 2rpx 4rpx rgba(255, 255, 255, 0.25),
    0 8rpx 20rpx rgba(37, 99, 235, 0.32),
    0 2rpx 6rpx rgba(14, 165, 233, 0.2) !important;
}
/* 主按钮 Hover：轻微上浮 + 亮度提升 + 阴影扩大 */
uni-modal .uni-modal__btn_primary:hover {
  filter: brightness(1.08) !important;
  transform: translateY(-4rpx);
  box-shadow:
    inset 0 2rpx 4rpx rgba(255, 255, 255, 0.3),
    0 14rpx 32rpx rgba(37, 99, 235, 0.42),
    0 4rpx 10rpx rgba(14, 165, 233, 0.25) !important;
}
/* 主按钮 Active：按压反馈 */
uni-modal .uni-modal__btn_primary:active {
  transform: translateY(-1rpx) scale(0.97) !important;
  box-shadow:
    inset 0 2rpx 6rpx rgba(0, 0, 0, 0.12),
    0 4rpx 12rpx rgba(37, 99, 235, 0.28) !important;
}
/* 次按钮 Active：轻微按压 */
uni-modal .uni-modal__btn_default:active {
  transform: scale(0.97) !important;
  background-color: #e5e7eb !important;
}
/* #endif */
</style>
