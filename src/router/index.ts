import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

// Routes configuration
const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/LoginView.vue'),
        meta: { requiresAuth: false, guestOnly: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/RegisterView.vue'),
        meta: { requiresAuth: false, guestOnly: true }
    },
    {
        path: '/profile/:id',
        name: 'Profile',
        component: () => import('@/views/ProfileView.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/post/:id',
        name: 'Post',
        component: () => import('@/views/PostView.vue'),
        meta: { requiresAuth: false }
    },
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
});

// Navigation guards with authStore
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();

    // Initialize auth on first navigation
    if (!authStore.user && authStore.accessToken) {
        await authStore.initializeAuth();
    }

    const isAuthenticated = authStore.isAuthenticated;

    // Check if route requires authentication
    if (to.meta.requiresAuth && !isAuthenticated) {
        next({ name: 'Login' });
    }
    // Redirect authenticated users from login/register pages
    else if (to.meta.guestOnly && isAuthenticated) {
        next({ name: 'Home' });
    }
    else {
        next();
    }
});

export default router;