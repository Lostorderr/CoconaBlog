<template>
  <nav class="navbar">
    <div class="container navbar-container">
      <div class="navbar-left">
        <button class="menu-toggle" @click="toggleMenu">
          <span class="hamburger" :class="{ active: isMenuOpen }">
            <span></span>
            <span></span>
            <span></span>
          </span>
        </button>
        <router-link to="/" class="navbar-brand">
          <span class="logo-text">Cocona Blog</span>
        </router-link>
      </div>

      <div class="navbar-center" v-show="!isMenuOpen">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="navbar-item"
        >
          <span class="item-icon">{{ item.icon }}</span>
          <span class="item-text">{{ item.name }}</span>
        </router-link>
      </div>

      <div class="navbar-right">
        <button class="search-btn" @click="toggleSearch">
          <span>🔍</span>
        </button>

        <div v-if="!isLoggedIn" class="auth-links">
          <router-link to="/login" class="auth-link">
            <span>👤 登录</span>
          </router-link>
          <router-link to="/register" class="auth-link register">
            <span>📝 注册</span>
          </router-link>
        </div>

        <div v-else class="user-menu">
          <div class="user-info" @click="toggleUserMenu">
            <span class="user-avatar">
              <img v-if="user?.avatar" :src="user.avatar" alt="头像" />
              <span v-else>{{ user?.username?.charAt(0) }}</span>
            </span>
            <span class="user-name">{{ user?.username }}</span>
            <span class="user-dropdown">▼</span>
          </div>
          <div class="user-dropdown-menu" :class="{ active: isUserMenuOpen }">
            <router-link to="/profile" class="dropdown-item" @click="closeUserMenu">
              <span>👤 个人中心</span>
            </router-link>
            <router-link to="/create-article" class="dropdown-item" @click="closeUserMenu">
              <span>✏️ 发布文章</span>
            </router-link>
            <router-link v-if="isAdmin" to="/admin" class="dropdown-item admin" @click="closeUserMenu">
              <span>⚙️ 管理后台</span>
            </router-link>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item" @click="handleLogout">
              <span>🚪 退出登录</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="menu-dropdown" :class="{ active: isMenuOpen }">
      <div class="container">
        <div class="menu-dropdown-content">
          <div class="menu-section">
            <h4 class="menu-section-title">导航</h4>
            <router-link 
              v-for="item in menuItems" 
              :key="item.path" 
              :to="item.path" 
              class="menu-dropdown-item"
              @click="closeMenu"
            >
              <span class="menu-item-icon">{{ item.icon }}</span>
              <span>{{ item.name }}</span>
            </router-link>
          </div>
          <div class="menu-section">
            <h4 class="menu-section-title">分类</h4>
            <div v-if="categories.length > 0" class="menu-categories">
              <router-link 
                v-for="cat in categories" 
                :key="cat.id" 
                :to="`/articles?category=${cat.id}`"
                class="menu-category-tag"
                @click="closeMenu"
              >
                {{ cat.name }}
              </router-link>
            </div>
            <div v-else class="menu-empty">暂无分类</div>
          </div>
          <div class="menu-section">
            <h4 class="menu-section-title">热门标签</h4>
            <div v-if="tags.length > 0" class="menu-tags">
              <router-link 
                v-for="tag in tags" 
                :key="tag.id" 
                :to="`/articles?tag=${tag.id}`"
                class="menu-tag"
                @click="closeMenu"
              >
                #{{ tag.name }}
              </router-link>
            </div>
            <div v-else class="menu-empty">暂无标签</div>
          </div>
        </div>
      </div>
    </div>

    <transition name="fade">
      <div v-if="showSearch" class="search-overlay" @click="closeSearch">
        <div class="search-container" @click.stop>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索文章..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-submit" @click="handleSearch">
            <span>搜索</span>
          </button>
        </div>
      </div>
    </transition>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useApi'
import { useBlogStore } from '@/store/blog'
import { storeToRefs } from 'pinia'

const router = useRouter()
const { user, isLoggedIn, logout } = useAuth()
const blogStore = useBlogStore()
const { categories, tags } = storeToRefs(blogStore)

const isMenuOpen = ref(false)
const showSearch = ref(false)
const searchQuery = ref('')
const isUserMenuOpen = ref(false)

const isAdmin = computed(() => user.value?.role === 1)

const menuItems = [
  { name: '首页', path: '/', icon: '' },
  { name: '文章', path: '/articles', icon: '' },
  { name: '关于', path: '/about', icon: '' }
]

onMounted(async () => {
  await blogStore.fetchCategories()
  await blogStore.fetchTags()
})

function toggleMenu() {
  isMenuOpen.value = !isMenuOpen.value
}

function closeMenu() {
  isMenuOpen.value = false
}

function toggleSearch() {
  showSearch.value = !showSearch.value
}

function closeSearch() {
  showSearch.value = false
}

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/articles',
      query: { search: searchQuery.value }
    })
    searchQuery.value = ''
    closeSearch()
  }
}

