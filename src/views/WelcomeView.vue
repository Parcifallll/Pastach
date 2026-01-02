<template>
  <div class="welcome-view">
    <div class="hero">
      <h1>Welcome to Pastach 🎉</h1>
      <p class="tagline">Your new favorite social media platform</p>

      <div class="features">
        <div class="feature">
          <div class="feature-icon">📝</div>
          <h3>Share Your Thoughts</h3>
          <p>Post text and photos with your friends</p>
        </div>

        <div class="feature">
          <div class="feature-icon">💬</div>
          <h3>Engage</h3>
          <p>Comment and react to posts</p>
        </div>

        <div class="feature">
          <div class="feature-icon">👥</div>
          <h3>Connect</h3>
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
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  overflow-y: auto;
}

.hero {
  text-align: center;
  max-width: 1200px;
  padding: 40px 20px;
  width: 100%;
}

h1 {
  font-size: 4rem;
  margin-bottom: 1rem;
  font-weight: bold;
}

.tagline {
  font-size: 1.8rem;
  margin-bottom: 4rem;
  opacity: 0.95;
}

.features {
  display: flex;
  justify-content: center;
  align-items: stretch;
  gap: 2rem;
  margin-bottom: 4rem;
  flex-wrap: wrap;
}

.feature {
  background: rgba(255, 255, 255, 0.15);
  padding: 2.5rem 2rem;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  flex: 1;
  min-width: 280px;
  max-width: 350px;
  transition: transform 0.3s, background 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.2);
}

.feature-icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
}

.feature h3 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.feature p {
  font-size: 1.1rem;
  opacity: 0.9;
  line-height: 1.6;
}

.cta-buttons {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 3rem;
}

.btn {
  padding: 18px 50px;
  font-size: 1.2rem;
  border-radius: 10px;
  text-decoration: none;
  font-weight: bold;
  transition: transform 0.2s, box-shadow 0.2s;
  display: inline-block;
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 2px solid white;
  backdrop-filter: blur(10px);
}

/* Responsive */
@media (max-width: 768px) {
  h1 {
    font-size: 2.5rem;
  }

  .tagline {
    font-size: 1.3rem;
  }

  .features {
    flex-direction: column;
    align-items: center;
  }

  .feature {
    max-width: 100%;
  }
}
</style>