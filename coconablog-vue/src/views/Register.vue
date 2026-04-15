<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <div class="logo">✨</div>
          <h1 class="title">创建账户</h1>
          <p class="subtitle">开启你的博客之旅</p>
        </div>

        <form class="register-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">邮箱</label>
            <input
              v-model="form.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            <span v-if="loading">注册中...</span>
            <span v-else>注册</span>
          </button>
        </form>

        <div class="register-footer">
          <p>已有账户？</p>
          <router-link to="/login" class="link">立即登录</router-link>
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useApi'

const router = useRouter()
const { register, loading } = useAuth()

const form = reactive({
  username: '',
  email: '',
  password: ''
})

const error = ref('')

async function handleRegister() {
  error.value = ''
  try {
    await register(form)
    router.push('/login')
  } catch (e: any) {
    error.value = e.response?.data?.message || '注册失败'
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  padding: var(--spacing-lg);
}

.register-container {
  width: 100%;
  max-width: 420px;
}

.register-card {
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-2xl);
}

.register-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.logo {
  font-size: 4rem;
  margin-bottom: var(--spacing-md);
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.subtitle {
  color: var(--text-secondary);
}

.register-form {
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
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.btn-block {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.register-footer p {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
}

.link {
  color: var(--primary-color);
  font-weight: 500;
}

.error-message {
  margin-top: var(--spacing-md);
  padding: var(--spacing-md);
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border-radius: var(--border-radius-sm);
  text-align: center;
}
</style>