function toggleUserMenu() {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

function closeUserMenu() {
  isUserMenuOpen.value = false
}

async function handleLogout() {
  await logout()
  isUserMenuOpen.value = false
  window.location.href = '/'
}
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.navbar-center {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.menu-toggle {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.menu-toggle:hover {
  background: var(--bg-hover);
  border-color: var(--primary-color);
  transform: scale(1.1);
}

.hamburger {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 20px;
  height: 18px;
  gap: 4px;
}

.hamburger span {
  display: block;
  width: 100%;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all var(--transition-normal);
}

.hamburger.active span:nth-child(1) {
  transform: rotate(45deg) translate(4px, 4px);
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
  transform: scaleX(0);
}

.hamburger.active span:nth-child(3) {
  transform: rotate(-45deg) translate(4px, -4px);
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.navbar-brand:hover {
  transform: scale(1.05);
}

.logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
  position: relative;
}

.navbar-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 3px;
  transform: translateX(-50%);
  transition: width var(--transition-normal);
}

.navbar-item:hover {
  color: var(--primary-color);
  background: var(--bg-hover);
}

.navbar-item:hover::after {
  width: 80%;
}

.navbar-item.router-link-active {
  color: var(--primary-color);
}

.navbar-item.router-link-active::after {
  width: 80%;
}

.item-icon {
  font-size: 1.2rem;
}

.search-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1.2rem;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.search-btn:hover {
  background: var(--bg-hover);
  border-color: var(--primary-color);
  transform: scale(1.1);
}

.auth-links {
  display: flex;
  gap: var(--spacing-sm);
}

.auth-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  text-decoration: none;
  color: var(--text-secondary);
  font-weight: 500;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.auth-link:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.auth-link.register {
  background: var(--gradient-primary);
  color: white;
}

.auth-link.register:hover {
  color: white;
  transform: scale(1.05);
  box-shadow: var(--shadow-md);
}

.user-menu {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  cursor: pointer;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.user-info:hover {
  background: var(--bg-hover);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-weight: 500;
  color: var(--text-primary);
}

.user-dropdown {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.user-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: var(--bg-card);
  border-radius: var(--border-radius-sm);
  box-shadow: var(--shadow-md);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-xs);
  min-width: 160px;
  opacity: 0;
  pointer-events: none;
  transform: translateY(-10px);
  transition: all var(--transition-normal);
}

.user-dropdown-menu.active {
  opacity: 1;
  pointer-events: all;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  background: none;
  border: none;
  border-radius: var(--border-radius-xs);
  cursor: pointer;
  font-size: 0.9rem;
  color: var(--text-secondary);
  text-align: left;
  text-decoration: none;
  transition: all var(--transition-normal);
}

.dropdown-item:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.dropdown-item.admin {
  color: #a855f7;
}

.dropdown-item.admin:hover {
  background: rgba(168, 85, 247, 0.1);
}

.dropdown-divider {
  height: 1px;
  background: var(--border-color);
  margin: var(--spacing-xs) 0;
}

.menu-dropdown {
  max-height: 0;
  overflow: hidden;
  background: var(--bg-card);
  border-bottom: 2px solid var(--border-color);
  transition: max-height var(--transition-normal);
}

.menu-dropdown.active {
  max-height: 500px;
}

.menu-dropdown-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xl);
  padding: var(--spacing-xl) 0;
}

.menu-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.menu-section-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-color);
}

.menu-dropdown-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.menu-dropdown-item:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.menu-item-icon {
  font-size: 1.2rem;
}

.menu-categories,
.menu-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.menu-category-tag {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--bg-hover);
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 20px;
  font-size: 0.9rem;
  transition: all var(--transition-normal);
}

.menu-category-tag:hover {
  background: var(--primary-color);
  color: white;
}

.menu-tag {
  padding: var(--spacing-xs) var(--spacing-sm);
  background: rgba(255, 107, 157, 0.1);
  color: var(--primary-color);
  text-decoration: none;
  border-radius: var(--border-radius-xs);
  font-size: 0.85rem;
  transition: all var(--transition-normal);
}

.menu-tag:hover {
  background: var(--primary-color);
  color: white;
}

.menu-empty {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(45, 27, 48, 0.8);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 150px;
  z-index: 2000;
}

.search-container {
  display: flex;
  gap: var(--spacing-sm);
  background: var(--bg-card);
  padding: var(--spacing-md);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
  animation: slideIn 0.3s ease-out;
}

.search-input {
  width: 400px;
  padding: var(--spacing-md) var(--spacing-lg);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  font-family: var(--font-sans);
  transition: all var(--transition-normal);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.search-submit {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--border-radius-sm);
  font-size: 1.2rem;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.search-submit:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-md);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1024px) {
  .navbar-center {
    display: none;
  }

  .menu-dropdown-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .logo-text {
    display: none;
  }

  .auth-links {
    gap: var(--spacing-xs);
  }

  .auth-link span:last-child {
    display: none;
  }

  .user-name {
    display: none;
  }

  .user-dropdown {
    display: none;
  }

  .search-input {
    width: 250px;
  }
}
</style>
