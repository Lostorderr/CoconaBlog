<template>
  <footer class="footer">
    <div class="footer-content container">
      <div class="footer-section">
        <div class="footer-brand">
          <span class="brand-icon">🌸</span>
          <span class="brand-text">Cocona Blog</span>
        </div>
        <p class="footer-desc">
          一个个人博客，记录技术与生活的点滴 ✨
        </p>
      </div>

      <div class="footer-section">
        <h4 class="footer-title">快速链接</h4>
        <div class="footer-links">
          <router-link to="/" class="footer-link">首页</router-link>
          <router-link to="/articles" class="footer-link">文章</router-link>
          <router-link to="/about" class="footer-link">关于</router-link>
        </div>
      </div>

      <div class="footer-section">
        <h4 class="footer-title">联系方式</h4>
        <div class="footer-links">
          <a href="#" class="footer-link">📧 邮箱联系</a>
          <a href="#" class="footer-link">💻 GitHub</a>
          <a href="#" class="footer-link">🐦 Twitter</a>
        </div>
      </div>

      <div class="footer-section">
        <h4 class="footer-title">技术栈</h4>
        <div class="tech-stack">
          <span class="tech-tag">Vue 3</span>
          <span class="tech-tag">TypeScript</span>
          <span class="tech-tag">Vite</span>
          <span class="tech-tag">Spring Boot</span>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="container">
        <p class="copyright">
          © {{ currentYear }} Cocona Blog. Made with 💖 and ☕
        </p>
        <div class="footer-decoration">
          <span>✨</span>
          <span>🌸</span>
          <span>✨</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #2d1b30 0%, #4a2c4e 100%);
  color: white;
  margin-top: var(--spacing-2xl);
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-rainbow);
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-xl);
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1.5rem;
  font-weight: 700;
}

.brand-icon {
  font-size: 2rem;
  animation: float 3s ease-in-out infinite;
}

.brand-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-desc {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  font-size: 0.95rem;
}

.footer-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: var(--spacing-sm);
  color: var(--primary-light);
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.footer-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all var(--transition-fast);
  font-size: 0.95rem;
}

.footer-link:hover {
  color: var(--primary-light);
  transform: translateX(4px);
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tech-tag {
  padding: var(--spacing-xs) var(--spacing-md);
  background: rgba(255, 107, 157, 0.2);
  border: 1px solid var(--primary-light);
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--primary-light);
  transition: all var(--transition-normal);
}

.tech-tag:hover {
  background: var(--primary-color);
  color: white;
  transform: scale(1.05);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: var(--spacing-lg);
}

.footer-bottom .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.copyright {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

.footer-decoration {
  display: flex;
  gap: var(--spacing-md);
  font-size: 1.2rem;
}

.footer-decoration span {
  animation: float 2s ease-in-out infinite;
}

.footer-decoration span:nth-child(2) {
  animation-delay: 0.3s;
}

.footer-decoration span:nth-child(3) {
  animation-delay: 0.6s;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .footer-brand {
    justify-content: center;
  }

  .footer-bottom .container {
    flex-direction: column;
    gap: var(--spacing-md);
  }
}
</style>
