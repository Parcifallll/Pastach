
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient } from '@/api/axios'
import type { User, AuthResponse, LoginRequest, SignupRequest, RefreshTokenRequest } from '@/types/models'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
    // State
    const user = ref<User | null>(null)
    const accessToken = ref<string | null>(localStorage.getItem('auth_token'))
    const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'))
    const loading = ref(false)
    const error = ref<string | null>(null)

    // Getters
    const isAuthenticated = computed(() => !!accessToken.value && !!user.value)

    const isAdmin = computed(() => {
        return user.value?.roles?.some(role => role.name === 'ADMIN') ?? false
    })

    // Actions
    const login = async (credentials: LoginRequest) => {
        loading.value = true
        error.value = null

        try {
            const response = await apiClient.post<AuthResponse>('/auth/login', credentials)
            const data = response.data

            accessToken.value = data.accessToken
            refreshToken.value = data.refreshToken

            localStorage.setItem('auth_token', data.accessToken)
            localStorage.setItem('refresh_token', data.refreshToken)

            // Load current user
            await fetchCurrentUser()
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Login error'
            throw err
        } finally {
            loading.value = false
        }
    }

    const signup = async (data: SignupRequest) => {
        loading.value = true
        error.value = null

        try {
            const response = await apiClient.post<AuthResponse>('/auth/signup', data)
            const authData = response.data

          
            accessToken.value = authData.accessToken
            refreshToken.value = authData.refreshToken

            localStorage.setItem('auth_token', authData.accessToken)
            localStorage.setItem('refresh_token', authData.refreshToken)

            // Load current user
            await fetchCurrentUser()

            return authData
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Registration error'
            throw err
        } finally {
            loading.value = false
        }
    }

    const fetchCurrentUser = async () => {
        if (!accessToken.value) return

        try {
            const response = await apiClient.get<User>('/users/me')
            user.value = response.data

            // Save to localStorage for quick restore
            localStorage.setItem('user', JSON.stringify(user.value))
        } catch (err) {
            console.error('Failed to fetch current user:', err)
            logout() // if token expired - logout
        }
    }

    const logout = async () => {
        try {
            // Try to logout on backend
            if (refreshToken.value) {
                await apiClient.post('/auth/logout', { refreshToken: refreshToken.value })
            }
        } catch (err) {
            console.error('Logout error:', err)
        } finally {
            // Clear local state regardless of backend response
            user.value = null
            accessToken.value = null
            refreshToken.value = null

            localStorage.removeItem('auth_token')
            localStorage.removeItem('refresh_token')
            localStorage.removeItem('user')

            router.push('/login')
        }
    }

    const refreshAccessToken = async () => {
        if (!refreshToken.value) {
            logout()
            return
        }

        try {
            const response = await apiClient.post<AuthResponse>('/auth/refresh', {
                refreshToken: refreshToken.value
            } as RefreshTokenRequest)

            const data = response.data

            accessToken.value = data.accessToken
            refreshToken.value = data.refreshToken

            localStorage.setItem('auth_token', data.accessToken)
            localStorage.setItem('refresh_token', data.refreshToken)

            await fetchCurrentUser()
        } catch (err) {
            console.error('Token refresh failed:', err)
            logout()
        }
    }

    const initializeAuth = async () => {
        // Try to restore user from localStorage first
        const savedUser = localStorage.getItem('user')
        if (savedUser) {
            try {
                user.value = JSON.parse(savedUser)
            } catch (e) {
                console.error('Failed to parse saved user:', e)
            }
        }

        // Then fetch fresh data from backend if we have a token
        if (accessToken.value) {
            await fetchCurrentUser()
        }
    }


    return {
        user,
        accessToken,
        refreshToken,
        loading,
        error,
        isAuthenticated,
        isAdmin,
        login,
        signup,
        logout,
        refreshAccessToken,
        fetchCurrentUser,
        initializeAuth
    }
})