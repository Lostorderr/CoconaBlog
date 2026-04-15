<template>
  <div class="user-management">
    <div class="management-header">
      <h2>用户管理</h2>
    </div>

    <div class="coming-soon">
      <span class="icon">👥</span>
      <h3>功能开发中</h3>
      <p>用户管理功能正在开发中，敬请期待...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
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

.coming-soon {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-2xl);
  text-align: center;
  box-shadow: var(--shadow-sm);
}

.icon {
  font-size: 4rem;
  display: block;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.coming-soon h3 {
  font-size: 1.5rem;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.coming-soon p {
  color: var(--text-secondary);
}
</style>
