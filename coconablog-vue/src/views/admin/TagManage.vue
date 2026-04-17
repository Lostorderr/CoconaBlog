<template>
  <div class="tag-management">
    <div class="management-header">
      <h2>标签管理</h2>
      <button class="btn btn-primary" @click="showCreateModal = true">
        <span>+</span> 新建标签
      </button>
    </div>

    <div class="tag-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="tags.length === 0" class="empty">暂无标签</div>
      <div v-else class="tags-grid">
        <div v-for="tag in tags" :key="tag.id" class="tag-card">
          <div class="tag-info">
            <span class="tag-name">{{ tag.name }}</span>
            <span class="tag-slug">{{ tag.slug }}</span>
          </div>
          <div class="tag-actions">
            <button class="btn-sm btn-edit" @click="editTag(tag)">编辑</button>
            <button class="btn-sm btn-delete" @click="deleteTag(tag)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑标签' : '新建标签' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>标签名称 *</label>
            <input v-model="form.name" type="text" required />
          </div>
          <div class="form-group">
            <label>URL标识 *</label>
            <input v-model="form.slug" type="text" required />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { tagApi } from '@/api/tag'
import type { TagInfo } from '@/api/types'

const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const tags = ref<TagInfo[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  slug: ''
})

onMounted(async () => {
  await loadTags()
})

async function loadTags() {
  loading.value = true
  try {
    const response = await tagApi.getAll()
    tags.value = response.data
  } finally {
    loading.value = false
  }
}

function editTag(tag: TagInfo) {
  editingId.value = tag.id
  form.name = tag.name
  form.slug = tag.slug
  showEditModal.value = true
}

async function deleteTag(tag: TagInfo) {
  if (!confirm(`确定要删除标签"${tag.name}"吗？`)) return
  
  try {
    await tagApi.delete(tag.id)
    tags.value = tags.value.filter(t => t.id !== tag.id)
    alert('删除成功')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}

function closeModal() {
  showCreateModal.value = false
  showEditModal.value = false
  editingId.value = null
  form.name = ''
  form.slug = ''
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (showEditModal.value && editingId.value) {
      await tagApi.update(editingId.value, form)
      alert('更新成功')
    } else {
      await tagApi.create(form)
      alert('创建成功')
    }
    await loadTags()
    closeModal()
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.tag-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-md);
}

.tag-card {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.tag-card:hover {
  box-shadow: var(--shadow-md);
}

.tag-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tag-name {
  font-weight: 600;
  color: var(--text-primary);
}

.tag-slug {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.tag-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.btn-sm {
  padding: 4px 12px;
  border: none;
  border-radius: var(--border-radius-xs);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-edit {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.btn-edit:hover {
  background: #3b82f6;
  color: white;
}

.btn-delete {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.btn-delete:hover {
  background: #ef4444;
  color: white;
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-muted);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-xl);
  width: 100%;
  max-width: 400px;
  box-shadow: var(--shadow-lg);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.modal-header h3 {
  font-size: 1.2rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-muted);
}

.form-group {
  margin-bottom: var(--spacing-md);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-xs);
  font-weight: 500;
  color: var(--text-primary);
}

.form-group input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.modal-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
  margin-top: var(--spacing-lg);
}
</style>
