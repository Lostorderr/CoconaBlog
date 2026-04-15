<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            <span class="title-line">欢迎来到</span>
            <span class="title-highlight">Cocona Blog</span>
            <span class="title-emoji">✨</span>
          </h1>
          <p class="hero-description">
            记录技术探索与生活感悟的奇妙旅程
          </p>
          <div class="hero-actions">
            <router-link to="/articles" class="btn btn-primary sparkle">
              <span>开始探索</span>
              <span>🚀</span>
            </router-link>
            <router-link to="/about" class="btn btn-secondary">
              <span>了解更多</span>
              <span>💫</span>
            </router-link>
          </div>
        </div>
        <div class="hero-illustration">
          <div class="floating-elements">
            <span class="float-item" style="--delay: 0s">🌸</span>
            <span class="float-item" style="--delay: 0.5s">💖</span>
            <span class="float-item" style="--delay: 1s">✨</span>
            <span class="float-item" style="--delay: 1.5s">🎨</span>
            <span class="float-item" style="--delay: 2s">💫</span>
          </div>
        </div>
      </div>
    </section>

    <section class="featured-section container">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">📝</span>
          <span>最新文章</span>
        </h2>
        <router-link to="/articles" class="view-all">
          查看全部 →
        </router-link>
      </div>

      <div v-if="blogStore.loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="latestArticles.length > 0" class="articles-grid">
        <ArticleCard
          v-for="article in latestArticles"
          :key="article.id"
          :article="article"
        />
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">📝</div>
        <h3 class="empty-title">暂无文章</h3>
        <p class="empty-desc">敬请期待更多精彩内容~</p>
      </div>
    </section>

    <section class="features-section container">
      <h2 class="section-title centered">
        <span class="title-icon">🌟</span>
        <span>博客特色</span>
      </h2>

      <div class="features-grid">
        <div class="feature-card card">
          <div class="feature-icon">🎨</div>
          <h3 class="feature-title">专属风格</h3>
          <p class="feature-desc">
            可爱的UI设计，梦幻的配色方案，让你感受动漫的魅力
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-icon">💻</div>
          <h3 class="feature-title">技术分享</h3>
          <p class="feature-desc">
            前端、后端、设计，多领域技术文章，助你成长
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-icon">💖</div>
          <h3 class="feature-title">互动交流</h3>
          <p class="feature-desc">
            点赞、评论、分享，与博主和其他读者互动
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-icon">🌙</div>
          <h3 class="feature-title">持续更新</h3>
          <p class="feature-desc">
            定期更新优质内容，总有新的发现等着你
          </p>
        </div>
      </div>
    </section>

    <section class="stats-section container">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-value">{{ blogStore.totalArticles }}</div>
          <div class="stat-label">篇文章</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">👁️</div>
          <div class="stat-value">{{ formatNumber(blogStore.totalViews) }}</div>
          <div class="stat-label">次浏览</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💖</div>
          <div class="stat-value">{{ formatNumber(blogStore.totalLikes) }}</div>
          <div class="stat-label">个点赞</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🏷️</div>
          <div class="stat-value">{{ blogStore.allTags.length }}</div>
          <div class="stat-label">个标签</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useBlogStore } from '@/store/blog'
import ArticleCard from '@/components/ArticleCard.vue'

const blogStore = useBlogStore()

onMounted(async () => {
  await blogStore.fetchArticles({ pageSize: 3 })
  await blogStore.fetchTags()
})

const latestArticles = computed(() => 
  blogStore.articles.slice(0, 3)
)

function formatNumber(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.hero {
  background: linear-gradient(135deg, var(--bg-primary) 0%, #ffeef3 100%);
  padding: var(--spacing-2xl) 0;
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(255, 107, 157, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(124, 77, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.hero-content {
  container: default;
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-lg);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-2xl);
  align-items: center;
  position: relative;
  z-index: 1;
}

.hero-text {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  line-height: 1.2;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.title-line {
  color: var(--text-secondary);
  font-size: 1.5rem;
}

.title-highlight {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 3.5rem;
}

.title-emoji {
  font-size: 2.5rem;
  display: inline-block;
  animation: float 3s ease-in-out infinite;
}

.hero-description {
  font-size: 1.2rem;
  color: var(--text-secondary);
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

.hero-illustration {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.floating-elements {
  position: relative;
  width: 300px;
  height: 300px;
}

.float-item {
  position: absolute;
  font-size: 3rem;
  animation: floatRandom 6s ease-in-out infinite;
  animation-delay: var(--delay);
}

.float-item:nth-child(1) { top: 0; left: 50%; }
.float-item:nth-child(2) { top: 25%; right: 0; }
.float-item:nth-child(3) { bottom: 25%; right: 10%; }
.float-item:nth-child(4) { bottom: 0; left: 50%; }
.float-item:nth-child(5) { top: 25%; left: 0; }

@keyframes floatRandom {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(10px, -20px) rotate(10deg);
  }
  50% {
    transform: translate(-10px, -10px) rotate(-10deg);
  }
  75% {
    transform: translate(15px, 5px) rotate(5deg);
  }
}

.featured-section {
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--text-primary);
}

.section-title.centered {
  justify-content: center;
  margin-bottom: var(--spacing-xl);
}

.title-icon {
  font-size: 2rem;
}

.view-all {
  color: var(--primary-color);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.view-all:hover {
  color: var(--primary-dark);
  transform: translateX(4px);
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-lg);
}

.loading-state {
  text-align: center;
  padding: var(--spacing-2xl);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto var(--spacing-md);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.empty-title {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.empty-desc {
  color: var(--text-secondary);
}

.features-section {
  padding: var(--spacing-2xl) var(--spacing-lg);
  background: var(--bg-secondary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

.feature-card {
  text-align: center;
  padding: var(--spacing-xl);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-md);
  animation: float 3s ease-in-out infinite;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.feature-desc {
  color: var(--text-secondary);
  line-height: 1.6;
}

.stats-section {
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.stat-card {
  text-align: center;
  padding: var(--spacing-xl);
  background: var(--gradient-primary);
  border-radius: var(--border-radius);
  color: white;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
}

.stat-icon {
  font-size: 2.5rem;
  margin-bottom: var(--spacing-sm);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: 1rem;
  opacity: 0.9;
}

@media (max-width: 968px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .hero-title {
    font-size: 2rem;
  }

  .title-highlight {
    font-size: 2.5rem;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-illustration {
    min-height: 200px;
  }

  .floating-elements {
    width: 200px;
    height: 200px;
  }

  .float-item {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    gap: var(--spacing-md);
    text-align: center;
  }

  .articles-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
