import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authApi, usersApi } from '@/api';
import type { User } from '@/types/models';
import router from '@/router';

export const useAuthStore = defineStore('auth', () => {
    // State
    const user = ref<User | null>(null);
    const accessToken = ref<string | null>(localStorage.getItem('auth_token'));
    const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'));
    const loading = ref(false);
    const error = ref<string | null>(null);

    // Getters
    const isAuthenticated = computed(() => !!accessToken.value && !!user.value);
    const isAdmin = computed(() => {
        return user.value?.roles.some(role => role.name === 'ADMIN') ?? false;
    });

    // Actions
    const login = async (email: string, password: string) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await authApi.login({ email, password });

            // Store tokens
            accessToken.value = response.accessToken;
            refreshToken.value = response.refreshToken;
            localStorage.setItem('auth_token', response.accessToken);
            localStorage.setItem('refresh_token', response.refreshToken);

            // Fetch current user data
            await fetchCurrentUser();

            router.push({ name: 'Feed' });
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Login failed';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const signup = async (data: {
        id: string;
        email: string;
        firstName: string;
        lastName: string;
        birthday: string;
        password: string;
    }) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await authApi.signup(data);

            // Store tokens
            accessToken.value = response.accessToken;
            refreshToken.value = response.refreshToken;
            localStorage.setItem('auth_token', response.accessToken);
            localStorage.setItem('refresh_token', response.refreshToken);

            // Fetch current user data
            await fetchCurrentUser();

            router.push({ name: 'Feed' });
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Signup failed';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const logout = () => {
        user.value = null;
        accessToken.value = null;
        refreshToken.value = null;
        authApi.logout();
        router.push({ name: 'Login' });
    };

    const fetchCurrentUser = async () => {
        if (!accessToken.value) return;

        try {
            const userData = await usersApi.getCurrentUser();
            user.value = userData;
            localStorage.setItem('user', JSON.stringify(userData));
        } catch (err) {
            console.error('Failed to fetch current user:', err);
            logout();
        }
    };

    const refreshAccessToken = async () => {
        if (!refreshToken.value) {
            logout();
            return;
        }

        try {
            const response = await authApi.refreshToken(refreshToken.value);
            accessToken.value = response.accessToken;
            localStorage.setItem('auth_token', response.accessToken);
        } catch (err) {
            console.error('Token refresh failed:', err);
            logout();
        }
    };

    // Initialize store from localStorage
    const initializeAuth = async () => {
        const storedUser = localStorage.getItem('user');
        if (storedUser && accessToken.value) {
            try {
                user.value = JSON.parse(storedUser);
                // Verify token is still valid by fetching current user
                await fetchCurrentUser();
            } catch (err) {
                logout();
            }
        }
    };

    return {
        // State
        user,
        accessToken,
        refreshToken,
        loading,
        error,
        // Getters
        isAuthenticated,
        isAdmin,
        // Actions
        login,
        signup,
        logout,
        fetchCurrentUser,
        refreshAccessToken,
        initializeAuth,
    };
});