<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 via-purple-900 to-violet-900 flex items-center justify-center p-4">
    <div class="max-w-md w-full space-y-8 bg-gray-800/50 backdrop-blur-lg p-8 rounded-2xl shadow-2xl border border-gray-700">
      <!-- Header -->
      <div class="text-center">
        <h2 class="text-4xl font-bold text-white mb-2">Регистрация</h2>
        <p class="text-gray-300">Создайте новый аккаунт</p>
      </div>

      <!-- Error message -->
      <div v-if="errorMessage" class="bg-red-500/20 border border-red-500 text-red-200 px-4 py-3 rounded-lg">
        {{ errorMessage }}
      </div>

      <!-- Form -->
      <form @submit.prevent="handleRegister" class="space-y-4">
        <!-- Username field (required) -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-200 mb-2">
            Имя пользователя *
          </label>
          <input
              id="username"
              v-model="form.username"
              type="text"
              required
              autocomplete="username"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
              placeholder="username"
          />
        </div>

        <!-- Email field (optional) -->
        <div>
          <label for="email" class="block text-sm font-medium text-gray-200 mb-2">
            Email (опционально)
          </label>
          <input
              id="email"
              v-model="form.email"
              type="email"
              autocomplete="email"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
              placeholder="example@mail.com"
          />
        </div>

        <!-- First Name -->
        <div>
          <label for="firstName" class="block text-sm font-medium text-gray-200 mb-2">
            Имя *
          </label>
          <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              required
              autocomplete="given-name"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
              placeholder="Иван"
          />
        </div>

        <!-- Last Name -->
        <div>
          <label for="lastName" class="block text-sm font-medium text-gray-200 mb-2">
            Фамилия *
          </label>
          <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              required
              autocomplete="family-name"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
              placeholder="Иванов"
          />
        </div>

        <!-- Birthday (optional) -->
        <div>
          <label for="birthday" class="block text-sm font-medium text-gray-200 mb-2">
            Дата рождения (опционально)
          </label>
          <input
              id="birthday"
              v-model="form.birthday"
              type="date"
              autocomplete="bday"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
          />
        </div>

        <!-- Password field -->
        <div>
          <label for="password" class="block text-sm font-medium text-gray-200 mb-2">
            Пароль *
          </label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              required
              autocomplete="new-password"
              minlength="6"
              class="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
              placeholder="••••••••"
          />
          <p class="text-xs text-gray-400 mt-1">Минимум 6 символов</p>
        </div>

        <!-- Submit button -->
        <button
            type="submit"
            :disabled="isLoading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-700 hover:to-blue-700 text-white font-semibold rounded-lg shadow-lg hover:shadow-xl transform hover:scale-[1.02] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          <span v-if="!isLoading">Зарегистрироваться</span>
          <span v-else class="flex items-center justify-center">
            <svg class="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Регистрация...
          </span>
        </button>
      </form>

      <!-- Login link -->
      <div class="text-center">
        <p class="text-gray-300">
          Уже есть аккаунт?
          <router-link to="/login" class="text-purple-400 hover:text-purple-300 font-semibold transition">
            Войти
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
import type { SignupRequest } from '@/types/models'

const router = useRouter()
const authStore = useAuthStore()

// Form state
const form = ref<SignupRequest>({
  username: '',
  email: undefined, // Optional field
  password: '',
  firstName: '',
  lastName: '',
  birthday: undefined // Optional field
})

const isLoading = ref(false)
const errorMessage = ref<string | null>(null)

// Handle registration
const handleRegister = async () => {
  isLoading.value = true
  errorMessage.value = null

  try {
    // Clean up empty optional fields
    const signupData: SignupRequest = {
      username: form.value.username,
      password: form.value.password,
      firstName: form.value.firstName,
      lastName: form.value.lastName,
    }

    // Add optional fields only if they have values
    if (form.value.email && form.value.email.trim()) {
      signupData.email = form.value.email
    }
    if (form.value.birthday && form.value.birthday.trim()) {
      signupData.birthday = form.value.birthday
    }

    await authStore.signup(signupData)
    // Redirect to feed on success (backend returns tokens immediately)
    router.push('/feed')
  } catch (error: any) {
    // Handle validation errors
    if (error.response?.data?.details) {
      const details = error.response.data.details
      errorMessage.value = details.map((d: any) => `${d.field}: ${d.message}`).join(', ')
    } else {
      errorMessage.value = error.response?.data?.message || 'Ошибка регистрации. Попробуйте снова.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* Additional styling if needed */
</style>