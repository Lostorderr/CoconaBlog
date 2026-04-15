<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="container">
        <div class="profile-info">
          <div class="profile-avatar">👤</div>
          <div class="profile-details">
            <h1 class="profile-name">{{ user?.username }}</h1>
            <p class="profile-email">{{ user?.email }}</p>
            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-value">{{ stats.articleCount }}</span>
                <span class="stat-label">文章</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ stats.totalViews }}</span>
                <span class="stat-label">浏览</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ stats.totalLikes }}</span>
                <span class="stat-label">获赞</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="container profile-content">
      <div class="profile-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          <span class="tab-icon">{{ tab.icon }}</span>
          <span>{{ tab.name }}</span>
        </button>
      </div>

      <div class="tab-content">
        <div v-if="activeTab === 'articles'" class="articles-tab">
          <div class="tab-header">
            <h2>我的文章</h2>
            <router-link to="/create-article" class="btn btn-primary">
              <span>✏️</span> 发布文章
            </router-link>
          </div>

          <div class="articles-filter">
            <select v-model="articleFilter" class="filter-select">
              <option value="all">全部文章</option>
              <option value="published">已发布</option>
              <option value="draft">草稿</option>
              <option value="trash">回收站</option>
            </select>
          </div>

          <div v-if="loading" class="loading-state">
            <span class="loading-spinner">🌸</span>
            <p>加载中...</p>
          </div>

          <div v-else-if="myArticles.length === 0" class="empty-state">
            <span class="empty-icon">📝</span>
            <p>暂无文章</p>
            <router-link to="/create-article" class="btn btn-primary">发布第一篇文章</router-link>
          </div>

          <div v-else class="articles-list">
            <div v-for="article in filteredArticles" :key="article.id" class="article-item">
              <div class="article-cover">
                <img :src="article.coverImage || defaultCover" :alt="article.title" />
              </div>
              <div class="article-info">
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <span>👁️</span> {{ article.viewCount }}
                  </span>
                  <span class="meta-item">
                    <span>💖</span> {{ article.likeCount }}
                  </span>
                  <span class="meta-item">
                    <span>💬</span> {{ article.commentCount }}
                  </span>
                  <span class="meta-item status" :class="getStatusClass(article.status)">
                    {{ getStatusText(article.status) }}
                  </span>
                </div>
              </div>
              <div class="article-actions">
                <router-link :to="`/edit-article/${article.id}`" class="action-btn edit" v-if="article.status !== 2">
                  <span>✏️</span>
                </router-link>
                <button class="action-btn delete" @click="handleDelete(article)" v-if="article.status !== 2">
                  <span>🗑️</span>
                </button>
                <button class="action-btn restore" @click="handleRestore(article)" v-if="article.status === 2">
                  <span>♻️</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'settings'" class="settings-tab">
          <h2>个人设置</h2>
          
          <form class="settings-form" @submit.prevent="handleUpdateProfile">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input v-model="profileForm.username" type="text" class="form-input" />
            </div>
            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input v-model="profileForm.email" type="email" class="form-input" />
            </div>
            <div class="form-group">
              <label class="form-label">头像URL</label>
              <input v-model="profileForm.avatar" type="url" class="form-input" placeholder="头像图片地址" />
            </div>
            <button type="submit" class="btn btn-primary" :disabled="updating">
              {{ updating ? '保存中...' : '保存修改' }}
            </button>
          </form>
        </div>

        <div v-if="activeTab === 'comments'" class="comments-tab">
          <h2>我的评论</h2>
          
          <div v-if="myComments.length === 0" class="empty-state">
            <span class="empty-icon">💬</span>
            <p>暂无评论</p>
          </div>

          <div v-else class="comments-list">
            <div v-for="comment in myComments" :key="comment.id" class="comment-item">
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-meta">
                <span>发表于 {{ formatDate(comment.createTime) }}</span>
                <span>《{{ comment.article?.title || '未知文章' }}》</span>
              </div>
              <button class="delete-btn" @click="handleDeleteComment(comment)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useApi'
import { useBlogStore, type Article, type CommentInfo } from '@/store/blog'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'

const router = useRouter()
const { user, isLoggedIn } = useAuth()
const blogStore = useBlogStore()

const activeTab = ref('articles')
const articleFilter = ref('all')
const loading = ref(false)
const updating = ref(false)
const myArticles = ref<Article[]>([])
const myComments = ref<CommentInfo[]>([])
const defaultCover = 'https://picsum.photos/seed/default/300/200'

const tabs = [
  { key: 'articles', name: '我的文章', icon: '📝' },
  { key: 'comments', name: '我的评论', icon: '💬' },
  { key: 'settings', name: '个人设置', icon: '⚙️' }
]

const stats = reactive({
  articleCount: 0,
  totalViews: 0,
  totalLikes: 0
})

