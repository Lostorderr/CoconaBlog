<template>
  <aside class="sidebar">
    <div class="sidebar-section card">
      <h3 class="section-title">
        <span class="title-icon">📊</span>
        <span>博客统计</span>
      </h3>
      <div class="stats-grid">
        <div class="stat-box">
          <div class="stat-value">{{ blogStore.totalArticles }}</div>
          <div class="stat-label">文章数</div>
        </div>
        <div class="stat-box">
          <div class="stat-value">{{ formatNumber(blogStore.totalViews) }}</div>
          <div class="stat-label">总浏览</div>
        </div>
        <div class="stat-box">
          <div class="stat-value">{{ formatNumber(blogStore.totalLikes) }}</div>
          <div class="stat-label">总点赞</div>
        </div>
        <div class="stat-box">
          <div class="stat-value">{{ blogStore.categories.length }}</div>
          <div class="stat-label">分类数</div>
        </div>
      </div>
    </div>

    <div class="sidebar-section card">
      <h3 class="section-title">
        <span class="title-icon">📁</span>
        <span>文章分类</span>
      </h3>
      <div class="category-list">
        <div
          v-for="category in blogStore.categories"
          :key="category"
          class="category-item"
          @click="filterByCategory(category)"
        >
          <span class="category-name">{{ category }}</span>
          <span class="category-count">
            {{ blogStore.getArticlesByCategory(category).length }}
          </span>
        </div>
      </div>
    </div>

    <div class="sidebar-section card">
      <h3 class="section-title">
        <span class="title-icon">🏷️</span>
        <span>热门标签</span>
      </h3>
      <div class="tag-cloud">
        <span
          v-for="tag in blogStore.allTags"
          :key="tag"
          class="tag"
          @click="filterByTag(tag)"
        >
          #{{ tag }}
        </span>
      </div>
    </div>

    <div class="sidebar-section card">
      <h3 class="section-title">
        <span class="title-icon">💫</span>
        <span>关于博主</span>
      </h3>
      <div class="author-card">
        <div class="author-avatar">🌸</div>
        <div class="author-name">Cocona</div>
        <div class="author-bio">
          一个热爱生活和编程的博主，喜欢分享技术心得和生活感悟 ✨
        </div>
        <div class="social-links">
          <a href="#" class="social-link" title="GitHub">
            <span>💻</span>
          </a>
          <a href="#" class="social-link" title="Twitter">
            <span>🐦</span>
          </a>
          <a href="#" class="social-link" title="Email">
            <span>📧</span>
          </a>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'

const router = useRouter()
const blogStore = useBlogStore()

function formatNumber(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

function filterByCategory(category: string) {
  router.push({
    path: '/articles',
    query: { category }
  })
}

function filterByTag(tag: string) {
  router.push({
    path: '/articles',
    query: { tag }
  })
}
</script>

<style scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.sidebar-section {
  padding: var(--spacing-lg);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.title-icon {
  font-size: 1.3rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

.stat-box {
  text-align: center;
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.stat-box:hover {
  background: var(--gradient-primary);
  transform: translateY(-2px);
}

.stat-box:hover .stat-value,
.stat-box:hover .stat-label {
  color: white;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.category-item:hover {
  background: var(--primary-color);
  color: white;
  transform: translateX(4px);
}

.category-name {
  font-weight: 500;
}

.category-count {
  background: var(--gradient-primary);
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
}

.category-item:hover .category-count {
  background: white;
  color: var(--primary-color);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tag {
  cursor: pointer;
}

.author-card {
  text-align: center;
}

.author-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-md);
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  box-shadow: var(--shadow-md);
  animation: float 3s ease-in-out infinite;
}

.author-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.author-bio {
  font-size: 0.9rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
}

.social-links {
  display: flex;
  justify-content: center;
  gap: var(--spacing-md);
}

.social-link {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-hover);
  border-radius: 50%;
  font-size: 1.2rem;
  transition: all var(--transition-normal);
}

.social-link:hover {
  background: var(--gradient-primary);
  transform: translateY(-4px) scale(1.1);
  box-shadow: var(--shadow-md);
}

@media (max-width: 1024px) {
  .sidebar {
    display: none;
  }
}
</style>
