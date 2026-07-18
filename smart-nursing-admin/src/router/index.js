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
      },
      // ===== 编辑/详情页静态路由（不在菜单中，需固定注册） =====
      {
        path: '/content/article/edit',
        name: 'ArticleEdit',
        component: () => import('@/views/content/ArticleEdit.vue'),
        meta: { title: '文章编辑' }
      },
      {
        path: '/content/video/edit',
        name: 'VideoEdit',
        component: () => import('@/views/content/VideoEdit.vue'),
        meta: { title: '视频编辑' }
      },
      {
        path: '/content/ppt/edit',
        name: 'PptEdit',
        component: () => import('@/views/content/PptEdit.vue'),
        meta: { title: '课件编辑' }
      },
      {
        path: '/exam/edit',
        name: 'ExamEdit',
        component: () => import('@/views/exam/ExamEdit.vue'),
        meta: { title: '考试编辑' }
      },
      {
        path: '/examRecord/detail',
        name: 'RecordDetail',
        component: () => import('@/views/exam/RecordDetail.vue'),
        meta: { title: '考试详情' }
      }
    ]
  },
  // 兜底路由：防止动态路由未加载时出现 "No match found" 警告
  // 使用空渲染组件而非 redirect，以保留原始路径供导航守卫重新解析
  {
    path: '/:pathMatch(.*)*',
    name: 'CatchAll',
    component: { render: () => null },
    meta: { title: '加载中' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 基础路由路径集合，用于跳过已存在的路由
const basePathSet = new Set(['/dashboard'])

// 将后端菜单转换为路由配置
function generateRoutes(menuList) {
  const routes = []
  // 预加载所有视图组件
  const modules = import.meta.glob('../views/**/*.vue')

  if (!menuList || !Array.isArray(menuList)) {
    return routes
  }

  menuList.forEach((item) => {
    // 跳过 path 为 null/empty 的目录节点，只处理其子菜单
    if (!item.path || item.path.trim() === '') {
      if (item.children && item.children.length > 0) {
        routes.push(...generateRoutes(item.children))
      }
      return
    }
    // 跳过已存在的基础路由（如 /dashboard），防止覆盖
    if (basePathSet.has(item.path)) {
      return
    }
    const route = {
      path: item.path,
      name: item.name || undefined,
      meta: {
        title: item.title,
        icon: item.icon
      }
    }
    // 动态导入组件：item.component 形如 'UserView' 或 'system/UserView'
    // 通过文件名在所有子目录中搜索匹配
    if (item.component) {
      const componentName = item.component
      const matchingKey = Object.keys(modules).find(key => {
        // key 形如 '../views/system/UserView.vue'
        return key === `../views/${componentName}.vue` ||
               key.endsWith(`/${componentName}.vue`)
      })
      if (matchingKey) {
        route.component = modules[matchingKey]
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
      // 只传 path，不传 to.matched，强制路由器重新解析路径
      next({ path: to.fullPath, replace: true })
      return
    } catch (error) {
      console.error('获取菜单失败', error)
      userStore.clear()
      dynamicRoutesAdded = false
      next('/')
      return
    }
  }

  // 动态路由已加载但目标路由仍无匹配（或仅匹配到兜底路由），跳转到首页
  if (to.matched.length === 0 ||
      (to.matched.length === 1 && to.matched[0].name === 'CatchAll')) {
    next('/dashboard')
    return
  }

  next()
})

export default router

// 重置动态路由标记（退出登录时调用）
export function resetRouter() {
  dynamicRoutesAdded = false
}
