<template>
  <div class="home">
    <section class="hero">
      <!-- 背景图片轮播 -->
      <div class="hero-bg-slides">
        <transition name="fade">
          <img
            :key="currentBgIndex"
            :src="bgImages[currentBgIndex]"
            class="hero-bg-image"
            alt="背景"
          />
        </transition>
        <div class="hero-bg-overlay"></div>
      </div>

      <!-- 轮播指示器 -->
      <div class="hero-indicators">
        <button
          v-for="(img, i) in bgImages"
          :key="i"
          class="indicator-dot"
          :class="{ active: i === currentBgIndex }"
          @click="goToSlide(i)"
        ></button>
      </div>

      <div class="hero-content">
        <div class="hero-glass">
          <div class="hero-text">
            <h1 class="hero-title">
              <span class="title-line">欢迎来到</span>
              <span class="title-highlight">Cocona Blog</span>
            </h1>
            <p class="hero-description">
              记录技术探索与生活感悟的奇妙旅程
            </p>
            <div class="hero-actions">
              <router-link to="/articles" class="btn btn-glass-primary">
                <span>开始探索</span>
              </router-link>
              <router-link to="/about" class="btn btn-glass-secondary">
                <span>了解更多</span>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="featured-section container">
      <div class="section-header">
        <h2 class="section-title">
          <span>📰 最新文章</span>
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
        <div class="empty-icon"></div>
        <h3 class="empty-title">暂无文章</h3>
        <p class="empty-desc">敬请期待更多精彩内容~</p>
      </div>
    </section>

    <section class="features-section container">
      <h2 class="section-title centered">
        <span>🌈 博客特色</span>
      </h2>

      <div class="features-grid">
        <div class="feature-card card">
          <div class="feature-emoji">🎨</div>
          <h3 class="feature-title">专属风格</h3>
          <p class="feature-desc">
            可爱的UI设计，梦幻的配色方案，让你感受动漫的魅力
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-emoji">💡</div>
          <h3 class="feature-title">技术分享</h3>
          <p class="feature-desc">
            前端、后端、设计，多领域技术文章，助你成长
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-emoji">💬</div>
          <h3 class="feature-title">互动交流</h3>
          <p class="feature-desc">
            点赞、评论、分享，与博主和其他读者互动
          </p>
        </div>

        <div class="feature-card card">
          <div class="feature-emoji">🚀</div>
          <h3 class="feature-title">持续更新</h3>
          <p class="feature-desc">
            定期更新优质内容，总有新的发现等着你
          </p>
        </div>
      </div>
    </section>

    <section class="stats-section container">
      <div class="stats-row">
        <span class="stat-item">
          <span class="stat-emoji">📚</span>
          <span class="stat-value">{{ statTotalArticles }}</span>
          <span class="stat-label">篇文章</span>
        </span>
        <span class="stat-divider"></span>
        <span class="stat-item">
          <span class="stat-value">{{ formatNumber(statTotalViews) }}</span>
          <span class="stat-label">次浏览</span>
        </span>
        <span class="stat-divider"></span>
        <span class="stat-item">
          <span class="stat-value">{{ formatNumber(statTotalLikes) }}</span>
          <span class="stat-label">个点赞</span>
        </span>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useBlogStore } from '@/store/blog'
import ArticleCard from '@/components/ArticleCard.vue'
import { articleApi } from '@/api/article'
import type { Article } from '@/api/types'

const blogStore = useBlogStore()
const allArticlesForStats = ref<Article[]>([])

const statTotalArticles = computed(() => blogStore.pagination.total)
const statTotalViews = computed(() => 
  allArticlesForStats.value.reduce((sum, a) => sum + (a.viewCount || 0), 0)
)
const statTotalLikes = computed(() => 
  allArticlesForStats.value.reduce((sum, a) => sum + (a.likeCount || 0), 0)
)

// 背景图片轮播（public 目录，运行时路径）
const bgImages = [
  '/images/hero/bg1.jpg',
  '/images/hero/bg2.png',
  '/images/hero/bg3.png'
]
const currentBgIndex = ref(0)
let bgTimer: ReturnType<typeof setInterval> | null = null

function startBgCarousel() {
  bgTimer = setInterval(() => {
    currentBgIndex.value = (currentBgIndex.value + 1) % bgImages.length
  }, 5000)
}

