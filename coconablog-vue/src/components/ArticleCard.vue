<template>
  <div class="article-card card" @click="navigateToArticle">
    <div class="card-cover">
      <img :src="article.cover" :alt="article.title" />
      <div class="cover-overlay">
        <span class="category-tag">{{ article.category }}</span>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="card-title">{{ article.title }}</h3>
      <p class="card-summary">{{ article.summary }}</p>
      
      <div class="card-tags">
        <span v-for="tag in article.tags.slice(0, 3)" :key="tag" class="tag">
          #{{ tag }}
        </span>
      </div>
      
      <div class="card-footer">
        <div class="author-info">
          <div class="author-avatar">👤</div>
          <span class="author-name">{{ article.author }}</span>
        </div>
        
        <div class="article-stats">
          <span class="stat-item">
            <span class="stat-icon">👁️</span>
            <span>{{ formatNumber(article.views) }}</span>
          </span>
          <span class="stat-item">
            <span class="stat-icon">💖</span>
            <span>{{ formatNumber(article.likes) }}</span>
          </span>
        </div>
      </div>
      
      <div class="card-date">
        <span class="date-icon">📅</span>
        <span>{{ formatDate(article.createdAt) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { Article } from '@/store/blog'

const props = defineProps<{
  article: Article
}>()

const router = useRouter()

function navigateToArticle() {
  router.push(`/article/${props.article.id}`)
}

function formatNumber(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

function formatDate(date: string): string {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.article-card {
  cursor: pointer;
  overflow: hidden;
  padding: 0;
  display: flex;
  flex-direction: column;
}

.card-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.article-card:hover .card-cover img {
  transform: scale(1.1);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgba(45, 27, 48, 0.3) 100%
  );
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: var(--spacing-md);
}

.category-tag {
  background: var(--gradient-primary);
  color: white;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.card-content {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition-fast);
}

.article-card:hover .card-title {
  color: var(--primary-color);
}

.card-summary {
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xs);
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color);
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}

.author-name {
  font-weight: 500;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.article-stats {
  display: flex;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--text-muted);
  font-size: 0.85rem;
}

.stat-icon {
  font-size: 1rem;
}

.card-date {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--text-muted);
  font-size: 0.85rem;
  margin-top: var(--spacing-sm);
}

.date-icon {
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .card-cover {
    height: 180px;
  }

  .card-title {
    font-size: 1.1rem;
  }

  .card-summary {
    font-size: 0.9rem;
  }
}
</style>
