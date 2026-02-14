import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient } from '@/api/axios'
import type { User, AuthResponse, LoginRequest, SignupRequest, RefreshTokenRequest } from '@/types/models'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null)
    const accessToken = ref<string | null>(localStorage.getItem('auth_token'))
    const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'))
    const loading = ref(false)
    const error = ref<string | null>(null)

    const isAuthenticated = computed(() => {
        const result = !!accessToken.value && !!user.value
        console.log('[AUTH] isAuthenticated:', result, { hasToken: !!accessToken.value, hasUser: !!user.value })
        return result
    })

    const isAdmin = computed(() => {
        return user.value?.roles?.some(role => role.name === 'ADMIN') ?? false
    })

    const login = async (credentials: LoginRequest) => {
        console.log('[AUTH] Login started')
        loading.value = true
        error.value = null

        try {
            const response = await apiClient.post<AuthResponse>('/auth/login', credentials)
            const data = response.data

            accessToken.value = data.accessToken
            refreshToken.value = data.refreshToken

            localStorage.setItem('auth_token', data.accessToken)
            localStorage.setItem('refresh_token', data.refreshToken)

            await fetchCurrentUser()
            console.log('[AUTH] Login complete')
        } catch (err: any) {
            console.error('[AUTH] Login failed:', err)
            error.value = err.response?.data?.message || 'Login error'
            throw err
        } finally {
            loading.value = false
        }
    }

    const signup = async (data: SignupRequest) => {
        console.log('[AUTH] Signup started')
        loading.value = true
        error.value = null

        try {
            const response = await apiClient.post<AuthResponse>('/auth/signup', data)
            const authData = response.data

            accessToken.value = authData.accessToken
            refreshToken.value = authData.refreshToken

            localStorage.setItem('auth_token', authData.accessToken)
            localStorage.setItem('refresh_token', authData.refreshToken)

            await fetchCurrentUser()
            console.log('[AUTH] Signup complete')

            return authData
        } catch (err: any) {
            console.error('[AUTH] Signup failed:', err)
            error.value = err.response?.data?.message || 'Registration error'
            throw err
        } finally {
            loading.value = false
        }
    }

    const fetchCurrentUser = async () => {
        console.log('[AUTH] fetchCurrentUser called, hasToken:', !!accessToken.value)

        if (!accessToken.value) {
            console.log('[AUTH] No token, skipping fetch')
            return
        }

        try {
            console.log('[AUTH] Fetching /users/me')
            const response = await apiClient.get<User>('/users/me')
            user.value = response.data

            localStorage.setItem('user', JSON.stringify(user.value))
            console.log('[AUTH] User fetched successfully:', user.value.username)
        } catch (err: any) {
            console.error('[AUTH] fetchCurrentUser error:', err?.response?.status, err?.message)
            console.log('[AUTH] NOT calling logout, letting interceptor handle it')
            // Не вызываем logout! Interceptor сам всё обработает
        }
    }

    const logout = async () => {
        console.log('[AUTH] ⚠️ LOGOUT CALLED!')
        console.trace('[AUTH] Logout stack trace')

        const currentRefreshToken = refreshToken.value

        user.value = null
        accessToken.value = null
        refreshToken.value = null

        localStorage.removeItem('auth_token')
        localStorage.removeItem('refresh_token')
        localStorage.removeItem('user')

        try {
            if (currentRefreshToken) {
                console.log('[AUTH] Calling backend /auth/logout')
                await apiClient.post('/auth/logout', {
                    refreshToken: currentRefreshToken
                })
            }
        } catch (err) {
            console.error('[AUTH] Backend logout error:', err)
        } finally {
            console.log('[AUTH] Redirecting to /login')
            router.push('/login')
        }
    }

    const refreshAccessToken = async () => {
        console.log('[AUTH] Manual refreshAccessToken called')

        if (!refreshToken.value) {
            console.log('[AUTH] No refresh token, calling logout')
            await logout()
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
            console.log('[AUTH] Manual refresh successful')
        } catch (err) {
            console.error('[AUTH] Manual refresh failed:', err)
            await logout()
        }
    }

    const initializeAuth = async () => {
        console.log('[AUTH] ========== INITIALIZE AUTH ==========')

        const savedUser = localStorage.getItem('user')
        if (savedUser) {
            try {
                user.value = JSON.parse(savedUser)
                console.log('[AUTH] Restored user from localStorage:', user.value?.username)
            } catch (e) {
                console.error('[AUTH] Failed to parse saved user:', e)
            }
        }

        if (accessToken.value) {
            console.log('[AUTH] Has access token, fetching current user')
            await fetchCurrentUser()
        } else {
            console.log('[AUTH] No access token')
        }

        console.log('[AUTH] ========== INITIALIZE COMPLETE ==========')
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