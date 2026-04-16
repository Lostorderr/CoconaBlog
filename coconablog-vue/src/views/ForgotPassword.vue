<template>
  <div class="forgot-page">
    <div class="forgot-container">
      <div class="forgot-card">
        <div class="forgot-header">
          <div class="logo">🔑</div>
          <h1 class="title">找回密码</h1>
          <p class="subtitle">{{ step === 1 ? '验证你的身份' : '设置新密码' }}</p>
        </div>

        <!-- Step 1: 验证密保 -->
        <form v-if="step === 1" class="forgot-form" @submit.prevent="handleVerify">
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

          <div v-if="securityQuestion" class="form-group">
            <label class="form-label">密保问题</label>
            <div class="question-display">{{ securityQuestion }}</div>
          </div>

          <div class="form-group">
            <label class="form-label">密保答案</label>
            <input
              v-model="form.securityAnswer"
              type="text"
              class="form-input"
              placeholder="请输入密保答案"
              required
            />
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading || !securityQuestion">
            <span v-if="loading">验证中...</span>
            <span v-else>下一步</span>
          </button>

          <button type="button" class="btn btn-secondary btn-block" @click="fetchQuestion" :disabled="loading">
            获取密保问题
          </button>
        </form>

        <!-- Step 2: 设置新密码 -->
        <form v-else class="forgot-form" @submit.prevent="handleReset">
          <div class="success-badge">身份验证通过！</div>

          <div class="form-group">
            <label class="form-label">新密码</label>
            <input
              v-model="newPassword"
              type="password"
              class="form-input"
              placeholder="请输入新密码"
              required
              minlength="6"
            />
          </div>

          <div class="form-group">
            <label class="form-label">确认新密码</label>
            <input
              v-model="confirmPassword"
              type="password"
              class="form-input"
              placeholder="再次输入新密码"
              required
              minlength="6"
            />
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            <span v-if="loading">重置中...</span>
            <span v-else>重置密码</span>
          </button>
        </form>

        <div class="forgot-footer">
          <router-link to="/login" class="link">返回登录</router-link>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="success" class="success-message">{{ success }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useApi'

const router = useRouter()
const { getSecurityQuestion, verifySecurity, resetPassword, loading } = useAuth()

const step = ref(1)
const securityQuestion = ref('')
const error = ref('')
const success = ref('')

const form = reactive({
  username: '',
  securityAnswer: ''
})

const newPassword = ref('')
const confirmPassword = ref('')

async function fetchQuestion() {
  error.value = ''
  success.value = ''
  if (!form.username) {
    error.value = '请先输入用户名'
    return
  }
  try {
    const data = await getSecurityQuestion(form.username)
    if (data.securityQuestion) {
      securityQuestion.value = data.securityQuestion
    } else {
      error.value = '该用户未设置密保问题，请联系管理员'
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '获取密保问题失败'
  }
}

async function handleVerify() {
  error.value = ''
  success.value = ''
  try {
    const ok = await verifySecurity({ username: form.username, securityAnswer: form.securityAnswer })
    if (ok) {
      step.value = 2
      error.value = ''
    } else {
      error.value = '密保答案不正确'
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '验证失败'
  }
}

async function handleReset() {
  error.value = ''
  success.value = ''
  if (newPassword.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致'
    return
  }
  try {
    await resetPassword({ username: form.username, newPassword: newPassword.value })
    success.value = '密码重置成功！即将跳转到登录页...'
    setTimeout(() => router.push('/login'), 1500)
  } catch (e: any) {
    error.value = e.response?.data?.message || '重置密码失败'
  }
}
</script>

<style scoped>
.forgot-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: var(--spacing-lg);
}

.forgot-container {
  width: 100%;
  max-width: 420px;
}

.forgot-card {
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-2xl);
}

.forgot-header {
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

.forgot-form {
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
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.question-display {
  padding: var(--spacing-md);
  background: #f3f4f6;
  border-radius: var(--border-radius-sm);
  color: var(--text-primary);
  font-weight: 500;
}

.success-badge {
  text-align: center;
  padding: var(--spacing-md);
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border-radius: var(--border-radius-sm);
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.btn-block {
  width: 100%;
}

.btn-secondary {
  background: #f3f4f6;
  color: var(--text-primary);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: none;
  font-size: 1rem;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.forgot-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.link {
  color: #667eea;
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

.success-message {
  margin-top: var(--spacing-md);
  padding: var(--spacing-md);
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border-radius: var(--border-radius-sm);
  text-align: center;
}
</style>
