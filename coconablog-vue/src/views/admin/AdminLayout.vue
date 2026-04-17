<template>
  <div class="admin-page">
    <div class="admin-sidebar">
      <div class="sidebar-header">
        <span class="logo">CB</span>
        <span class="title">管理后台</span>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" exact-active-class="active">
          <span class="nav-icon"></span>
          <span>数据概览</span>
        </router-link>
        <router-link to="/admin/articles" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>文章管理</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>分类管理</span>
        </router-link>
        <router-link to="/admin/tags" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>标签管理</span>
        </router-link>
        <router-link to="/admin/comments" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>评论管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/config" class="nav-item" active-class="active">
          <span class="nav-icon"></span>
          <span>系统配置</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/" class="back-link">
          <span></span>
          <span>返回前台</span>
        </router-link>
      </div>
    </div>

    <div class="admin-main">
      <header class="admin-header">
        <h1 class="page-title">{{ pageTitle }}</h1>
        <div class="header-actions">
          <span class="user-info">
            <span class="user-avatar">{{ user?.username?.charAt(0) }}</span>
            <span>{{ user?.username }}</span>
          </span>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/composables/useApi'

const route = useRoute()
const router = useRouter()
const { user, isAdmin } = useAuth()

const pageTitle = computed(() => {
  const path = route.path
  if (path === '/admin') return '数据概览'
  if (path === '/admin/articles') return '文章管理'
  if (path === '/admin/categories') return '分类管理'
  if (path === '/admin/tags') return '标签管理'
  if (path === '/admin/comments') return '评论管理'
  if (path === '/admin/users') return '用户管理'
  if (path === '/admin/config') return '系统配置'
  return '管理后台'
})

onMounted(() => {
  if (!isAdmin.value) {
    alert('无权访问管理后台')
    router.push('/')
  }
})
</script>

<style scoped>
.admin-page {
  display: flex;
  min-height: 100vh;
  background: var(--bg-secondary);
}

.admin-sidebar {
  width: 240px;
  background: linear-gradient(180deg, #2d1b30 0%, #4a2c4e 100%);
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
}

.sidebar-header {
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 2rem;
}

.title {
  font-size: 1.2rem;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: var(--gradient-primary);
  color: white;
}

.nav-icon {
  font-size: 1.2rem;
}

.sidebar-footer {
  padding: var(--spacing-md);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.back-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: var(--bg-card);
  padding: var(--spacing-lg) var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.user-avatar {
  font-size: 1.2rem;
}

.admin-content {
  padding: var(--spacing-xl);
  flex: 1;
}

@media (max-width: 1024px) {
  .admin-sidebar {
    width: 60px;
  }

  .sidebar-header .title,
  .nav-item span:last-child,
  .back-link span:last-child {
    display: none;
  }

  .admin-main {
    margin-left: 60px;
  }
}
</style>
