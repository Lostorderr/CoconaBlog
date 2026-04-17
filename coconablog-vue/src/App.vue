<script setup lang="ts">
import { ref, onErrorCaptured, watch } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'

const route = useRoute()
const errorMap = ref<Record<string, { msg: string; time: number }>>({})

onErrorCaptured((err, instance, info) => {
  console.error('[App ErrorBoundary]', err, info)
  const currentPath = route.path
  errorMap.value[currentPath] = {
    msg: String(err.message || err),
    time: Date.now()
  }
  return false // 阻止错误继续传播，但不影响其他路由
})

// 路由切换时清除当前路由的错误状态（允许重新尝试渲染）
watch(() => route.path, () => {})

function hasErrorForCurrentRoute(): boolean {
  return !!errorMap.value[route.path]
}

function currentErrorMsg(): string {
  return errorMap.value[route.path]?.msg || ''
}

function retry() {
  delete errorMap.value[route.path]
}
</script>

<template>
  <div id="app">
    <Navbar />
    <main class="main-content">
      <!-- 当前路由出错时显示错误回退 -->
      <div v-if="hasErrorForCurrentRoute()" class="error-fallback">
        <div class="error-card">
          <h3>⚠️ 页面加载出错</h3>
          <p>{{ currentErrorMsg() }}</p>
          <button @click="retry" class="retry-btn">重新加载</button>
        </div>
      </div>
      <!-- 正常渲染路由 -->
      <router-view v-else v-slot="{ Component }">
        <Suspense>
          <template #default>
            <component :is="Component" />
          </template>
          <template #fallback>
            <div class="loading-fallback">加载中...</div>
          </template>
        </Suspense>
      </router-view>
    </main>
    <Footer />
  </div>
</template>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
}

.error-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
}

.error-card {
  text-align: center;
  padding: var(--spacing-2xl);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  max-width: 400px;
}

.error-card h3 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.error-card p {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-lg);
  word-break: break-all;
  font-size: 0.9rem;
}

.retry-btn {
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--gradient-primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.retry-btn:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-md);
}

.loading-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  color: var(--text-secondary);
  font-size: 1.2rem;
}
</style>
