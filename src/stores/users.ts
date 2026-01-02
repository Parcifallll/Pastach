import { defineStore } from 'pinia';
import { ref } from 'vue';
import { usersApi } from '@/api';
import type { User } from '@/types/models';

export const useUsersStore = defineStore('users', () => {
    // State
    const users = ref<Map<string, User>>(new Map());
    const currentUser = ref<User | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);

    // Actions
    const fetchUserById = async (id: string, forceRefresh = false) => {
        // Check cache first
        if (!forceRefresh && users.value.has(id)) {
            currentUser.value = users.value.get(id)!;
            return currentUser.value;
        }

        loading.value = true;
        error.value = null;

        try {
            const user = await usersApi.getUserById(id);
            users.value.set(id, user);
            currentUser.value = user;
            return user;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch user';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updateUser = async (id: string, data: {
        firstName?: string;
        lastName?: string;
        birthday?: string;
    }) => {
        loading.value = true;
        error.value = null;

        try {
            const updatedUser = await usersApi.updateUser(id, data);
            users.value.set(id, updatedUser);
            currentUser.value = updatedUser;
            return updatedUser;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to update user';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const getCachedUser = (id: string): User | undefined => {
        return users.value.get(id);
    };

    return {
        // State
        users,
        currentUser,
        loading,
        error,
        // Actions
        fetchUserById,
        updateUser,
        getCachedUser,
    };
});