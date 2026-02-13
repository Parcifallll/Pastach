import { defineStore } from 'pinia';
import { ref } from 'vue';
import { apiClient } from '@/api/axios';
import type { Post, PagedModel, CreatePostRequest, UpdatePostRequest } from '@/types/models';

export const usePostsStore = defineStore('posts', () => {
    // State
    const posts = ref<Post[]>([]);
    const currentPost = ref<Post | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const hasMore = ref(true);
    const currentPage = ref(0);
    const pageSize = ref(15);
    const totalPages = ref(0);
    const totalElements = ref(0);

    // Actions
    const fetchPosts = async (page: number = 0, size: number = 15) => {
        loading.value = true;
        error.value = null;

        try {
            // GET /posts with pagination params
            const response = await apiClient.get<PagedModel<Post>>('/posts', {
                params: { page, size, sort: 'createdAt,desc' }
            });

            const data = response.data;

            // Extract posts from HATEOAS response
            const newPosts = data._embedded?.postResponseDTOList || [];

            // Replace or append posts based on page number
            if (page === 0) {
                posts.value = newPosts;
            } else {
                posts.value = [...posts.value, ...newPosts];
            }

            // Update pagination metadata
            currentPage.value = data.page?.number || page;
            totalPages.value = data.page?.totalPages || 0;
            totalElements.value = data.page?.totalElements || 0;
            pageSize.value = data.page?.size || size;

            // Check if there are more pages
            hasMore.value = (currentPage.value + 1) < totalPages.value;
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
            const response = await apiClient.get<Post>(`/posts/${id}`);
            currentPost.value = response.data;
            return response.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const fetchUserPosts = async (authorId: number, page: number = 0, size: number = 15) => {
        loading.value = true;
        error.value = null;

        try {
            // GET /posts/users/{authorId}/posts
            const response = await apiClient.get<PagedModel<Post>>(`/posts/users/${authorId}/posts`, {
                params: { page, size, sort: 'createdAt,desc' }
            });

            const data = response.data;
            const newPosts = data._embedded?.postResponseDTOList || [];

            if (page === 0) {
                posts.value = newPosts;
            } else {
                posts.value = [...posts.value, ...newPosts];
            }

            currentPage.value = data.page?.number || page;
            totalPages.value = data.page?.totalPages || 0;
            totalElements.value = data.page?.totalElements || 0;
            hasMore.value = (currentPage.value + 1) < totalPages.value;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to fetch user posts';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const createPost = async (data: CreatePostRequest) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await apiClient.post<Post>('/posts', data);
            const newPost = response.data;

            // Add to beginning of posts array
            posts.value = [newPost, ...posts.value];
            totalElements.value += 1;

            return newPost;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Failed to create post';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updatePost = async (id: number, data: UpdatePostRequest) => {
        loading.value = true;
        error.value = null;

        try {
            const response = await apiClient.patch<Post>(`/posts/${id}`, data);
            const updatedPost = response.data;

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
            await apiClient.delete(`/posts/${id}`);

            // Remove from posts array
            posts.value = posts.value.filter(p => p.id !== id);
            totalElements.value = Math.max(0, totalElements.value - 1);

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

    // Infinite scroll: load next page
    const loadMorePosts = async () => {
        if (!hasMore.value || loading.value) return;
        await fetchPosts(currentPage.value + 1, pageSize.value);
    };

    const resetPosts = () => {
        posts.value = [];
        currentPost.value = null;
        currentPage.value = 0;
        totalPages.value = 0;
        totalElements.value = 0;
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
        pageSize,
        totalPages,
        totalElements,
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