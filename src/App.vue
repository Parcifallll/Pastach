<template>
  <div id="app">
    <nav v-if="authStore.isAuthenticated">
      <router-link to="/feed">Feed</router-link> |
      <router-link :to="`/profile/${authStore.user?.id}`">Profile</router-link> |
      <button @click="authStore.logout()">Logout</button>
    </nav>

    <nav v-else>
      <router-link to="/">Home</router-link> |
      <router-link to="/login">Login</router-link> |
      <router-link to="/register">Register</router-link>
    </nav>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

onMounted(async () => {
  await authStore.initializeAuth();
});
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

nav {
  padding: 20px;
  background: #f5f5f5;
  margin-bottom: 20px;
}

nav a {
  margin-right: 15px;
  text-decoration: none;
  color: #42b983;
}

nav a.router-link-active {
  font-weight: bold;
}

nav button {
  padding: 5px 15px;
  background: #ff4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>