const profileForm = reactive({
  username: '',
  email: '',
  avatar: ''
})

const filteredArticles = computed(() => {
  if (articleFilter.value === 'all') return myArticles.value
  if (articleFilter.value === 'published') return myArticles.value.filter(a => a.status === 1)
  if (articleFilter.value === 'draft') return myArticles.value.filter(a => a.status === 0)
  if (articleFilter.value === 'trash') return myArticles.value.filter(a => a.status === 2)
  return myArticles.value
})

onMounted(async () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  if (user.value) {
    profileForm.username = user.value.username
    profileForm.email = user.value.email
    profileForm.avatar = user.value.avatar || ''
  }
  
  await loadMyArticles()
  await loadMyComments()
})

async function loadMyArticles() {
  loading.value = true
  try {
    const response = await articleApi.getMyArticles()
    myArticles.value = response.data.list || response.data
    stats.articleCount = myArticles.value.filter(a => a.status === 1).length
    stats.totalViews = myArticles.value.reduce((sum, a) => sum + a.viewCount, 0)
    stats.totalLikes = myArticles.value.reduce((sum, a) => sum + a.likeCount, 0)
  } catch {
    myArticles.value = []
  } finally {
    loading.value = false
  }
}

async function loadMyComments() {
  try {
    const response = await commentApi.getMyComments()
    myComments.value = response.data.list || response.data
  } catch {
    myComments.value = []
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

async function handleDelete(article: Article) {
  if (!confirm('确定要删除这篇文章吗？')) return
  
  try {
    await articleApi.delete(article.id)
    article.status = 2
    alert('文章已移至回收站')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}

async function handleRestore(article: Article) {
  try {
    await articleApi.update(article.id, { status: 0 })
    article.status = 0
    alert('文章已恢复为草稿')
  } catch (e: any) {
    alert(e.response?.data?.message || '恢复失败')
  }
}

async function handleUpdateProfile() {
  updating.value = true
  try {
    alert('个人资料更新成功')
  } catch (e: any) {
    alert(e.response?.data?.message || '更新失败')
  } finally {
    updating.value = false
  }
}

async function handleDeleteComment(comment: CommentInfo) {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    await commentApi.delete(comment.id)
    myComments.value = myComments.value.filter(c => c.id !== comment.id)
    alert('评论已删除')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.profile-header {
  background: var(--gradient-primary);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.profile-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

.profile-avatar {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  box-shadow: var(--shadow-lg);
}

.profile-name {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
}

.profile-email {
  opacity: 0.9;
  margin-bottom: var(--spacing-md);
}

.profile-stats {
  display: flex;
  gap: var(--spacing-xl);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.profile-content {
  padding: var(--spacing-xl) var(--spacing-lg);
}

.profile-tabs {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xl);
  border-bottom: 2px solid var(--border-color);
  padding-bottom: var(--spacing-md);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background: none;
  border: none;
  font-size: 1rem;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.tab-btn:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.tab-btn.active {
  background: var(--gradient-primary);
  color: white;
}

.tab-icon {
  font-size: 1.2rem;
}

.tab-content {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.articles-filter {
  margin-bottom: var(--spacing-lg);
}

.filter-select {
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  background: var(--bg-card);
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.article-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.article-item:hover {
  box-shadow: var(--shadow-sm);
}

.article-cover {
  width: 150px;
  height: 100px;
  border-radius: var(--border-radius-sm);
  overflow: hidden;
  flex-shrink: 0;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-info {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
  color: var(--text-primary);
}

.article-summary {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: 0.85rem;
  color: var(--text-muted);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.meta-item.status {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.meta-item.status.published {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.meta-item.status.draft {
  background: rgba(234, 179, 8, 0.1);
  color: #eab308;
}

.meta-item.status.trash {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.article-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  cursor: pointer;
  transition: all var(--transition-normal);
  text-decoration: none;
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-btn.edit:hover {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
}

.action-btn.delete:hover {
  border-color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.action-btn.restore:hover {
  border-color: #22c55e;
  background: rgba(34, 197, 94, 0.1);
}

.loading-state,
.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
}

.loading-spinner {
  font-size: 3rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.settings-form {
  max-width: 500px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  font-weight: 500;
  color: var(--text-primary);
}

.form-input {
  padding: var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  transition: all var(--transition-normal);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-item {
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  position: relative;
}

.comment-content {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.comment-meta {
  font-size: 0.85rem;
  color: var(--text-muted);
  display: flex;
  gap: var(--spacing-md);
}

.delete-btn {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: none;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.8rem;
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.delete-btn:hover {
  border-color: #ef4444;
  color: #ef4444;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    text-align: center;
  }

  .profile-stats {
    justify-content: center;
  }

  .article-item {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 150px;
  }

  .article-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}
</style>
