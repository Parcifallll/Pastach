<template>
  <div class="min-h-screen" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <!-- Header -->
    <header class="bg-white/15 backdrop-blur-lg border-b border-white/20 sticky top-0 z-50">
      <div class="max-w-6xl mx-auto px-4 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <button
                @click="$router.back()"
                class="p-2 text-white hover:text-white/80 transition"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
              </svg>
            </button>
            <h1 class="text-2xl font-bold text-white">Рекомендации для вас</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-white">{{ authStore.user?.username }}</span>
            <button
                @click="handleLogout"
                class="px-4 py-2 bg-red-500/80 hover:bg-red-600/80 text-white rounded-lg transition"
            >
              Выйти
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <main class="max-w-4xl mx-auto px-4 py-8">
      <!-- Info banner -->
      <div class="bg-white/20 border border-white/30 text-white px-6 py-4 rounded-2xl mb-8 backdrop-blur-sm">
        <div class="flex items-start space-x-3">
          <svg class="w-6 h-6 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <div>
            <h3 class="font-semibold mb-1">Персонализированная лента</h3>
            <p class="text-sm text-white/90">Эти посты подобраны специально для вас на основе ваших интересов и активности</p>
          </div>
        </div>
      </div>

      <!-- Error message -->
      <div v-if="errorMessage" class="bg-red-500/30 border border-red-300 text-white px-4 py-3 rounded-lg mb-6 backdrop-blur-sm">
        {{ errorMessage }}
      </div>

      <!-- Posts list -->
      <div class="space-y-6">
        <!-- Loading state -->
        <div v-if="postsStore.recommendationsLoading && (!postsStore.recommendedPosts || postsStore.recommendedPosts.length === 0)" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-white"></div>
          <p class="text-white mt-4">Подбираем рекомендации...</p>
        </div>

        <!-- Posts -->
        <div
            v-for="post in postsStore.recommendedPosts"
            :key="post.id"
            class="bg-white/15 backdrop-blur-lg p-6 rounded-2xl shadow-xl border border-white/20"
        >
          <!-- Post header -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-white/30 rounded-full flex items-center justify-center text-white font-semibold">
                {{ getUserInitials(post.authorId) }}
              </div>
              <div>
                <p class="text-white font-semibold">Пользователь #{{ post.authorId }}</p>
                <p class="text-white/70 text-sm">{{ formatDate(post.createdAt) }}</p>
              </div>
            </div>

            <!-- Delete button for own posts or admin -->
            <button
                v-if="canDeletePost(post)"
                @click="handleDeletePost(post.id)"
                class="text-red-300 hover:text-red-200 transition"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
              </svg>
            </button>
          </div>

          <!-- Post content -->
          <div class="mb-4">
            <p class="text-white whitespace-pre-wrap">{{ post.text }}</p>
            <img
                v-if="post.photoUrl"
                :src="post.photoUrl"
                alt="Post image"
                class="mt-4 rounded-lg max-w-full h-auto"
            />
          </div>

          <!-- Post stats and actions -->
          <div class="flex items-center space-x-6 text-white/80">
            <button
                @click="handleReaction(post.id, 'LIKE')"
                class="flex items-center space-x-2 hover:text-green-300 transition"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5"></path>
              </svg>
              <span>{{ post.likesCount }}</span>
            </button>
            <button
                @click="handleReaction(post.id, 'DISLIKE')"
                class="flex items-center space-x-2 hover:text-red-300 transition"
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
        <div v-if="!postsStore.recommendationsLoading && (!postsStore.recommendedPosts || postsStore.recommendedPosts.length === 0)" class="text-center py-12">
          <svg class="w-16 h-16 mx-auto text-white/70 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <p class="text-white text-lg">Пока нет рекомендаций</p>
          <p class="text-white/70 text-sm mt-2">Начните взаимодействовать с постами, чтобы получить персональные рекомендации</p>
          <button
              @click="$router.push('/feed')"
              class="mt-4 px-6 py-2 bg-white text-purple-600 hover:bg-white/90 rounded-lg transition"
          >
            Перейти к основной ленте
          </button>
        </div>

        <!-- Infinite scroll trigger -->
        <div
            ref="loadMoreTrigger"
            v-if="postsStore.recommendationsHasMore && postsStore.recommendedPosts && postsStore.recommendedPosts.length > 0"
            class="text-center py-6"
        >
          <div v-if="postsStore.recommendationsLoading" class="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-white"></div>
        </div>

        <!-- End of feed message -->
        <div v-if="!postsStore.recommendationsHasMore && postsStore.recommendedPosts && postsStore.recommendedPosts.length > 0" class="text-center py-6">
          <p class="text-white">Вы просмотрели все рекомендации 🎉</p>
          <p class="text-white/70 text-sm mt-2">Загружено постов: {{ postsStore.recommendedPosts.length }}</p>
          <button
              @click="refreshRecommendations"
              class="mt-4 px-6 py-2 bg-white text-purple-600 hover:bg-white/90 rounded-lg transition"
          >
            Обновить рекомендации
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePostsStore } from '@/stores/posts'
import { apiClient } from '@/api/axios'
import type { ReactionType } from '@/types/models'

