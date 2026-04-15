<template>
  <div class="article-detail" v-if="article">
    <article class="article-container">
      <header class="article-header">
        <div class="container">
          <div class="article-meta">
            <span class="category-tag">{{ article.category }}</span>
            <span class="meta-item">
              <span class="meta-icon">📅</span>
              {{ formatDate(article.createdAt) }}
            </span>
            <span class="meta-item">
              <span class="meta-icon">👁️</span>
              {{ article.views }} 次浏览
            </span>
          </div>

          <h1 class="article-title">{{ article.title }}</h1>

          <div class="article-tags">
            <span v-for="tag in article.tags" :key="tag" class="tag">
              #{{ tag }}
            </span>
          </div>

          <div class="author-info">
            <div class="author-avatar">👤</div>
            <div class="author-details">
              <div class="author-name">{{ article.author }}</div>
              <div class="author-bio">博客作者</div>
            </div>
          </div>
        </div>
      </header>

      <div class="article-cover container">
        <img :src="article.cover" :alt="article.title" />
      </div>

      <div class="container content-layout">
        <main class="article-content">
          <div class="content-wrapper">
            <div class="markdown-body" v-html="renderedContent"></div>
          </div>

          <div class="article-actions">
            <button
              class="action-btn like-btn"
              :class="{ liked: isLiked }"
              @click="handleLike"
            >
              <span class="action-icon">{{ isLiked ? '💖' : '🤍' }}</span>
              <span>{{ article.likes }}</span>
            </button>
            <button class="action-btn share-btn" @click="handleShare">
              <span class="action-icon">🔗</span>
              <span>分享</span>
            </button>
          </div>

          <div class="article-navigation">
            <router-link to="/articles" class="nav-btn">
              <span>←</span>
              <span>返回文章列表</span>
            </router-link>
          </div>
        </main>

        <aside class="article-sidebar">
          <div class="sidebar-section card">
            <h3 class="section-title">
              <span class="title-icon">📋</span>
              <span>目录</span>
            </h3>
            <div class="toc">
              <div class="toc-empty">暂无目录</div>
            </div>
          </div>

          <div class="sidebar-section card">
            <h3 class="section-title">
              <span class="title-icon">📝</span>
              <span>相关文章</span>
            </h3>
            <div class="related-articles">
              <div
                v-for="related in relatedArticles"
                :key="related.id"
                class="related-item"
                @click="navigateToArticle(related.id)"
              >
                <div class="related-cover">
                  <img :src="related.cover" :alt="related.title" />
                </div>
                <div class="related-info">
                  <h4 class="related-title">{{ related.title }}</h4>
                  <div class="related-date">{{ formatDate(related.createdAt) }}</div>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </article>
  </div>

  <div v-else class="not-found">
    <div class="container">
      <div class="not-found-content">
        <div class="not-found-icon">🔍</div>
        <h2 class="not-found-title">文章未找到</h2>
        <p class="not-found-desc">抱歉，这篇文章可能已经被删除或不存在</p>
        <router-link to="/articles" class="btn btn-primary">
          返回文章列表
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore, type Article } from '@/store/blog'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()

const article = ref<Article | null>(null)
const isLiked = ref(false)

const renderedContent = computed(() => {
  if (!article.value) return ''
  return renderMarkdown(article.value.content)
})

const relatedArticles = computed(() => {
  if (!article.value) return []
  return blogStore.articles
    .filter(a => 
      a.id !== article.value?.id && 
      (a.category === article.value?.category || 
       a.tags.some(tag => article.value?.tags.includes(tag)))
    )
    .slice(0, 3)
})

onMounted(() => {
  loadArticle()
})

watch(() => route.params.id, () => {
  loadArticle()
})

function loadArticle() {
  const id = parseInt(route.params.id as string)
  article.value = blogStore.getArticleById(id)
  
  if (article.value) {
    blogStore.viewArticle(id)
  }
}

function renderMarkdown(content: string): string {
  let html = content
  
  html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>')
  html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>')
  html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>')
  
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\*(.*?)\*/g, '<em>$1</em>')
  
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>')
  html = html.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
  
  html = html.replace(/^\- (.*$)/gim, '<li>$1</li>')
  html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  
  html = html.replace(/^\> (.*$)/gim, '<blockquote>$1</blockquote>')
  
  html = html.replace(/\n/g, '<br>')
  
  return html
}

function formatDate(date: string): string {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function handleLike() {
  if (article.value) {
    blogStore.likeArticle(article.value.id)
    isLiked.value = true
  }
}

function handleShare() {
  if (navigator.share) {
    navigator.share({
      title: article.value?.title,
      url: window.location.href
    })
  } else {
    navigator.clipboard.writeText(window.location.href)
    alert('链接已复制到剪贴板！')
  }
}

function navigateToArticle(id: number) {
  router.push(`/article/${id}`)
}
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
}

.article-container {
  padding-bottom: var(--spacing-2xl);
}

.article-header {
  background: var(--gradient-primary);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
  padding-bottom: var(--spacing-xl);
}

.article-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  flex-wrap: wrap;
}

.category-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 0.9rem;
  opacity: 0.9;
}

.meta-icon {
  font-size: 1rem;
}

.article-title {
  font-size: 2.5rem;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: var(--spacing-md);
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.article-tags .tag {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.article-tags .tag:hover {
  background: white;
  color: var(--primary-color);
}

.author-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.author-avatar {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.author-name {
  font-weight: 600;
  font-size: 1.1rem;
}

.author-bio {
  font-size: 0.9rem;
  opacity: 0.8;
}

.article-cover {
  margin-top: calc(-1 * var(--spacing-xl));
  position: relative;
  z-index: 1;
}

.article-cover img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: var(--spacing-xl);
  margin-top: var(--spacing-xl);
}

.article-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.content-wrapper {
  background: var(--bg-card);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
}

.article-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  padding: var(--spacing-lg);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--bg-hover);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all var(--transition-normal);
}

.action-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-2px);
}

.like-btn.liked {
  background: var(--gradient-primary);
  border-color: transparent;
  color: white;
}

.action-icon {
  font-size: 1.3rem;
}

.article-navigation {
  text-align: center;
}

.nav-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  color: var(--text-secondary);
  font-weight: 500;
  transition: all var(--transition-normal);
}

.nav-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateX(-4px);
}

.article-sidebar {
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
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.title-icon {
  font-size: 1.3rem;
}

.toc-empty {
  color: var(--text-muted);
  font-size: 0.9rem;
  text-align: center;
  padding: var(--spacing-md);
}

.related-articles {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.related-item {
  display: flex;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.related-item:hover {
  background: var(--primary-color);
  color: white;
  transform: translateX(4px);
}

.related-cover {
  width: 80px;
  height: 60px;
  border-radius: var(--border-radius-sm);
  overflow: hidden;
  flex-shrink: 0;
}

.related-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-info {
  flex: 1;
  min-width: 0;
}

.related-title {
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-date {
  font-size: 0.8rem;
  opacity: 0.7;
}

.not-found {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--spacing-2xl);
}

.not-found-content {
  max-width: 400px;
}

.not-found-icon {
  font-size: 5rem;
  margin-bottom: var(--spacing-lg);
  opacity: 0.5;
}

.not-found-title {
  font-size: 2rem;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.not-found-desc {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
}

@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .article-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .article-title {
    font-size: 1.75rem;
  }

  .article-cover img {
    height: 250px;
  }

  .content-wrapper {
    padding: var(--spacing-lg);
  }

  .article-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
