import { defineStore } from 'pinia';
import { ref } from 'vue';
import { postsApi } from '@/api';
import type { Post } from '@/types/models';

export const usePostsStore = defineStore('posts', () => {
    // State
    const posts = ref<Post[]>([]);
    const currentPost = ref<Post | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const hasMore = ref(true);
    const currentPage = ref(0);
    const pageSize = ref(15);

    // Actions
    const fetchPosts = async (page: number = 0, size: number = 15) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await postsApi.getPosts(page, size);
            const newPosts = response._embedded.postResponseDTOList;

            if (page === 0) {
                posts.value = newPosts;
            } else {
                posts.value = [...posts.value, ...newPosts];
            }

            currentPage.value = page;
            hasMore.value = page < response.page.totalPages - 1;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch posts';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const fetchPostById = async (id: number) => {
        loading.value = true;
        error.value = null;

        try {
            const post = await postsApi.getPostById(id);
            currentPost.value = post;
            return post;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const fetchUserPosts = async (authorId: string, page: number = 0, size: number = 15) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await postsApi.getUserPosts(authorId, page, size);
            const newPosts = response._embedded.postResponseDTOList;

            if (page === 0) {
                posts.value = newPosts;
            } else {
                posts.value = [...posts.value, ...newPosts];
            }

            currentPage.value = page;
            hasMore.value = page < response.page.totalPages - 1;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch user posts';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const createPost = async (data: { text: string; photoUrl?: string | null }) => {
        loading.value = true;
        error.value = null;

        try {
            const newPost = await postsApi.createPost(data);
            posts.value = [newPost, ...posts.value]; // Add to beginning
            return newPost;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to create post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updatePost = async (id: number, data: { text: string; photoUrl?: string | null }) => {
        loading.value = true;
        error.value = null;

        try {
            const updatedPost = await postsApi.updatePost(id, data);

            // Update in posts array
            const index = posts.value.findIndex(p => p.id === id);
            if (index !== -1) {
                posts.value[index] = updatedPost;
            }

            // Update currentPost if it's the same
            if (currentPost.value?.id === id) {
                currentPost.value = updatedPost;
            }

            return updatedPost;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to update post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const deletePost = async (id: number) => {
        loading.value = true;
        error.value = null;

        try {
            await postsApi.deletePost(id);

            // Remove from posts array
            posts.value = posts.value.filter(p => p.id !== id);

            // Clear currentPost if it's the same
            if (currentPost.value?.id === id) {
                currentPost.value = null;
            }
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to delete post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const loadMorePosts = async () => {
        if (!hasMore.value || loading.value) return;
        await fetchPosts(currentPage.value + 1, pageSize.value);
    };

    const resetPosts = () => {
        posts.value = [];
        currentPost.value = null;
        currentPage.value = 0;
        hasMore.value = true;
        error.value = null;
    };

    return {
        // State
        posts,
        currentPost,
        loading,
        error,
        hasMore,
        currentPage,
        // Actions
        fetchPosts,
        fetchPostById,
        fetchUserPosts,
        createPost,
        updatePost,
        deletePost,
        loadMorePosts,
        resetPosts,
    };
});