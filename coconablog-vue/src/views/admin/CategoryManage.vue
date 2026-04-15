<template>
  <div class="category-management">
    <div class="management-header">
      <h2>分类管理</h2>
      <button class="btn btn-primary" @click="showCreateModal = true">
        <span>➕</span> 新建分类
      </button>
    </div>

    <div class="category-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="categories.length === 0" class="empty">暂无分类</div>
      <div v-else class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>标识</th>
              <th>描述</th>
              <th>文章数</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id">
              <td>{{ cat.id }}</td>
              <td>{{ cat.name }}</td>
              <td><code>{{ cat.slug }}</code></td>
              <td>{{ cat.description || '-' }}</td>
              <td>{{ cat.articleCount || 0 }}</td>
              <td>{{ cat.sort }}</td>
              <td class="actions">
                <button class="btn-sm btn-edit" @click="editCategory(cat)">编辑</button>
                <button class="btn-sm btn-delete" @click="deleteCategory(cat)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑分类' : '新建分类' }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>分类名称 *</label>
            <input v-model="form.name" type="text" required />
          </div>
          <div class="form-group">
            <label>URL标识 *</label>
            <input v-model="form.slug" type="text" required />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="form.description" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>排序</label>
            <input v-model.number="form.sort" type="number" />
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
import { categoryApi } from '@/api/category'
import type { CategoryInfo } from '@/api/types'

const loading = ref(false)
const submitting = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const categories = ref<CategoryInfo[]>([])
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  slug: '',
  description: '',
  sort: 0
})

onMounted(async () => {
  await loadCategories()
})

async function loadCategories() {
  loading.value = true
  try {
    const response = await categoryApi.getList()
    categories.value = response.data
  } finally {
    loading.value = false
  }
}

function editCategory(cat: CategoryInfo) {
  editingId.value = cat.id
  form.name = cat.name
  form.slug = cat.slug
  form.description = cat.description || ''
  form.sort = cat.sort
  showEditModal.value = true
}

async function deleteCategory(cat: CategoryInfo) {
  if (!confirm(`确定要删除分类"${cat.name}"吗？`)) return
  
  try {
    await categoryApi.delete(cat.id)
    categories.value = categories.value.filter(c => c.id !== cat.id)
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
  form.description = ''
  form.sort = 0
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (showEditModal.value && editingId.value) {
      await categoryApi.update(editingId.value, form)
      alert('更新成功')
    } else {
      await categoryApi.create(form)
      alert('创建成功')
    }
    await loadCategories()
    closeModal()
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.category-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-container {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.data-table th {
  background: var(--bg-hover);
  font-weight: 600;
  color: var(--text-primary);
}

.data-table td {
  color: var(--text-secondary);
}

.data-table code {
  background: var(--bg-hover);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.actions {
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
  max-width: 500px;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
}

.form-group input:focus,
.form-group textarea:focus {
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
