<template>
  <div class="user-management">
    <div class="management-header">
      <h2>用户管理</h2>
      <div class="header-stats">
        <span class="stat-badge">共 {{ users.length }} 个用户</span>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <span class="loading-spinner"></span>
      <p>加载中...</p>
    </div>

    <div v-else-if="users.length === 0" class="empty-state">
      <span class="empty-icon"></span>
      <p>暂无用户数据</p>
    </div>

    <div v-else class="users-table-wrapper">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>
              <div class="user-cell">
                <div class="user-avatar-small">{{ user.username.charAt(0).toUpperCase() }}</div>
                <span>{{ user.username }}</span>
              </div>
            </td>
            <td>{{ user.email }}</td>
            <td>
              <span class="role-badge" :class="getRoleClass(user.role)">
                {{ getRoleName(user.role) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="user.status === 0 ? 'active' : 'disabled'">
                {{ user.status === 0 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createTime) }}</td>
            <td>
              <div class="action-buttons">
                <select
                  v-if="user.id !== currentUserId"
                  class="role-select"
                  :value="user.role"
                  @change="handleChangeRole(user, Number(($event.target as HTMLSelectElement).value))"
                  :disabled="operating"
                >
                  <option :value="0">设为普通用户</option>
                  <option :value="2">设为授权用户</option>
                  <option :value="1">设为管理员</option>
                </select>
                <span v-else class="self-badge">当前账号</span>
                <button
                  v-if="user.status === 0 && user.id !== currentUserId"
                  class="action-btn disable-btn"
                  @click="handleToggleStatus(user)"
                  :disabled="operating"
                >
                  禁用
                </button>
                <button
                  v-if="user.status !== 0 && user.id !== currentUserId"
                  class="action-btn enable-btn"
                  @click="handleToggleStatus(user)"
                  :disabled="operating"
                >
                  启用
                </button>
                <button
                  v-if="user.id !== currentUserId"
                  class="action-btn delete-btn"
                  @click="handleDeleteUser(user)"
                  :disabled="operating"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { authApi } from '@/api/auth'
import { useAuth } from '@/composables/useApi'
import type { UserInfo } from '@/api/types'

const { user: currentUser } = useAuth()
const currentUserId = computed(() => currentUser.value?.id)

const users = ref<UserInfo[]>([])
const loading = ref(false)
const operating = ref(false)

onMounted(async () => {
  await loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const response = await authApi.getAllUsers({ page: 1, pageSize: 100 })
    users.value = response.data.list || []
  } catch {
    users.value = []
  } finally {
    loading.value = false
  }
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('zh-CN')
}

function getRoleName(role: number | undefined): string {
  if (role === 1) return '管理员'
  if (role === 2) return '授权用户'
  return '普通用户'
}

function getRoleClass(role: number | undefined): string {
  if (role === 1) return 'admin'
  if (role === 2) return 'author'
  return 'user'
}

async function handleChangeRole(user: UserInfo, newRole: number) {
  const action = getRoleName(newRole)
  if (!confirm(`确定要将用户 "${user.username}" ${action}吗？`)) return

  operating.value = true
  try {
    await authApi.updateUserRole(user.id, newRole)
    user.role = newRole
    alert(`已${action}`)
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    operating.value = false
  }
}

async function handleToggleStatus(user: UserInfo) {
  const newStatus = user.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '启用' : '禁用'
  if (!confirm(`确定要${action}用户 "${user.username}" 吗？`)) return

  operating.value = true
  try {
    await authApi.updateUserStatus(user.id, newStatus)
    user.status = newStatus
    alert(`${action}成功`)
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    operating.value = false
  }
}

async function handleDeleteUser(user: UserInfo) {
  if (!confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`)) return

  operating.value = true
  try {
    await authApi.deleteUser(user.id)
    users.value = users.value.filter(u => u.id !== user.id)
    alert('删除成功')
  } catch (e: any) {
    alert(e.response?.data?.message || '删除失败')
  } finally {
    operating.value = false
  }
}
</script>

<style scoped>
.user-management {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-stats {
  display: flex;
  gap: var(--spacing-md);
}

.stat-badge {
  padding: var(--spacing-xs) var(--spacing-md);
  background: rgba(255, 107, 157, 0.1);
  color: var(--primary-color);
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: var(--spacing-2xl);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
}

.loading-spinner {
  font-size: 3rem;
  animation: spin 1s linear infinite;
  display: block;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 4rem;
  display: block;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.users-table-wrapper {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-sm);
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th {
  padding: var(--spacing-md);
  text-align: left;
  font-weight: 600;
  color: var(--text-primary);
  background: var(--bg-hover);
  border-bottom: 2px solid var(--border-color);
  font-size: 0.9rem;
  white-space: nowrap;
}

.users-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.users-table tr:hover td {
  background: var(--bg-hover);
}

.user-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.user-avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 600;
  flex-shrink: 0;
}

.role-badge {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.role-badge.admin {
  background: rgba(255, 107, 157, 0.1);
  color: var(--primary-color);
}

.role-badge.user {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.role-badge.author {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.status-badge {
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.status-badge.disabled {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.action-btn {
  padding: 4px 10px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.8rem;
  cursor: pointer;
  background: var(--bg-card);
  color: var(--text-secondary);
  transition: all var(--transition-normal);
  white-space: nowrap;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-btn.toggle-role:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
}

.action-btn.disable-btn:hover:not(:disabled) {
  border-color: #eab308;
  color: #eab308;
  background: rgba(234, 179, 8, 0.05);
}

.action-btn.enable-btn:hover:not(:disabled) {
  border-color: #22c55e;
  color: #22c55e;
  background: rgba(34, 197, 94, 0.05);
}

.action-btn.delete-btn:hover:not(:disabled) {
  border-color: #ef4444;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.05);
}

.role-select {
  padding: 4px 8px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.8rem;
  cursor: pointer;
  background: var(--bg-card);
  color: var(--text-secondary);
  transition: all var(--transition-normal);
}

.role-select:focus {
  outline: none;
  border-color: var(--primary-color);
}

.self-badge {
  padding: 4px 10px;
  font-size: 0.8rem;
  color: var(--text-muted);
  font-style: italic;
}

@media (max-width: 768px) {
  .users-table th,
  .users-table td {
    padding: var(--spacing-sm);
    font-size: 0.8rem;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
