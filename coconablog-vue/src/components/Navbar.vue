<template>
  <nav class="navbar">
    <div class="container navbar-container">
      <router-link to="/" class="navbar-brand">
        <span class="logo-icon">🌸</span>
        <span class="logo-text">Cocona Blog</span>
      </router-link>

      <div class="navbar-menu" :class="{ active: isMenuOpen }">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="navbar-item"
          @click="closeMenu"
        >
          <span class="item-icon">{{ item.icon }}</span>
          <span class="item-text">{{ item.name }}</span>
        </router-link>
      </div>

      <div class="navbar-actions">
        <button class="search-btn" @click="toggleSearch">
          <span>🔍</span>
        </button>
        <button class="menu-toggle" @click="toggleMenu">
          <span v-if="!isMenuOpen">☰</span>
          <span v-else>✕</span>
        </button>
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
            <span>🔍</span>
          </button>
        </div>
      </div>
    </transition>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isMenuOpen = ref(false)
const showSearch = ref(false)
const searchQuery = ref('')

const menuItems = [
  { name: '首页', path: '/', icon: '🏠' },
  { name: '文章', path: '/articles', icon: '📝' },
  { name: '关于', path: '/about', icon: '💖' }
]

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
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: var(--shadow-sm);
  z-index: 1000;
  border-bottom: 2px solid var(--border-color);
}

.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
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

.logo-icon {
  font-size: 2rem;
  animation: float 3s ease-in-out infinite;
}

.logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-menu {
  display: flex;
  gap: var(--spacing-md);
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

.navbar-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.search-btn,
.menu-toggle {
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

.search-btn:hover,
.menu-toggle:hover {
  background: var(--bg-hover);
  border-color: var(--primary-color);
  transform: scale(1.1);
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

@media (max-width: 768px) {
  .navbar-menu {
    position: fixed;
    top: 70px;
    left: 0;
    right: 0;
    background: var(--bg-card);
    flex-direction: column;
    padding: var(--spacing-lg);
    box-shadow: var(--shadow-lg);
    transform: translateY(-100%);
    opacity: 0;
    pointer-events: none;
    transition: all var(--transition-normal);
  }

  .navbar-menu.active {
    transform: translateY(0);
    opacity: 1;
    pointer-events: all;
  }

  .search-input {
    width: 250px;
  }
}
</style>
