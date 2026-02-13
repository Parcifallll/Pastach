<template>
  <div class="min-h-screen flex items-center justify-center p-4" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <div class="max-w-md w-full space-y-8 bg-white/15 backdrop-blur-lg p-8 rounded-2xl shadow-2xl border border-white/20">
      <!-- Header -->
      <div class="text-center">
        <h2 class="text-4xl font-bold text-white mb-2">Добро пожаловать</h2>
        <p class="text-white/90">Войдите в свой аккаунт</p>
      </div>

      <!-- Error message -->
      <div v-if="errorMessage" class="bg-red-500/30 border border-red-300 text-white px-4 py-3 rounded-lg backdrop-blur-sm">
        {{ errorMessage }}
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-6">
        <!-- Username field -->
        <div>
          <label for="username" class="block text-sm font-medium text-white mb-2">
            Имя пользователя
          </label>
          <input
              id="username"
              v-model="form.username"
              type="text"
              required
              autocomplete="username"
              class="w-full px-4 py-3 bg-white/20 border border-white/30 rounded-lg text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-white/50 focus:border-transparent transition"
              placeholder="username"
          />
        </div>

        <!-- Password field -->
        <div>
          <label for="password" class="block text-sm font-medium text-white mb-2">
            Пароль
          </label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              required
              autocomplete="current-password"
              class="w-full px-4 py-3 bg-white/20 border border-white/30 rounded-lg text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-white/50 focus:border-transparent transition"
              placeholder="••••••••"
          />
        </div>

        <!-- Submit button -->
        <button
            type="submit"
            :disabled="isLoading"
            class="w-full py-3 px-4 bg-white text-purple-600 hover:bg-white/90 font-semibold rounded-lg shadow-lg hover:shadow-xl transform hover:scale-[1.02] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          <span v-if="!isLoading">Войти</span>
          <span v-else class="flex items-center justify-center">
            <svg class="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Вход...
          </span>
        </button>
      </form>

      <!-- Register link -->
      <div class="text-center">
        <p class="text-white/90">
          Нет аккаунта?
          <router-link to="/register" class="text-white font-semibold hover:underline transition">
            Зарегистрироваться
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { LoginRequest } from '@/types/models'

const router = useRouter()
const authStore = useAuthStore()

// Form state
const form = ref<LoginRequest>({
  username: '',
  password: ''
})

const isLoading = ref(false)
const errorMessage = ref<string | null>(null)

// Handle login
const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    await authStore.login(form.value)
    // Redirect to feed on success
    router.push('/feed')
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Неверное имя пользователя или пароль'
  } finally {
    isLoading.value = false
  }
}
</script>