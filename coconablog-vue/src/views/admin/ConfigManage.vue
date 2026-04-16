<template>
  <div class="config-management">
    <div class="management-header">
      <h2>系统配置</h2>
      <button class="btn btn-primary" @click="saveAllConfigs" :disabled="saving">
        {{ saving ? '保存中...' : '保存所有配置' }}
      </button>
    </div>

    <div class="config-sections">
      <div class="config-section">
        <h3 class="section-title">基本信息</h3>
        <div class="config-form">
          <div class="form-group">
            <label>网站名称</label>
            <input v-model="configs.siteName" type="text" />
          </div>
          <div class="form-group">
            <label>网站描述</label>
            <textarea v-model="configs.siteDescription" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>网站关键词</label>
            <input v-model="configs.siteKeywords" type="text" placeholder="用逗号分隔" />
          </div>
        </div>
      </div>

      <div class="config-section">
        <h3 class="section-title">页脚设置</h3>
        <div class="config-form">
          <div class="form-group">
            <label>页脚文字</label>
            <textarea v-model="configs.footerText" rows="2"></textarea>
          </div>
        </div>
      </div>

      <div class="config-section">
        <h3 class="section-title">社交链接</h3>
        <div class="config-form">
          <div class="form-group">
            <label>GitHub</label>
            <input v-model="configs.socialGithub" type="url" placeholder="GitHub 主页地址" />
          </div>
          <div class="form-group">
            <label>Bilibili</label>
            <input v-model="configs.socialBilibili" type="url" placeholder="Bilibili 主页地址" />
          </div>
          <div class="form-group">
            <label>联系邮箱</label>
            <input v-model="configs.contactEmail" type="email" placeholder="联系邮箱地址" />
          </div>
        </div>
      </div>

      <div class="config-section">
        <h3 class="section-title">其他配置</h3>
        <div class="config-list">
          <div v-for="config in otherConfigs" :key="config.key" class="config-item">
            <div class="config-info">
              <span class="config-key">{{ config.key }}</span>
              <span class="config-desc">{{ config.description || '-' }}</span>
            </div>
            <input v-model="config.value" type="text" class="config-input" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { configApi } from '@/api/config'

const saving = ref(false)
const loading = ref(false)

const keyMap: Record<string, string> = {
  siteName: 'site_name',
  siteDescription: 'site_description',
  siteKeywords: 'site_keywords',
  footerText: 'footer_text',
  socialGithub: 'social_github',
  socialBilibili: 'social_bilibili',
  contactEmail: 'contact_email'
}

const reverseKeyMap: Record<string, string> = Object.fromEntries(
  Object.entries(keyMap).map(([k, v]) => [v, k])
)

const configs = reactive({
  siteName: 'Cocona Blog',
  siteDescription: '',
  siteKeywords: '',
  footerText: '',
  socialGithub: 'https://github.com/Lostorderr',
  socialBilibili: 'https://space.bilibili.com/113969638',
  contactEmail: '842622982@qq.com'
})

const otherConfigs = ref<Array<{ key: string; value: string; description?: string }>>([])

onMounted(async () => {
  await loadConfigs()
})

async function loadConfigs() {
  loading.value = true
  try {
    const response = await configApi.getAll()
    const list = response.data || []
    
    list.forEach((item: any) => {
      const camelKey = reverseKeyMap[item.key]
      if (camelKey && camelKey in configs) {
        (configs as any)[camelKey] = item.value || ''
      } else if (camelKey) {
        (configs as any)[item.key] = item.value || ''
      } else {
        otherConfigs.value.push({
          key: item.key,
          value: item.value || '',
          description: item.description
        })
      }
    })
  } finally {
    loading.value = false
  }
}

async function saveAllConfigs() {
  saving.value = true
  try {
    const allConfigs = [
      { key: keyMap.siteName, value: configs.siteName },
      { key: keyMap.siteDescription, value: configs.siteDescription },
      { key: keyMap.siteKeywords, value: configs.siteKeywords },
      { key: keyMap.footerText, value: configs.footerText },
      { key: keyMap.socialGithub, value: configs.socialGithub },
      { key: keyMap.socialBilibili, value: configs.socialBilibili },
      { key: keyMap.contactEmail, value: configs.contactEmail },
      ...otherConfigs.value
    ]

    await Promise.all(
      allConfigs.map(c => configApi.update(c.key, { value: c.value }))
    )

    alert('配置保存成功')
  } catch (e: any) {
    alert(e.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.config-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config-sections {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.config-section {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.config-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.form-group label {
  font-weight: 500;
  color: var(--text-primary);
}

.form-group input,
.form-group textarea {
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  transition: all var(--transition-normal);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

.config-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.config-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.config-key {
  font-weight: 500;
  color: var(--text-primary);
  font-family: monospace;
}

.config-desc {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.config-input {
  width: 300px;
  padding: var(--spacing-xs) var(--spacing-sm);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.9rem;
}

.config-input:focus {
  outline: none;
  border-color: var(--primary-color);
}
</style>
