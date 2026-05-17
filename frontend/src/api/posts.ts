import { apiClient } from './axios';
import type { Post } from '@/types/models';

// HATEOAS PagedResponse for posts
interface PagedPostsResponse {
    _embedded: {
        postResponseDTOList: Post[];
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

interface PostCreateDTO {
    text: string;
    photoUrl?: string | null;
}

interface PostUpdateDTO {
    text: string;
    photoUrl?: string | null;
}

interface RecommendationViewDTO {
    viewedAt: string;
    viewDuration: number;
}

export const postsApi = {
    // GET /posts?page=0&size=15&sort=createdAt,desc
    getPosts: async (page: number = 0, size: number = 15): Promise<PagedPostsResponse> => {
        const response = await apiClient.get<PagedPostsResponse>('/posts', {
            params: {
                page,
                size,
                sort: 'createdAt,desc'
            },
        });
        return response.data;
    },

    // GET /posts/{id}
    getPostById: async (id: number): Promise<Post> => {
        const response = await apiClient.get<Post>(`/posts/${id}`);
        return response.data;
    },

    // POST /posts
    createPost: async (data: PostCreateDTO): Promise<Post> => {
        const response = await apiClient.post<Post>('/posts', data);
        return response.data;
    },

    // PATCH /posts/{postId}
    updatePost: async (id: number, data: PostUpdateDTO): Promise<Post> => {
        const response = await apiClient.patch<Post>(`/posts/${id}`, data);
        return response.data;
    },

    // DELETE /posts/{postId}
    deletePost: async (id: number): Promise<void> => {
        await apiClient.delete(`/posts/${id}`);
    },

    // GET /posts/users/{authorId}/posts?page=0&size=15
    getUserPosts: async (
        authorId: string,
        page: number = 0,
        size: number = 15
    ): Promise<PagedPostsResponse> => {
        const response = await apiClient.get<PagedPostsResponse>(
            `/posts/users/${authorId}/posts`,
            {
                params: { page, size, sort: 'createdAt,desc' },
            }
        );
        return response.data;
    },

    // POST /posts/recommendations/{postId}/view
    reportRecommendationView: async (postId: number, viewedAt: string, viewDuration: number): Promise<void> => {
        const payload: RecommendationViewDTO = {
            viewedAt,
            viewDuration
        };
        await apiClient.post(`/posts/recommendations/${postId}/view`, payload);
    }
};