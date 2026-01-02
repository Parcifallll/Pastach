import { apiClient } from './axios';
import type { Comment } from '@/types/models';

// HATEOAS PagedResponse for comments
interface PagedCommentsResponse {
    _embedded: {
        commentResponseDTOList: Comment[];
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

interface CommentCreateDTO {
    text: string;
    photoUrl?: string | null;
}

interface CommentUpdateDTO {
    text: string;
    photoUrl?: string | null;
}

export const commentsApi = {
    // GET /posts/{postId}/comments?page=0&size=10
    getCommentsByPostId: async (
        postId: number,
        page: number = 0,
        size: number = 10
    ): Promise<PagedCommentsResponse> => {
        const response = await apiClient.get<PagedCommentsResponse>(
            `/posts/${postId}/comments`,
            {
                params: { page, size, sort: 'createdAt,desc' },
            }
        );
        return response.data;
    },

    // POST /posts/{postId}/comments
    createComment: async (postId: number, data: CommentCreateDTO): Promise<Comment> => {
        const response = await apiClient.post<Comment>(`/posts/${postId}/comments`, data);
        return response.data;
    },

    // PATCH /posts/{postId}/comments/{commentId}
    updateComment: async (
        postId: number,
        commentId: number,
        data: CommentUpdateDTO
    ): Promise<Comment> => {
        const response = await apiClient.patch<Comment>(
            `/posts/${postId}/comments/${commentId}`,
            data
        );
        return response.data;
    },

    // DELETE /posts/{postId}/comments/{commentId}
    deleteComment: async (postId: number, commentId: number): Promise<void> => {
        await apiClient.delete(`/posts/${postId}/comments/${commentId}`);
    },
};