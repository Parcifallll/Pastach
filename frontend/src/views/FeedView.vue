<template>
  <div class="feed-view">
    <h1>Feed</h1>

    <div v-if="postsStore.loading && postsStore.posts.length === 0">
      Loading posts...
    </div>

    <div v-else-if="postsStore.error">
      Error: {{ postsStore.error }}
    </div>

    <div v-else>
      <div v-for="post in postsStore.posts" :key="post.id" class="post-item">
        <p><strong>Author ID:</strong> {{ post.authorId }}</p>
        <p>{{ post.text }}</p>
        <p class="post-meta">
          👍 {{ post.likesCount }} | 👎 {{ post.dislikesCount }} | 💬 {{ post.commentsCount }}
        </p>
        <small>{{ new Date(post.createdAt).toLocaleString() }}</small>
      </div>

      <button
          v-if="postsStore.hasMore"
          @click="postsStore.loadMorePosts()"
          :disabled="postsStore.loading"
      >
        {{ postsStore.loading ? 'Loading...' : 'Load More' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { usePostsStore } from '@/stores/posts';

const postsStore = usePostsStore();

onMounted(async () => {
  await postsStore.fetchPosts();
});
</script>

<style scoped>
.feed-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.post-item {
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 8px;
  background: #fff;
}

.post-meta {
  color: #666;
  margin: 10px 0;
}

button {
  padding: 10px 20px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>