import { apiClient } from './axios';
import type { User } from '@/types/models';

// HATEOAS PagedResponse for users
interface PagedUsersResponse {
    _embedded: {
        userResponseDTOList: User[];
    };
    _links: {
        self: { href: string };
        next?: { href: string };
        prev?: { href: string };
    };
    page: {
        size: number;
        totalElements: number;
        totalPages: number;
        number: number;
    };
}

interface UserUpdateDTO {
    firstName?: string;
    lastName?: string;
    birthday?: string; // "YYYY-MM-DD"
}

interface PasswordChangeDTO {
    currentPassword: string;
    newPassword: string;
}

export const usersApi = {
    // GET /users/me
    getCurrentUser: async (): Promise<User> => {
        const response = await apiClient.get<User>('/users/me');
        return response.data;
    },

    // GET /users/{id}
    getUserById: async (id: string): Promise<User> => {
        const response = await apiClient.get<User>(`/users/${id}`);
        return response.data;
    },

    // GET /users?page=0&size=15
    getAllUsers: async (page: number = 0, size: number = 15): Promise<PagedUsersResponse> => {
        const response = await apiClient.get<PagedUsersResponse>('/users', {
            params: { page, size, sort: 'createdAt,desc' },
        });
        return response.data;
    },

    // PATCH /users/{id}
    updateUser: async (id: string, data: UserUpdateDTO): Promise<User> => {
        const response = await apiClient.patch<User>(`/users/${id}`, data);
        return response.data;
    },

    // DELETE /users/{userId}
    deleteUser: async (userId: string): Promise<void> => {
        await apiClient.delete(`/users/${userId}`);
    },

    // PUT /users/{id}/password
    changePassword: async (id: string, data: PasswordChangeDTO): Promise<void> => {
        await apiClient.put(`/users/${id}/password`, data);
    },
};