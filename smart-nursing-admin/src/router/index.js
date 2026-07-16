import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 基础路由
export const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/HomeView.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '首页', icon: 'Odometer' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 将后端菜单转换为路由配置
function generateRoutes(menuList) {
  const routes = []
  menuList.forEach((item) => {
    const route = {
      path: item.path,
      name: item.name,
      meta: {
        title: item.title,
        icon: item.icon
      }
    }
    // 动态导入组件：item.component 形如 'system/UserView'
    if (item.component) {
      // 使用 Glob 匹配 src/views 下的组件
      const modules = import.meta.glob('@/views/**/**.vue')
      const modulePath = `/src/views/${item.component}.vue`
      if (modules[modulePath]) {
        route.component = modules[modulePath]
      }
    }
    if (item.children && item.children.length > 0) {
      route.children = generateRoutes(item.children)
    }
    routes.push(route)
  })
  return routes
}

// 标记是否已添加动态路由
let dynamicRoutesAdded = false

// 路由守卫
router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 智慧护理管理平台`
    : '智慧护理管理平台'

  const userStore = useUserStore()
  const token = userStore.token

  if (!token) {
    // 没有 token
    if (to.path === '/') {
      next()
    } else {
      next('/')
    }
    return
  }

  // 已有 token 且在登录页，跳转到首页
  if (to.path === '/') {
    next('/dashboard')
    return
  }

  // 如果动态路由尚未添加，先添加
  if (!dynamicRoutesAdded) {
    try {
      if (!userStore.menuList || userStore.menuList.length === 0) {
        await userStore.fetchMenus()
      }
      const dynamicRoutes = generateRoutes(userStore.menuList)
      // 动态路由作为 /home 的子路由注入
      dynamicRoutes.forEach((route) => {
        router.addRoute('Home', route)
      })
      dynamicRoutesAdded = true
      // 重新导航以确保路由生效
      next({ ...to, replace: true })
      return
    } catch (error) {
      console.error('获取菜单失败', error)
      userStore.clear()
      dynamicRoutesAdded = false
      next('/')
      return
    }
  }

  next()
})

export default router

// 重置动态路由标记（退出登录时调用）
export function resetRouter() {
  dynamicRoutesAdded = false
}
