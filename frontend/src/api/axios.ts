import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 15000,
});

let isRefreshing = false;
let failedQueue: Array<{
    resolve: (value?: any) => void;
    reject: (reason?: any) => void;
}> = [];

const processQueue = (error: any = null, token: string | null = null) => {
    console.log('[AXIOS] Processing queue, items:', failedQueue.length, 'error:', !!error);
    failedQueue.forEach(promise => {
        if (error) {
            promise.reject(error);
        } else {
            promise.resolve(token);
        }
    });
    failedQueue = [];
};

apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        console.log('[AXIOS REQUEST]', config.method?.toUpperCase(), config.url);
        return config;
    },
    (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
    (response) => {
        console.log('[AXIOS RESPONSE]', response.status, response.config.url);
        return response;
    },

    async (error) => {
        const originalRequest = error.config;

        console.log('[AXIOS ERROR]', {
            url: originalRequest?.url,
            status: error.response?.status,
            _retry: originalRequest?._retry,
            isRefreshing
        });

        if (error.response?.status !== 401 || originalRequest._retry) {
            console.log('[AXIOS] Not handling error, rejecting');
            return Promise.reject(error);
        }

        if (isRefreshing) {
            console.log('[AXIOS] Refresh in progress, queueing request');
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            })
                .then(token => {
                    console.log('[AXIOS] Request from queue, retrying with new token');
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    return apiClient(originalRequest);
                })
                .catch(err => {
                    console.log('[AXIOS] Queue request failed');
                    return Promise.reject(err);
                });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken = localStorage.getItem('refresh_token');

        console.log('[AXIOS] Starting refresh, has refreshToken:', !!refreshToken);

        if (!refreshToken) {
            console.log('[AXIOS] No refresh token, redirecting to login');
            processQueue(new Error('No refresh token'), null);
            isRefreshing = false;

            localStorage.removeItem('auth_token');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('user');

            window.location.href = '/login';
            return Promise.reject(error);
        }

        try {
            console.log('[AXIOS] Calling /auth/refresh');
            const response = await axios.post(`${BASE_URL}/auth/refresh`, {
                refreshToken: refreshToken
            });

            console.log('[AXIOS] Refresh successful');

            const { accessToken, refreshToken: newRefreshToken } = response.data;

            localStorage.setItem('auth_token', accessToken);
            localStorage.setItem('refresh_token', newRefreshToken);

            originalRequest.headers.Authorization = `Bearer ${accessToken}`;

            processQueue(null, accessToken);

            console.log('[AXIOS] Retrying original request');
            return apiClient(originalRequest);

        } catch (refreshError: any) {
            console.error('[AXIOS] Refresh FAILED:', refreshError?.response?.status);

            processQueue(refreshError, null);

            localStorage.removeItem('auth_token');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('user');

            console.log('[AXIOS] Redirecting to /login due to refresh failure');
            window.location.href = '/login';

            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);