<template>
  <div class="login-view">
    <h1>Login to Pastach</h1>

    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="email">Email:</label>
        <input
            id="email"
            v-model="email"
            type="email"
            required
            placeholder="your@email.com"
        />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input
            id="password"
            v-model="password"
            type="password"
            required
            placeholder="••••••••"
        />
      </div>

      <div v-if="authStore.error" class="error">
        {{ authStore.error }}
      </div>

      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? 'Logging in...' : 'Login' }}
      </button>

      <p class="register-link">
        Don't have an account?
        <router-link to="/register">Register here</router-link>
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const email = ref('');
const password = ref('');

const handleLogin = async () => {
  try {
    await authStore.login(email.value, password.value);
  } catch (err) {
    console.error('Login error:', err);
  }
};
</script>

<style scoped>
.login-view {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 12px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error {
  color: red;
  margin: 10px 0;
}

.register-link {
  text-align: center;
  margin-top: 15px;
}
</style>