const router = useRouter()
const authStore = useAuthStore()
const postsStore = usePostsStore()

const errorMessage = ref<string | null>(null)

// Infinite scroll observer
const loadMoreTrigger = ref<HTMLElement | null>(null)
let observer: IntersectionObserver | null = null

// Load recommendations on mount
onMounted(async () => {
  try {
    // Reset previous recommendations
    postsStore.resetRecommendations()

    // Load first batch
    await postsStore.fetchRecommendations(0, 10)

    // Setup infinite scroll observer
    setupInfiniteScroll()
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось загрузить рекомендации'
  }
})

// Cleanup observer on unmount
onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})

// Setup Intersection Observer for infinite scroll
const setupInfiniteScroll = () => {
  // Small delay to ensure DOM is rendered
  setTimeout(() => {
    if (!loadMoreTrigger.value) {
      return
    }

    observer = new IntersectionObserver(
        (entries) => {
          const firstEntry = entries[0]
          if (firstEntry.isIntersecting && postsStore.recommendationsHasMore && !postsStore.recommendationsLoading) {
            loadMore()
          }
        },
        {
          rootMargin: '100px',
          threshold: 0.1
        }
    )

    observer.observe(loadMoreTrigger.value)
  }, 100)
}

// Refresh recommendations from beginning
const refreshRecommendations = async () => {
  try {
    postsStore.resetRecommendations()
    await postsStore.fetchRecommendations(0, 10)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось обновить рекомендации'
  }
}

// Delete post handler
const handleDeletePost = async (postId: number) => {
  if (!confirm('Вы уверены, что хотите удалить этот пост?')) return

  try {
    await postsStore.deletePost(postId)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось удалить пост'
  }
}

// Reaction handler
const handleReaction = async (postId: number, type: ReactionType) => {
  try {
    await apiClient.put(`/posts/${postId}/reactions`, { type })
    const currentOffset = postsStore.recommendationsOffset
    const currentLimit = postsStore.recommendationsLimit
    await postsStore.fetchRecommendations(0, currentOffset + currentLimit)
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Не удалось поставить реакцию'
  }
}

// Load more recommendations
const loadMore = async () => {
  try {
    await postsStore.loadMoreRecommendations()
  } catch (error: any) {
    errorMessage.value = 'Не удалось загрузить рекомендации'
  }
}

// Logout handler
const handleLogout = async () => {
  await authStore.logout()
}

// Helper: check if user can delete post
const canDeletePost = (post: any) => {
  return post.authorId === authStore.user?.id || authStore.isAdmin
}

// Helper: get user initials
const getUserInitials = (userId: number) => {
  return `U${userId}`
}

// Helper: format date
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