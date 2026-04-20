<template>
  <div class="article-card card" @click="navigateToArticle">
    <div v-if="hotIndex" class="hot-badge" :class="'hot-' + hotIndex">{{ hotIndex }}</div>
    <div class="card-main">
      <div class="card-body">
        <div class="card-header">
          <h3 class="card-title">{{ article.title }}</h3>
          <span class="category-tag">{{ article.category?.name || '未分类' }}</span>
        </div>
        <p class="card-summary">{{ article.summary || '暂无摘要' }}</p>
        <div class="card-tags">
          <span v-for="tag in (article.tags || []).slice(0, 4)" :key="tag.id" class="tag">
            #{{ tag.name }}
          </span>
        </div>
      </div>

      <div class="card-meta">
        <div class="author-info">
          <img v-if="article.author?.avatar" :src="article.author.avatar" alt="" class="author-avatar" />
          <div v-else class="author-avatar author-avatar-placeholder">{{ article.author?.username?.charAt(0) || '?' }}</div>
          <div class="author-detail">
            <span class="author-name">{{ article.author?.username || '匿名' }}</span>
            <span class="card-date">{{ formatDate(article.createTime) }}</span>
          </div>
        </div>

        <div class="article-stats">
          <span class="stat-item">
            <span>{{ article.viewCount }}</span> 浏览
          </span>
          <span class="stat-item">
            <span>{{ article.likeCount }}</span> 赞
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { Article } from '@/store/blog'

const props = defineProps<{
  article: Article
  hotIndex?: number
}>()

const router = useRouter()

function navigateToArticle() {
  router.push(`/article/${props.article.id}`)
}

function formatDate(date: string): string {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    month: 'short',
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
  position: relative;
}

.hot-badge {
  position: absolute;
  top: 0;
  left: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: white;
  border-bottom-right-radius: 10px;
  z-index: 1;
}

.hot-1 {
  background: linear-gradient(135deg, #ff4757, #ff6b81);
}

.hot-2 {
  background: linear-gradient(135deg, #ff7f50, #ffa07a);
}

.hot-3 {
  background: linear-gradient(135deg, #ffa502, #ffc048);
}

.card-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--spacing-sm);
}

.card-title {
  font-size: 1.15rem;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
  min-width: 0;
  transition: color var(--transition-fast);
}

.article-card:hover .card-title {
  color: var(--primary-color);
}

.category-tag {
  background: var(--gradient-primary);
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  flex-shrink: 0;
}

.card-summary {
  color: var(--text-secondary);
  font-size: 0.9rem;
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
  gap: 6px;
}

.card-tags .tag {
  color: var(--primary-color);
  background: rgba(255, 107, 157, 0.08);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 500;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--border-color);
  gap: var(--spacing-md);
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  min-width: 0;
}

.author-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.author-avatar-placeholder {
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  color: white;
}

.author-detail {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.author-name {
  font-weight: 500;
  color: var(--text-secondary);
  font-size: 0.85rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-date {
  color: var(--text-muted);
  font-size: 0.78rem;
}

.article-stats {
  display: flex;
  gap: var(--spacing-md);
  flex-shrink: 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 3px;
  color: var(--text-muted);
  font-size: 0.82rem;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 6px;
  }

  .category-tag {
    align-self: flex-start;
  }

  .card-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
}
</style>
