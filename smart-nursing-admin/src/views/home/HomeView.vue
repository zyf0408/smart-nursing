<template>
  <el-container class="home-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '210px'" class="home-aside">
      <div class="logo-box">
        <el-icon size="28" color="#fff"><FirstAidKit /></el-icon>
        <span v-show="!isCollapse" class="logo-text">智慧护理</span>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <div class="custom-menu">
          <template v-for="item in visibleMenuList" :key="item.menuId">
            <!-- 有子菜单的目录 -->
            <div v-if="item.children && item.children.length > 0" class="menu-group">
              <div
                class="menu-title"
                :class="{ active: isGroupActive(item) }"
                @click="toggleGroup(String(item.menuId))"
              >
                <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
                <span v-show="!isCollapse" class="menu-text">{{ item.title }}</span>
                <el-icon v-show="!isCollapse" class="menu-arrow" :class="{ rotated: openedGroups.has(String(item.menuId)) }">
                  <ArrowDown />
                </el-icon>
              </div>
              <div
                v-show="!isCollapse"
                class="menu-children"
                :style="{ maxHeight: openedGroups.has(String(item.menuId)) ? '500px' : '0' }"
              >
                <div
                  v-for="child in item.children"
                  :key="child.menuId"
                  class="menu-item"
                  :class="{ active: route.path === child.path }"
                  @click="navigateTo(child.path)"
                >
                  <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
                  <span class="menu-text">{{ child.title }}</span>
                </div>
              </div>
            </div>
            <!-- 无子菜单的叶子节点 -->
            <div
              v-else
              class="menu-item leaf"
              :class="{ active: route.path === item.path }"
              @click="navigateTo(item.path)"
            >
              <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
              <span v-show="!isCollapse" class="menu-text">{{ item.title }}</span>
            </div>
          </template>
        </div>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="home-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbList"
              :key="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar">
                {{ userStore.userInfo.nickname?.charAt(0) || 'A' }}
              </el-avatar>
              <span class="username">
                {{ userStore.userInfo.nickname || userStore.userInfo.username || '管理员' }}
              </span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="dashboard">
                  <el-icon><Odometer /></el-icon>首页
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="home-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { resetRouter } from '@/router'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const openedGroups = ref(new Set())

const activeMenu = computed(() => route.path)

// 过滤菜单：只显示 visible !== 0 的菜单项
const visibleMenuList = computed(() => {
  const result = []
  for (const item of userStore.menuList) {
    if (item.visible === 0) continue
    if (item.children && item.children.length > 0) {
      const visibleChildren = item.children.filter(child => child.visible !== 0)
      if (visibleChildren.length > 0) {
        result.push({ ...item, children: visibleChildren })
      }
    } else {
      result.push(item)
    }
  }
  return result
})

const breadcrumbList = computed(() => {
  const matched = route.matched.filter((item) => item.meta && item.meta.title)
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title
  }))
})

const isGroupActive = (item) => {
  if (!item.children) return false
  return item.children.some(child => route.path === child.path)
}

const toggleGroup = (id) => {
  if (isCollapse.value) return
  if (openedGroups.value.has(id)) {
    openedGroups.value.delete(id)
  } else {
    openedGroups.value.add(id)
  }
  // 触发响应式更新
  openedGroups.value = new Set(openedGroups.value)
}

const navigateTo = (path) => {
  if (path) router.push(path)
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'dashboard') {
    router.push('/dashboard')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        userStore.logout().then(() => {
          resetRouter()
          ElMessage.success('已退出登录')
          router.push('/')
        })
      })
      .catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.home-container {
  height: 100vh;
}

.home-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo-box {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background-color: #2b3a4d;

    .logo-text {
      color: #fff;
      font-size: 18px;
      font-weight: bold;
      white-space: nowrap;
    }
  }

  .menu-scrollbar {
    height: calc(100vh - 60px);
  }
}

.custom-menu {
  user-select: none;

  .menu-group {
    .menu-title {
      display: flex;
      align-items: center;
      gap: 8px;
      height: 56px;
      padding: 0 20px;
      color: #bfcbd9;
      cursor: pointer;
      transition: background-color 0.2s;
      white-space: nowrap;

      &:hover {
        background-color: #263445;
      }

      &.active {
        color: #409eff;
      }

      .menu-text {
        flex: 1;
        font-size: 14px;
      }

      .menu-arrow {
        transition: transform 0.3s;
        font-size: 12px;

        &.rotated {
          transform: rotate(180deg);
        }
      }
    }

    .menu-children {
      overflow: hidden;
      transition: max-height 0.3s ease;

      .menu-item {
        padding-left: 48px;
        height: 48px;
        line-height: 48px;
        color: #bfcbd9;
        cursor: pointer;
        transition: all 0.2s;
        white-space: nowrap;

        &:hover {
          background-color: #263445;
          color: #fff;
        }

        &.active {
          color: #409eff;
          background-color: #1f2d3d;
        }
      }
    }
  }

  .menu-item.leaf {
    display: flex;
    align-items: center;
    gap: 8px;
    height: 56px;
    padding: 0 20px;
    color: #bfcbd9;
    cursor: pointer;
    transition: all 0.2s;
    white-space: nowrap;

    &:hover {
      background-color: #263445;
      color: #fff;
    }

    &.active {
      color: #409eff;
      background-color: #1f2d3d;
    }

    .menu-text {
      flex: 1;
      font-size: 14px;
    }
  }
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      cursor: pointer;
      color: #5a5e66;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .username {
        font-size: 14px;
        color: #303133;
      }
    }
  }
}

.home-main {
  background: #f0f2f5;
  overflow-y: auto;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
