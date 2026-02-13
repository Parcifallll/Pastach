<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 via-purple-900 to-violet-900">
    <!-- Header -->
    <header class="bg-gray-800/50 backdrop-blur-lg border-b border-gray-700 sticky top-0 z-50">
      <div class="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
        <h1 class="text-2xl font-bold text-white">Лента</h1>
        <div class="flex items-center space-x-4">
          <span class="text-gray-300">{{ authStore.user?.username }}</span>
          <button
              @click="handleLogout"
              class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition"
          >
            Выйти
          </button>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <main class="max-w-4xl mx-auto px-4 py-8">
      <!-- Create post form -->
      <div class="bg-gray-800/50 backdrop-blur-lg p-6 rounded-2xl shadow-xl border border-gray-700 mb-8">
        <h2 class="text-xl font-semibold text-white mb-4">Создать пост</h2>
        <form @submit.prevent="handleCreatePost" class="space-y-4">
          <textarea
              v-model="newPost.text"
              placeholder="Что у вас нового?"
              rows="3"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition resize-none"
          ></textarea>

          <div class="flex items-center space-x-4">
            <input
                v-model="newPost.photoUrl"
                type="url"
                placeholder="URL фото (опционально)"
                class="flex-1 px-4 py-2 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
            />
            <button
                type="submit"
                :disabled="isCreating || (!newPost.text?.trim() && !newPost.photoUrl?.trim())"
                class="px-6 py-2 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-700 hover:to-blue-700 text-white font-semibold rounded-lg shadow-lg hover:shadow-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isCreating ? 'Публикация...' : 'Опубликовать' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Error message -->
      <div v-if="errorMessage" class="bg-red-500/20 border border-red-500 text-red-200 px-4 py-3 rounded-lg mb-6">
        {{ errorMessage }}
      </div>

      <!-- Posts list -->
      <div class="space-y-6">
        <!-- Loading state -->
        <div v-if="postsStore.loading && postsStore.posts.length === 0" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-purple-500"></div>
          <p class="text-gray-300 mt-4">Загрузка постов...</p>
        </div>

        <!-- Posts -->
        <div
            v-for="post in postsStore.posts"
            :key="post.id"
            class="bg-gray-800/50 backdrop-blur-lg p-6 rounded-2xl shadow-xl border border-gray-700"
        >
          <!-- Post header -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-gradient-to-r from-purple-600 to-blue-600 rounded-full flex items-center justify-center text-white font-semibold">
                {{ getUserInitials(post.authorId) }}
              </div>
              <div>
                <p class="text-white font-semibold">User #{{ post.authorId }}</p>
                <p class="text-gray-400 text-sm">{{ formatDate(post.createdAt) }}</p>
              </div>
            </div>

            <!-- Delete button for own posts -->
            <button
                v-if="canDeletePost(post)"
                @click="handleDeletePost(post.id)"
                class="text-red-400 hover:text-red-300 transition"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
              </svg>
            </button>
          </div>

          <!-- Post content -->
          <div class="mb-4">
            <p class="text-gray-200 whitespace-pre-wrap">{{ post.text }}</p>
            <img
                v-if="post.photoUrl"
                :src="post.photoUrl"
                alt="Post image"
                class="mt-4 rounded-lg max-w-full h-auto"
            />
          </div>

          <!-- Post stats and actions -->
          <div class="flex items-center space-x-6 text-gray-400">
            <button
                @click="handleReaction(post.id, 'LIKE')"
                class="flex items-center space-x-2 hover:text-green-400 transition"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5"></path>
              </svg>
              <span>{{ post.likesCount }}</span>
            </button>
            <button
                @click="handleReaction(post.id, 'DISLIKE')"
                class="flex items-center space-x-2 hover:text-red-400 transition"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14H5.236a2 2 0 01-1.789-2.894l3.5-7A2 2 0 018.736 3h4.018a2 2 0 01.485.06l3.76.94m-7 10v5a2 2 0 002 2h.096c.5 0 .905-.405.905-.904 0-.715.211-1.413.608-2.008L17 13V4m-7 10h2m5-10h2a2 2 0 012 2v6a2 2 0 01-2 2h-2.5"></path>
              </svg>
              <span>{{ post.dislikesCount }}</span>
            </button>
            <div class="flex items-center space-x-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
              </svg>
              <span>{{ post.commentsCount }}</span>
            </div>
          </div>
        </div>

        <!-- No posts message -->
        <div v-if="!postsStore.loading && postsStore.posts.length === 0" class="text-center py-12">
          <p class="text-gray-400 text-lg">Пока нет постов. Создайте первый!</p>
        </div>

        <!-- Load more button -->
        <div v-if="postsStore.hasMore" class="text-center pt-6">
          <button
              @click="loadMore"
              :disabled="postsStore.loading"
              class="px-6 py-3 bg-gray-700 hover:bg-gray-600 text-white rounded-lg transition disabled:opacity-50"
          >
            {{ postsStore.loading ? 'Загрузка...' : 'Загрузить ещё' }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostsStore } from '@/stores/posts'
import { apiClient } from '@/api/axios'
import type { CreatePostRequest, ReactionType } from '@/types/models'

const router = useRouter()
const authStore = useAuthStore()
const postsStore = usePostsStore()

const newPost = ref<CreatePostRequest>({
  text: '',
  photoUrl: ''
})

const isCreating = ref(false)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  try {
    await postsStore.fetchPosts()
  } catch (error) {
    errorMessage.value = 'Не удалось загрузить посты'
  }
})

const handleCreatePost = async () => {
  isCreating.value = true
  errorMessage.value = null

  try {
    const postData: CreatePostRequest = {}
    if (newPost.value.text?.trim()) {
      postData.text = newPost.value.text
    }
    if (newPost.value.photoUrl?.trim()) {
      postData.photoUrl = newPost.value.photoUrl
    }

    await postsStore.createPost(postData)

    newPost.value = { text: '', photoUrl: '' }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось создать пост'
  } finally {
    isCreating.value = false
  }
}

const handleDeletePost = async (postId: number) => {
  if (!confirm('Вы уверены, что хотите удалить этот пост?')) return

  try {
    await postsStore.deletePost(postId)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось удалить пост'
  }
}

const handleReaction = async (postId: number, type: ReactionType) => {
  try {
    await apiClient.put(`/posts/${postId}/reactions`, { type })
    // Refresh posts to get updated counts
    await postsStore.fetchPosts(postsStore.currentPage)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось поставить реакцию'
  }
}

const loadMore = async () => {
  try {
    await postsStore.loadMorePosts()
  } catch (error) {
    errorMessage.value = 'Не удалось загрузить посты'
  }
}

const handleLogout = async () => {
  await authStore.logout()
}

const canDeletePost = (post: any) => {
  return post.authorId === authStore.user?.id || authStore.isAdmin
}

const getUserInitials = (userId: number) => {
  return `U${userId}`
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return 'только что'
  if (diffMins < 60) return `${diffMins} мин назад`
  if (diffHours < 24) return `${diffHours} ч назад`
  if (diffDays < 7) return `${diffDays} дн назад`

  return date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short', year: 'numeric' })
}
</script>

<style scoped>
</style>