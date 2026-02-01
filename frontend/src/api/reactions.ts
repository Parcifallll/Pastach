import { apiClient } from './axios';
import type { ReactionType } from '@/types/models';

interface ReactionCreateDTO {
    type: ReactionType;
}

export const reactionsApi = {
    // PUT /posts/{postId}/reactions
    togglePostReaction: async (postId: number, type: ReactionType): Promise<void> => {
        const data: ReactionCreateDTO = { type };
        await apiClient.put(`/posts/${postId}/reactions`, data);
    },

    // PUT /posts/{postId}/comments/{commentId}/reactions
    toggleCommentReaction: async (
        postId: number,
        commentId: number,
        type: ReactionType
    ): Promise<void> => {
        const data: ReactionCreateDTO = { type };
        await apiClient.put(`/posts/${postId}/comments/${commentId}/reactions`, data);
    },
};