<template>
  <div class="welcome-view">
    <div class="hero">
      <h1>Welcome to Pastach 🎉</h1>
      <p class="tagline">Your new favorite social media platform</p>

      <div class="features">
        <div class="feature">
          <h3>📝 Share Your Thoughts</h3>
          <p>Post text and photos with your friends</p>
        </div>
        <div class="feature">
          <h3>💬 Engage</h3>
          <p>Comment and react to posts</p>
        </div>
        <div class="feature">
          <h3>👥 Connect</h3>
          <p>Build your network and follow others</p>
        </div>
      </div>

      <div class="cta-buttons">
        <router-link to="/register" class="btn btn-primary">
          Get Started
        </router-link>
        <router-link to="/login" class="btn btn-secondary">
          Sign In
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// Redirect to feed if already logged in
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push({ name: 'Feed' });
  }
});
</script>

<style scoped>
.welcome-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.hero {
  text-align: center;
  max-width: 1000px;
  padding: 40px;
}

h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.tagline {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.feature {
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.feature h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 15px 40px;
  font-size: 1.1rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: bold;
  transition: transform 0.2s;
}

.btn:hover {
  transform: translateY(-2px);
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid white;
}
</style>