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
            <!-- 排序方式 -->
            <div class="sort-group">
              <span class="sort-label">排序：</span>
              <div class="sort-options">
                <div
                  v-for="opt in sortOptions"
                  :key="opt.value"
                  class="sort-item"
                >
                  <button
                    class="sort-btn"
                    :class="{ active: currentSort === opt.value }"
                    @click="changeSort(opt.value)"
                  >
                    {{ opt.label }}
                  </button>
                  <button
                    class="order-toggle-btn"
                    :class="{ active: currentSort === opt.value }"
                    :title="currentSort === opt.value && currentOrder === 'asc' ? '当前：升序（点击切降序）' : '当前：降序（点击切升序）'"
                    @click="toggleOrderFor(opt.value)"
                  >
                    {{ currentSort === opt.value ? (currentOrder === 'desc' ? '↓' : '↑') : '↓' }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 搜索框 -->
            <div class="search-box">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索文章..."
                class="search-input"
                @keyup.enter="handleSearch"
              />
              <button class="search-icon" @click="handleSearch">🔍</button>
            </div>
          </div>

          <!-- 当前筛选状态提示 -->
          <div v-if="searchQuery || (currentSort !== 'publish_time') || (currentOrder !== 'desc')" class="active-filters">
            <span class="filter-label">当前筛选：</span>
            <span v-if="currentSort !== 'publish_time'" class="filter-chip">
              排序：{{ sortOptions.find(o => o.value === currentSort)?.label }}
              <button @click="currentSort = 'publish_time'; currentOrder = 'desc'; loadArticles(); syncUrl()">&times;</button>
            </span>
            <span v-if="currentOrder !== 'desc'" class="filter-chip order-chip">
              顺序：{{ currentOrder === 'asc' ? '升序 ↑' : '降序 ↓' }}
              <button @click="currentOrder = 'desc'; loadArticles(); syncUrl()">&times;</button>
            </span>
            <span v-if="searchQuery" class="filter-chip">
              搜索：{{ searchQuery }}
              <button @click="clearSearch">&times;</button>
            </span>
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
            <p class="empty-desc">试试其他关键词或筛选条件吧~</p>
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
const currentSort = ref('publish_time')
const currentOrder = ref<'asc' | 'desc'>('desc')

const sortOptions = [
  { label: '发布时间', value: 'publish_time' },
  { label: '点赞量', value: 'like_count' },
  { label: '浏览量', value: 'view_count' }
]

onMounted(async () => {
  await blogStore.fetchCategories()
  // 从 URL 恢复状态
  if (route.query.orderBy) {
    const val = String(route.query.orderBy)
    if (sortOptions.some(o => o.value === val)) currentSort.value = val
  }
  if (route.query.order) {
    const ord = String(route.query.order)
    if (ord === 'asc' || ord === 'desc') currentOrder.value = ord
  }
  if (route.query.search) {
    searchQuery.value = String(route.query.search)
  }
  await loadArticles()
})

function toggleOrderFor(sort: string) {
  if (currentSort.value === sort) {
    currentOrder.value = currentOrder.value === 'desc' ? 'asc' : 'desc'
  } else {
    currentSort.value = sort
    currentOrder.value = 'desc'
  }
  loadArticles()
  syncUrl()
}

function changeSort(value: string) {
  currentSort.value = value
  loadArticles()
  syncUrl()
}

function clearSearch() {
  searchQuery.value = ''
  loadArticles()
  syncUrl()
}

function handleSearch() {
  loadArticles()
  syncUrl()
}

function syncUrl() {
  const query: Record<string, string> = {}
  if (currentSort.value !== 'publish_time') query.orderBy = currentSort.value
  if (currentOrder.value !== 'desc') query.order = currentOrder.value
  if (searchQuery.value) query.search = searchQuery.value
  router.replace({ path: '/articles', query })
}

async function loadArticles() {
  await blogStore.fetchArticles({
    orderBy: currentSort.value,
    order: currentOrder.value,
    keyword: searchQuery.value || undefined
  })
}

const filteredArticles = computed(() => {
  let articles = blogStore.articles

  // 搜索关键词二次过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    articles = articles.filter(a =>
      a.title.toLowerCase().includes(query) ||
      (a.summary && a.summary.toLowerCase().includes(query))
    )
  }

  return articles
})
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

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  flex-wrap: wrap;
}

.sort-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.sort-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  white-space: nowrap;
  font-weight: 500;
}

.sort-options {
  display: flex;
  gap: 4px;
  background: var(--bg-secondary);
  padding: 3px;
  border-radius: 20px;
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

.sort-btn {
  padding: 6px 14px;
  background: transparent;
  border: none;
  border-radius: 16px;
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
}

.sort-btn:hover {
  color: var(--primary-color);
}

.sort-btn.active {
  background: white;
  color: var(--primary-color);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  font-weight: 600;
}

.order-toggle-btn {
  width: 26px;
  padding: 6px 0;
  background: transparent;
  border: none;
  border-radius: 13px;
  color: var(--text-muted);
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s ease;
  flex-shrink: 0;
}

.order-toggle-btn:hover {
  color: var(--primary-color);
  background: white;
}

.order-toggle-btn.active {
  background: white;
  color: var(--primary-color);
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

/* 搜索框 */
.search-box {
  position: relative;
  flex: 0 0 200px;
}

.search-input {
  width: 100%;
  padding: 8px 36px 8px 14px;
  border: 1.5px solid var(--border-color);
  border-radius: 20px;
  font-size: 0.88rem;
  transition: all var(--transition-normal);
  background: var(--bg-secondary);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
  background: white;
}

.search-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.95rem;
  cursor: pointer;
  background: none;
  border: none;
  padding: 2px;
  line-height: 1;
}

/* 当前筛选状态 */
.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 14px;
  background: rgba(255, 107, 157, 0.06);
  border-radius: var(--border-radius-sm);
  border: 1px solid rgba(255, 107, 157, 0.15);
}

.filter-label {
  font-size: 0.83rem;
  color: var(--text-muted);
  font-weight: 500;
}

.filter-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  background: white;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 0.8rem;
  color: var(--text-primary);
}

.filter-chip button {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
  padding: 0 2px;
}

.filter-chip button:hover {
  color: #e74c3c;
}

.tag-chip {
  background: rgba(124, 77, 255, 0.08);
  border-color: rgba(124, 77, 255, 0.2);
  color: #7c4dff;
}

.order-chip {
  background: rgba(124, 77, 255, 0.08);
  border-color: rgba(124, 77, 255, 0.2);
  color: #7c4dff;
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

  .sort-group {
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