function goToSlide(index: number) {
  currentBgIndex.value = index
  // 重置定时器，从新图片重新开始计时
  if (bgTimer) clearInterval(bgTimer)
  startBgCarousel()
}

onMounted(async () => {
  await Promise.all([
    blogStore.fetchArticles({ status: 1, pageSize: 3, orderBy: 'publish_time', order: 'desc' }),
    articleApi.getList({ status: 1, pageSize: 999 }).then(res => {
      allArticlesForStats.value = res.data.list
    }).catch(() => {}),
    blogStore.fetchTags()
  ])
  startBgCarousel()
})

onUnmounted(() => {
  if (bgTimer) clearInterval(bgTimer)
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
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  position: relative;
  overflow: hidden;
  padding-bottom: var(--spacing-xl);
}

/* 背景轮播 */
.hero-bg-slides {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  position: absolute;
  inset: 0;
}

.hero-bg-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.25),
    rgba(124, 77, 255, 0.15)
  );
}

/* 轮播指示器 */
.hero-indicators {
  position: absolute;
  bottom: var(--spacing-lg);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 2;
}

.indicator-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
  padding: 0;
}

.indicator-dot.active {
  background: rgba(255, 255, 255, 0.9);
  transform: scale(1.3);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.indicator-dot:hover {
  background: rgba(255, 255, 255, 0.7);
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 内容容器 - 左对齐 */
.hero-content {
  max-width: 640px;
  width: 100%;
  padding: var(--spacing-2xl) calc(var(--spacing-lg) + var(--spacing-xl));
  position: relative;
  z-index: 1;
}

/* 毛玻璃卡片 - 已取消毛玻璃背景 */
.hero-glass {
  position: relative;
}

.hero-text {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  align-items: flex-start;
  position: relative;
  z-index: 1;
  padding: var(--spacing-3xl) var(--spacing-2xl);
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
  color: rgba(255, 255, 255, 0.85);
  font-size: 1.5rem;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.title-highlight {
  background: linear-gradient(135deg, #fff 0%, #ffd6e7 50%, #c4b5fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 3.5rem;
  line-height: 1.2;
  filter: drop-shadow(0 4px 12px rgba(124, 77, 255, 0.25));
  padding-bottom: 4px;
}

.hero-description {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.8;
  max-width: 460px;
  text-shadow: 0 1px 6px rgba(0, 0, 0, 0.12);
}

.hero-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

/* 毛玻璃按钮 */
.btn-glass-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  border-radius: 50px;
  font-size: 1.05rem;
  font-weight: 600;
  color: #fff;
  text-decoration: none;
  background: rgba(255, 107, 157, 0.35);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
    0 4px 20px rgba(255, 107, 157, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.btn-glass-primary:hover {
  background: rgba(255, 107, 157, 0.5);
  transform: translateY(-3px);
  box-shadow:
    0 8px 28px rgba(255, 107, 157, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
}

.btn-glass-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  border-radius: 50px;
  font-size: 1.05rem;
  font-weight: 600;
  color: #fff;
  text-decoration: none;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.btn-glass-secondary:hover {
  background: rgba(255, 255, 255, 0.28);
  transform: translateY(-3px);
  box-shadow:
    0 8px 28px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
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

.feature-emoji {
  font-size: 2.5rem;
  margin-bottom: var(--spacing-sm);
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
  padding: var(--spacing-xl) var(--spacing-lg);
}

.stats-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.stat-item .stat-emoji {
  font-size: 1rem;
  margin-bottom: 0;
}

.stat-item .stat-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0;
}

.stat-item .stat-label {
  font-size: 0.85rem;
}

.stat-divider {
  width: 1px;
  height: 16px;
  background: var(--border-color);
}

@media (max-width: 768px) {
  .hero {
    min-height: 60vh;
    justify-content: center;
  }

  .hero-content {
    padding: var(--spacing-lg);
  }

  .hero-text {
    align-items: center;
    padding: var(--spacing-2xl) var(--spacing-lg);
  }

  .hero-title {
    font-size: 2rem;
  }

  .title-highlight {
    font-size: 2.5rem;
  }

  .hero-description {
    font-size: 1rem;
    text-align: center;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .btn-glass-primary,
  .btn-glass-secondary {
    justify-content: center;
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

  .stats-row {
    gap: var(--spacing-sm);
  }

  .stat-divider {
    display: none;
  }

  .stat-item {
    font-size: 0.8rem;
  }
}
</style>
