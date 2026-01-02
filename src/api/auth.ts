import { apiClient } from './axios';

// Use types from models.ts instead of duplicating
interface JwtResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
}

interface SignupDTO {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    birthday: string; // "YYYY-MM-DD"
    password: string;
}

interface LoginDTO {
    email: string;
    password: string;
}

export const authApi = {
    // POST /auth/signup
    signup: async (data: SignupDTO): Promise<JwtResponse> => {
        const response = await apiClient.post<JwtResponse>('/auth/signup', data);
        return response.data;
    },

    // POST /auth/login
    login: async (credentials: LoginDTO): Promise<JwtResponse> => {
        const response = await apiClient.post<JwtResponse>('/auth/login', credentials);
        return response.data;
    },

    // POST /auth/refresh
    refreshToken: async (refreshToken: string): Promise<JwtResponse> => {
        const response = await apiClient.post<JwtResponse>('/auth/refresh', { refreshToken });
        return response.data;
    },

    // Client-side logout (clear tokens)
    logout: () => {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('user');
    },
};