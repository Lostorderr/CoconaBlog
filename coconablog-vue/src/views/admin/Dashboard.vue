<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">文章</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.articleCount }}</div>
          <div class="stat-label">文章总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">浏览</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalViews }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">点赞</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalLikes }}</div>
          <div class="stat-label">总点赞数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">评论</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.commentCount }}</div>
          <div class="stat-label">评论总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">用户</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">分类</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.categoryCount }}</div>
          <div class="stat-label">分类数量</div>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="dashboard-card">
        <h3 class="card-title">
          <span></span>
          <span>最新文章</span>
        </h3>
        <div class="card-content">
          <div v-if="recentArticles.length === 0" class="empty">暂无文章</div>
          <div v-else class="article-list">
            <div v-for="article in recentArticles" :key="article.id" class="article-item">
              <div class="article-info">
                <span class="article-title">{{ article.title }}</span>
                <span class="article-date">{{ formatDate(article.createTime) }}</span>
              </div>
              <div class="article-stats">
                <span>{{ article.viewCount }}</span>
                <span>{{ article.likeCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-card">
        <h3 class="card-title">
          <span></span>
          <span>最新评论</span>
        </h3>
        <div class="card-content">
          <div v-if="recentComments.length === 0" class="empty">暂无评论</div>
          <div v-else class="comment-list">
            <div v-for="comment in recentComments" :key="comment.id" class="comment-item">
              <div class="comment-user">{{ comment.user?.username || '匿名' }}</div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-date">{{ formatDate(comment.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="dashboard-card full-width">
      <h3 class="card-title">
        <span></span>
        <span>热门文章</span>
      </h3>
      <div class="card-content">
        <div v-if="hotArticles.length === 0" class="empty">暂无数据</div>
        <div v-else class="hot-articles">
          <div v-for="(article, index) in hotArticles" :key="article.id" class="hot-item">
            <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
            <span class="title">{{ article.title }}</span>
            <span class="views">{{ article.viewCount }}</span>
            <span class="likes">{{ article.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'
import { categoryApi } from '@/api/category'
import { authApi } from '@/api/auth'
import type { Article, CommentInfo } from '@/store/blog'

const stats = reactive({
  articleCount: 0,
  totalViews: 0,
  totalLikes: 0,
  commentCount: 0,
  userCount: 0,
  categoryCount: 0
})

const recentArticles = ref<Article[]>([])
const recentComments = ref<CommentInfo[]>([])
const hotArticles = ref<Article[]>([])

onMounted(async () => {
  await loadStats()
})

async function loadStats() {
  try {
    const [articlesRes, commentsRes, categoriesRes, usersRes] = await Promise.all([
      articleApi.getList({ pageSize: 100 }),
      commentApi.getAll({ page: 1, pageSize: 10 }),
      categoryApi.getList(),
      authApi.getAllUsers({ page: 1, pageSize: 1 })
    ])

    const articles = articlesRes.data.list || []
    stats.articleCount = articlesRes.data.total || articles.length
    stats.totalViews = articles.reduce((sum: number, a: Article) => sum + a.viewCount, 0)
    stats.totalLikes = articles.reduce((sum: number, a: Article) => sum + a.likeCount, 0)
    stats.categoryCount = categoriesRes.data.length
    stats.commentCount = commentsRes.data.total || 0
    stats.userCount = usersRes.data.total || 0

    recentArticles.value = articles.slice(0, 5)
    hotArticles.value = [...articles].sort((a, b) => b.viewCount - a.viewCount).slice(0, 10)
    recentComments.value = commentsRes.data.list || []
  } catch {
  }
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--spacing-md);
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  font-size: 2.5rem;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);
}

.dashboard-card {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.dashboard-card.full-width {
  grid-column: 1 / -1;
}

.card-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-hover);
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-color);
}

.card-content {
  padding: var(--spacing-md);
  max-height: 300px;
  overflow-y: auto;
}

.empty {
  text-align: center;
  color: var(--text-muted);
  padding: var(--spacing-xl);
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.article-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.article-title {
  font-weight: 500;
  color: var(--text-primary);
}

.article-date {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.article-stats {
  display: flex;
  gap: var(--spacing-sm);
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.comment-item {
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.comment-user {
  font-weight: 500;
  color: var(--primary-color);
  margin-bottom: 2px;
}

.comment-content {
  font-size: 0.9rem;
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.comment-date {
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-top: 4px;
}

.hot-articles {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.hot-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--border-color);
  border-radius: 50%;
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.rank.top {
  background: var(--gradient-primary);
  color: white;
}

.hot-item .title {
  flex: 1;
  font-size: 0.95rem;
  color: var(--text-primary);
}

.hot-item .views,
.hot-item .likes {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
