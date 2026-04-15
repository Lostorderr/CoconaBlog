<template>
  <div class="article-management">
    <div class="management-header">
      <h2>文章管理</h2>
      <router-link to="/create-article" class="btn btn-primary">
        <span>➕</span> 发布文章
      </router-link>
    </div>

    <div class="filter-bar">
      <select v-model="statusFilter" class="filter-select">
        <option value="all">全部状态</option>
        <option value="published">已发布</option>
        <option value="draft">草稿</option>
        <option value="trash">回收站</option>
      </select>
      <input v-model="searchQuery" type="text" placeholder="搜索文章..." class="search-input" />
    </div>

    <div class="article-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filteredArticles.length === 0" class="empty">暂无文章</div>
      <div v-else class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>作者</th>
              <th>分类</th>
              <th>状态</th>
              <th>浏览</th>
              <th>点赞</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in filteredArticles" :key="article.id">
              <td>{{ article.id }}</td>
              <td class="title-cell">{{ article.title }}</td>
              <td>{{ article.author?.username || '-' }}</td>
              <td>{{ article.category?.name || '-' }}</td>
              <td>
                <span class="status-badge" :class="getStatusClass(article.status)">
                  {{ getStatusText(article.status) }}
                </span>
              </td>
              <td>{{ article.viewCount }}</td>
              <td>{{ article.likeCount }}</td>
              <td>{{ formatDate(article.createTime) }}</td>
              <td class="actions">
                <router-link :to="`/article/${article.id}`" class="btn-sm btn-view">查看</router-link>
                <router-link :to="`/edit-article/${article.id}`" class="btn-sm btn-edit">编辑</router-link>
                <button v-if="article.status !== 2" class="btn-sm btn-delete" @click="deleteArticle(article)">删除</button>
                <button v-else class="btn-sm btn-restore" @click="restoreArticle(article)">恢复</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="pagination">
      <button class="page-btn" :disabled="page === 1" @click="page--">上一页</button>
      <span class="page-info">第 {{ page }} 页</span>
      <button class="page-btn" :disabled="articles.length < pageSize" @click="page++">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { articleApi } from '@/api/article'
import type { Article } from '@/api/types'

const loading = ref(false)
const articles = ref<Article[]>([])
const statusFilter = ref('all')
const searchQuery = ref('')
const page = ref(1)
const pageSize = 20

const filteredArticles = computed(() => {
  let result = articles.value
  
  if (statusFilter.value !== 'all') {
    const statusMap: Record<string, number> = {
      published: 1,
      draft: 0,
      trash: 2
    }
    result = result.filter(a => a.status === statusMap[statusFilter.value])
  }
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(a => 
      a.title.toLowerCase().includes(query)
    )
  }
  
  return result
})

onMounted(async () => {
  await loadArticles()
})

watch(page, () => {
  loadArticles()
})

async function loadArticles() {
  loading.value = true
  try {
    const response = await articleApi.getList({ page: page.value, pageSize })
    articles.value = response.data.list || []
  } finally {
    loading.value = false
  }
}

function getStatusClass(status: number) {
  switch (status) {
    case 0: return 'draft'
    case 1: return 'published'
    case 2: return 'trash'
    default: return ''
  }
}

function getStatusText(status: number) {
  switch (status) {
    case 0: return '草稿'
    case 1: return '已发布'
    case 2: return '回收站'
    default: return '未知'
  }
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('zh-CN')
}

async function deleteArticle(article: Article) {
  if (!confirm('确定要删除这篇文章吗？')) return
  
  try {
    await articleApi.delete(article.id)
    article.status = 2
    alert('已移至回收站')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}

async function restoreArticle(article: Article) {
  try {
    await articleApi.update(article.id, { status: 0 })
    article.status = 0
    alert('已恢复为草稿')
  } catch (e: any) {
    alert(e.response?.data?.message || '恢复失败')
  }
}
</script>

<style scoped>
.article-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
}

.filter-select,
.search-input {
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  background: var(--bg-card);
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.table-container {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: var(--spacing-sm) var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.data-table th {
  background: var(--bg-hover);
  font-weight: 600;
  font-size: 0.9rem;
}

.title-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.status-badge.published {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.status-badge.draft {
  background: rgba(234, 179, 8, 0.1);
  color: #eab308;
}

.status-badge.trash {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.actions {
  display: flex;
  gap: var(--spacing-xs);
}

.btn-sm {
  padding: 4px 10px;
  border: none;
  border-radius: var(--border-radius-xs);
  font-size: 0.8rem;
  cursor: pointer;
  text-decoration: none;
  transition: all var(--transition-normal);
}

.btn-view {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.btn-view:hover {
  background: #a855f7;
  color: white;
}

.btn-edit {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.btn-edit:hover {
  background: #3b82f6;
  color: white;
}

.btn-delete {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.btn-delete:hover {
  background: #ef4444;
  color: white;
}

.btn-restore {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.btn-restore:hover {
  background: #22c55e;
  color: white;
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-muted);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-md);
}

.page-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.page-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
}
</style>
