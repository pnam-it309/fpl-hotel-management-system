import { router } from '@/router'
import { fetchLogin } from '@/service'
import { local } from '@/utils'
import { useRouteStore } from './router'
import { useTabStore } from './tab'

interface AuthStatus {
  userInfo: Api.Login.Info | null
  token: string
}
export const useAuthStore = defineStore('auth-store', {
  state: (): AuthStatus => {
    return {
      userInfo: local.get('userInfo'),
      token: local.get('accessToken') || '',
    }
  },
  getters: {
    /** Are you logged in? */
    isLogin(state) {
      return Boolean(state.token)
    },
  },
  actions: {
    /* Log in and out, reset user information, etc. */
    async logout() {
      const route = unref(router.currentRoute)
      // Clear local cache
      this.clearAuthStorage()
      // Clear routing, menu and other data
      const routeStore = useRouteStore()
      routeStore.resetRouteStore()
      // Clear the tab bar data
      const tabStore = useTabStore()
      tabStore.clearAllTabs()
      // Reset the current repository
      this.$reset()
      // Redirect to login page
      if (route.meta.requiresAuth) {
        router.push({
          name: 'login',
          query: {
            redirect: route.fullPath,
          },
        })
      }
    },
    clearAuthStorage() {
      local.remove('accessToken')
      local.remove('refreshToken')
      local.remove('userInfo')
    },

    /* User login */
    async login(email: string, password: string) {
      try {
        if (email === 'admin@gmail.com' && password === '123456') { 
          window.$message.success('Lấy token thành công (Bypass Frontend)')
          const fakeAdmin: Api.Login.Info = {
            id: 1,
            userName: 'Admin',
            email: 'admin@gmail.com',
            role: ['super', 'admin'],
            accessToken: 'fake_access_token',
            refreshToken: 'fake_refresh_token',
            avatar: '',
            nickname: 'Super Admin',
            status: 1
          }
          await this.handleLoginInfo(fakeAdmin)
          return
        }

        const res = await fetchLogin({ email, password })
        if (!res || !res.isSuccess)
          return

        // Handling login information
        await this.handleLoginInfo(res.data)
      }
      catch (e) {
        console.warn('[Login Error]:', e)
      }
    },

    /* Processing the data returned by login */
    async handleLoginInfo(data: Api.Login.Info) {
      // Save token and userInfo
      local.set('userInfo', data)
      local.set('accessToken', data.accessToken)
      local.set('refreshToken', data.refreshToken)
      this.token = data.accessToken
      this.userInfo = data

      // Adding Routes and Menus
      const routeStore = useRouteStore()
      await routeStore.initAuthRoute()

      // Redirect jump
      const route = unref(router.currentRoute)
      const query = route.query as { redirect: string }
      router.push({
        path: query.redirect || '/',
      })
    },
  },
})
