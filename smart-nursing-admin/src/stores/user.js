import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo, getMenuList } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    roleCode: localStorage.getItem('roleCode') || '',
    menuList: JSON.parse(localStorage.getItem('menuList') || '[]')
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.roleCode = userInfo.roleCode || ''
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      if (userInfo.roleCode) {
        localStorage.setItem('roleCode', userInfo.roleCode)
      }
    },
    setMenuList(menuList) {
      this.menuList = menuList
      localStorage.setItem('menuList', JSON.stringify(menuList))
    },
    // 获取菜单列表
    async fetchMenus() {
      try {
        const res = await getMenuList()
        this.setMenuList(res || [])
        return res
      } catch (error) {
        console.error('获取菜单失败', error)
        this.setMenuList([])
        return []
      }
    },
    // 清除所有状态
    clear() {
      this.token = ''
      this.userInfo = {}
      this.roleCode = ''
      this.menuList = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('roleCode')
      localStorage.removeItem('menuList')
    },
    // 退出登录
    async logout() {
      try {
        await logoutApi()
      } catch (error) {
        console.error('退出登录接口异常', error)
      } finally {
        this.clear()
      }
    }
  }
})
