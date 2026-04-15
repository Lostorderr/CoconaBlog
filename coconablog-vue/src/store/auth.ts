import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { UserInfo, LoginRequest, RegisterRequest } from '@/api/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 1)

  async function login(data: LoginRequest) {
    loading.value = true
    try {
      const response = await authApi.login(data)
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      return response
    } finally {
      loading.value = false
    }
  }

  async function register(data: RegisterRequest) {
    loading.value = true
    try {
      const response = await authApi.register(data)
      return response
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch {
      // ignore
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  async function fetchProfile() {
    if (!token.value) return
    
    loading.value = true
    try {
      const response = await authApi.getProfile()
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
    } catch {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    } finally {
      loading.value = false
    }
  }

  function initFromStorage() {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        localStorage.removeItem('user')
      }
    }
  }

  initFromStorage()

  return {
    user,
    token,
    loading,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchProfile
  }
})
