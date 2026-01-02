<template>
  <div class="register-view">
    <h1>Create Your Account</h1>

    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="id">User ID:</label>
        <input
            id="id"
            v-model="formData.id"
            type="text"
            required
            placeholder="unique_user_id"
            pattern="[a-zA-Z0-9_-]+"
            title="Only letters, numbers, underscores and hyphens"
        />
        <small>Unique identifier (letters, numbers, _ and - only)</small>
      </div>

      <div class="form-group">
        <label for="email">Email:</label>
        <input
            id="email"
            v-model="formData.email"
            type="email"
            required
            placeholder="your@email.com"
        />
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="firstName">First Name:</label>
          <input
              id="firstName"
              v-model="formData.firstName"
              type="text"
              required
              placeholder="John"
          />
        </div>

        <div class="form-group">
          <label for="lastName">Last Name:</label>
          <input
              id="lastName"
              v-model="formData.lastName"
              type="text"
              required
              placeholder="Doe"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="birthday">Birthday (optional):</label>
        <input
            id="birthday"
            v-model="formData.birthday"
            type="date"
        />
      </div>

      <div class="form-group">
        <label for="password">Password:</label>
        <input
            id="password"
            v-model="formData.password"
            type="password"
            required
            minlength="8"
            placeholder="••••••••"
        />
        <small>At least 8 characters</small>
      </div>

      <div class="form-group">
        <label for="confirmPassword">Confirm Password:</label>
        <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            required
            placeholder="••••••••"
        />
      </div>

      <div v-if="validationError" class="error">
        {{ validationError }}
      </div>

      <div v-if="authStore.error" class="error">
        {{ authStore.error }}
      </div>

      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? 'Creating Account...' : 'Sign Up' }}
      </button>

      <p class="login-link">
        Already have an account?
        <router-link to="/login">Login here</router-link>
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

// Form data
const formData = ref({
  id: '',
  email: '',
  firstName: '',
  lastName: '',
  birthday: '',
  password: '',
});

const confirmPassword = ref('');
const validationError = ref('');

const validateForm = (): boolean => {
  validationError.value = '';

  // Check passwords match
  if (formData.value.password !== confirmPassword.value) {
    validationError.value = 'Passwords do not match';
    return false;
  }

  // Check password length
  if (formData.value.password.length < 8) {
    validationError.value = 'Password must be at least 8 characters';
    return false;
  }

  // Check user ID format
  const userIdRegex = /^[a-zA-Z0-9_-]+$/;
  if (!userIdRegex.test(formData.value.id)) {
    validationError.value = 'User ID can only contain letters, numbers, underscores and hyphens';
    return false;
  }

  return true;
};

const handleRegister = async () => {
  if (!validateForm()) {
    return;
  }

  try {
    await authStore.signup({
      id: formData.value.id,
      email: formData.value.email,
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      birthday: formData.value.birthday || undefined,
      password: formData.value.password,
    });
  } catch (err) {
    console.error('Registration error:', err);
  }
};
</script>

<style scoped>
.register-view {
  max-width: 500px;
  margin: 50px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #2c3e50;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: #42b983;
}

small {
  display: block;
  margin-top: 5px;
  color: #666;
  font-size: 12px;
}

button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: transform 0.2s;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.error {
  color: #ff4444;
  background: #ffebee;
  padding: 12px;
  border-radius: 6px;
  margin: 15px 0;
  font-size: 14px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>