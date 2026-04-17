<template>
  <div class="comment-management">
    <div class="management-header">
      <h2>评论管理</h2>
    </div>

    <div class="filter-bar">
      <select v-model="statusFilter" class="filter-select">
        <option value="all">全部状态</option>
        <option value="normal">正常</option>
        <option value="pending">待审核</option>
        <option value="deleted">已删除</option>
      </select>
    </div>

    <div class="comment-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="comments.length === 0" class="empty">暂无评论</div>
      <div v-else class="comments-container">
        <div v-for="comment in filteredComments" :key="comment.id" class="comment-card">
          <div class="comment-header">
            <span class="comment-user">
              <span class="user-avatar">{{ comment.user?.username?.charAt(0) }}</span>
              {{ comment.user?.username || '匿名' }}
            </span>
            <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
          </div>
          <div class="comment-article">
            评论文章: {{ comment.article?.title || '未知文章' }}
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-footer">
            <span class="comment-stats">
              {{ comment.likeCount }} 赞
            </span>
            <div class="comment-actions">
              <button v-if="comment.status === 1" class="btn-sm btn-approve" @click="approveComment(comment)">
                通过
              </button>
              <button v-if="comment.status !== 2" class="btn-sm btn-delete" @click="deleteComment(comment)">
                删除
              </button>
              <button v-else class="btn-sm btn-restore" @click="restoreComment(comment)">
                恢复
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { commentApi } from '@/api/comment'
import type { CommentInfo } from '@/api/types'

const loading = ref(false)
const comments = ref<CommentInfo[]>([])
const statusFilter = ref('all')

const filteredComments = computed(() => {
  if (statusFilter.value === 'all') return comments.value
  const statusMap: Record<string, number> = {
    normal: 0,
    pending: 1,
    deleted: 2
  }
  return comments.value.filter(c => c.status === statusMap[statusFilter.value])
})

onMounted(async () => {
  await loadComments()
})

async function loadComments() {
  loading.value = true
  try {
    const response = await commentApi.getAll({ page: 1, pageSize: 100 })
    comments.value = response.data.list || []
  } finally {
    loading.value = false
  }
}

function formatDate(date: string): string {
  return new Date(date).toLocaleString('zh-CN')
}

async function approveComment(comment: CommentInfo) {
  try {
    await commentApi.updateStatus(comment.id, 0)
    comment.status = 0
    alert('已通过审核')
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  }
}

async function deleteComment(comment: CommentInfo) {
  if (!confirm('确定要删除这条评论吗？')) return
  
  try {
    await commentApi.delete(comment.id)
    comment.status = 2
    alert('已删除')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  }
}

async function restoreComment(comment: CommentInfo) {
  try {
    await commentApi.updateStatus(comment.id, 0)
    comment.status = 0
    alert('已恢复')
  } catch (e: any) {
    alert(e.response?.data?.message || '恢复失败')
  }
}
</script>

<style scoped>
.comment-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
}

.filter-select {
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  background: var(--bg-card);
}

.comments-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.comment-card {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-sm);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.comment-user {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-weight: 500;
  color: var(--text-primary);
}

.user-avatar {
  font-size: 1.2rem;
}

.comment-date {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.comment-article {
  font-size: 0.9rem;
  color: var(--primary-color);
  margin-bottom: var(--spacing-sm);
}

.comment-content {
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-sm);
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-stats {
  font-size: 0.9rem;
  color: var(--text-muted);
}

.comment-actions {
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

.btn-approve {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.btn-approve:hover {
  background: #22c55e;
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

.btn-restore {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.btn-restore:hover {
  background: #3b82f6;
  color: white;
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-muted);
}
</style>
