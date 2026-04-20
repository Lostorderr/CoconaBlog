<template>
  <aside class="sidebar">
    <div class="sidebar-section card">
      <h3 class="section-title">
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
        <span>🎲 随机推荐</span>
      </h3>
      <div v-if="randomList.length === 0" class="sidebar-empty">加载中...</div>
      <div v-else class="random-list">
        <div
          v-for="item in randomList"
          :key="item.id"
          class="random-item"
          @click="$router.push(`/article/${item.id}`)"
        >
          <span class="random-title">{{ item.title }}</span>
          <span class="random-stats">👀 {{ item.viewCount }}  ❤️ {{ item.likeCount }}</span>
        </div>
      </div>
    </div>

    <div class="sidebar-section card">
      <h3 class="section-title">
        <span>👩‍💻 关于博主</span>
      </h3>
      <div class="author-card">
        <img src="/images/profilePicture/仙狐头像.png" alt="wuji" class="author-avatar" />
        <div class="author-name">wuji</div>
        <div class="author-bio">
          一个热爱编程的开发者，喜欢探索新技术，
          分享学习心得，记录生活点滴
        </div>
        <div class="social-links">
          <a href="https://github.com/Lostorderr" target="_blank" class="social-link" title="GitHub">
            <span>🐙 GitHub</span>
          </a>
          <a href="https://space.bilibili.com/113969638" target="_blank" class="social-link" title="Bilibili">
            <span>📺 Bilibili</span>
          </a>
          <a href="mailto:842622982@qq.com" class="social-link" title="Email">
            <span>📧 Email</span>
          </a>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'
import { articleApi } from '@/api/article'

const router = useRouter()
const blogStore = useBlogStore()
const randomList = ref<any[]>([])

onMounted(async () => {
  blogStore.fetchCategories()
  blogStore.fetchTags()
  try {
    const res = await articleApi.getList({ pageSize: 12 })
    const list = res.data.list
    for (let i = list.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1))
      ;[list[i], list[j]] = [list[j], list[i]]
    }
    randomList.value = list.slice(0, 5)
  } catch {
    randomList.value = []
  }
})

function formatNumber(num: number): string {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
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

.sidebar-empty {
  color: var(--text-muted);
  font-size: 0.9rem;
  text-align: center;
  padding: var(--spacing-md);
}

.random-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.random-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.random-item:hover {
  background: var(--primary-color);
  color: white;
  transform: translateX(4px);
}

.random-title {
  font-weight: 500;
  font-size: 0.88rem;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.random-stats {
  font-size: 0.78rem;
  opacity: 0.7;
}

.author-card {
  text-align: center;
}

.author-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-md);
  border-radius: 50%;
  object-fit: cover;
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
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.social-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.social-link:hover {
  background: var(--gradient-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
  color: white;
}

@media (max-width: 1024px) {
  .sidebar {
    display: none;
  }
}
</style>
