<template>
  <div class="articles-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">
          <span>文章列表</span>
        </h1>
        <p class="page-description">
          探索所有精彩文章，发现你感兴趣的内容
        </p>
      </div>
    </div>

    <div class="container page-content">
      <div class="content-layout">
        <main class="main-content">
          <div class="filter-bar">
            <div class="filter-tabs">
              <button
                class="filter-tab"
                :class="{ active: currentCategoryId === null }"
                @click="filterByCategory(null)"
              >
                全部
              </button>
              <button
                v-for="cat in blogStore.categories"
                :key="cat.id"
                class="filter-tab"
                :class="{ active: currentCategoryId === cat.id }"
                @click="filterByCategory(cat.id)"
              >
                {{ cat.name }}
              </button>
            </div>

            <div class="search-box">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索文章..."
                class="search-input"
              />
              <span class="search-icon"></span>
            </div>
          </div>

          <div v-if="blogStore.loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>

          <div v-else-if="blogStore.articles.length > 0" class="articles-list">
            <ArticleCard
              v-for="article in filteredArticles"
              :key="article.id"
              :article="article"
            />
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon"></div>
            <h3 class="empty-title">没有找到相关文章</h3>
            <p class="empty-desc">试试其他关键词或分类吧~</p>
          </div>
        </main>

        <Sidebar />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'
import ArticleCard from '@/components/ArticleCard.vue'
import Sidebar from '@/components/Sidebar.vue'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()

const searchQuery = ref('')
const currentCategoryId = ref<number | null>(null)

onMounted(async () => {
  await blogStore.fetchCategories()
  await loadArticles()
})

watch(() => route.query, (query) => {
  if (query.category) {
    currentCategoryId.value = Number(query.category)
  } else {
    currentCategoryId.value = null
  }
  if (query.tag) {
    searchQuery.value = String(query.tag)
  }
  if (query.search) {
    searchQuery.value = String(query.search)
  }
  loadArticles()
}, { immediate: true })

async function loadArticles() {
  await blogStore.fetchArticles({
    categoryId: currentCategoryId.value || undefined,
    keyword: searchQuery.value || undefined
  })
}

const filteredArticles = computed(() => {
  let articles = blogStore.articles

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    articles = articles.filter(a =>
      a.title.toLowerCase().includes(query) ||
      (a.summary && a.summary.toLowerCase().includes(query)) ||
      (a.tags && a.tags.some(tag => tag.name.toLowerCase().includes(query)))
    )
  }

  return articles
})

function filterByCategory(categoryId: number | null) {
  currentCategoryId.value = categoryId
  if (categoryId === null) {
    router.push({ path: '/articles' })
  } else {
    router.push({ path: '/articles', query: { category: categoryId } })
  }
}
</script>

<style scoped>
.articles-page {
  min-height: 100vh;
}

.page-header {
  background: var(--gradient-primary);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
  text-align: center;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: var(--spacing-md);
}

.title-icon {
  font-size: 2.5rem;
}

.page-description {
  font-size: 1.1rem;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

.page-content {
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--spacing-xl);
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  flex-wrap: wrap;
}

.filter-tabs {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.filter-tab {
  padding: var(--spacing-sm) var(--spacing-md);
  background: transparent;
  border: 2px solid var(--border-color);
  border-radius: 20px;
  color: var(--text-secondary);
  font-weight: 500;
  transition: all var(--transition-normal);
}

.filter-tab:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.filter-tab.active {
  background: var(--gradient-primary);
  border-color: transparent;
  color: white;
}

.search-box {
  position: relative;
  flex: 0 0 250px;
}

.search-input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  padding-right: 40px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 0.95rem;
  transition: all var(--transition-normal);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.1rem;
  pointer-events: none;
}

.articles-list {
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

@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-tabs {
    justify-content: center;
  }

  .search-box {
    flex: 1;
  }

  .articles-list {
    grid-template-columns: 1fr;
  }
}
</style>
