<template>
  <div class="about-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-title">
          <span class="title-icon">💖</span>
          <span>关于我</span>
        </h1>
        <p class="page-description">
          了解更多关于这个博客和博主的故事
        </p>
      </div>
    </div>

    <div class="container page-content">
      <div class="about-content">
        <section class="about-section card">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">🌸</span>
              <span>关于博客</span>
            </h2>
          </div>
          <div class="section-content">
            <p>
              欢迎来到 Cocona Blog！
              专注于分享技术文章、生活感悟和文化分享。
            </p>
            <p>
              博客采用现代化的技术栈构建，前端使用 Vue 3 + TypeScript + Vite，
              后端使用 Spring Boot，致力于提供流畅的阅读体验与视觉设计。
            </p>
          </div>
        </section>

        <section class="about-section card">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">👤</span>
              <span>关于博主</span>
            </h2>
          </div>
          <div class="author-profile">
            <div class="profile-avatar">🌸</div>
            <div class="profile-info">
              <h3 class="profile-name">Cocona</h3>
              <p class="profile-bio">
                一个热爱编程的开发者，喜欢探索新技术，
                分享学习心得，记录生活点滴。相信代码可以改变世界，
                动漫可以温暖人心 ✨
              </p>
            </div>
          </div>
        </section>

        <section class="about-section card">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">🎯</span>
              <span>博客特色</span>
            </h2>
          </div>
          <div class="features-list">
            <div class="feature-item">
              <div class="feature-icon">🎨</div>
              <div class="feature-content">
                <h4 class="feature-title">风格设计</h4>
                <p class="feature-desc">
                  可爱的UI界面
                </p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">💻</div>
              <div class="feature-content">
                <h4 class="feature-title">技术文章分享</h4>
                <p class="feature-desc">
                  前端、后端、设计等多领域技术文章，助你成长
                </p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📱</div>
              <div class="feature-content">
                <h4 class="feature-title">响应式设计</h4>
                <p class="feature-desc">
                  完美适配各种设备，随时随地享受阅读
                </p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🌙</div>
              <div class="feature-content">
                <h4 class="feature-title">持续更新</h4>
                <p class="feature-desc">
                  定期发布优质内容，总有新的发现等着你
                </p>
              </div>
            </div>
          </div>
        </section>

        <section class="about-section card">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">🛠️</span>
              <span>技术栈</span>
            </h2>
          </div>
          <div class="tech-stack">
            <div class="tech-category">
              <h4 class="category-title">前端技术</h4>
              <div class="tech-items">
                <span class="tech-tag">Vue 3</span>
                <span class="tech-tag">TypeScript</span>
                <span class="tech-tag">Vite</span>
                <span class="tech-tag">Vue Router</span>
                <span class="tech-tag">Pinia</span>
              </div>
            </div>
            <div class="tech-category">
              <h4 class="category-title">后端技术</h4>
              <div class="tech-items">
                <span class="tech-tag">Spring Boot</span>
                <span class="tech-tag">MyBatis</span>
                <span class="tech-tag">MySQL</span>
              </div>
            </div>
            <div class="tech-category">
              <h4 class="category-title">开发工具</h4>
              <div class="tech-items">
                <span class="tech-tag">VS Code</span>
                <span class="tech-tag">Git</span>
                <span class="tech-tag">npm</span>
              </div>
            </div>
          </div>
        </section>

        <section class="about-section card">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">📬</span>
              <span>联系方式</span>
            </h2>
          </div>
          <div class="contact-links">
            <a href="mailto:842622982@qq.com" class="contact-item">
              <span class="contact-icon">📧</span>
              <span class="contact-text">842622982@qq.com</span>
            </a>
            <a href="https://github.com/Lostorderr" target="_blank" class="contact-item">
              <span class="contact-icon">💻</span>
              <span class="contact-text">GitHub</span>
            </a>
            <a href="https://space.bilibili.com/113969638" target="_blank" class="contact-item">
              <span class="contact-icon">📺</span>
              <span class="contact-text">Bilibili</span>
            </a>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.about-page {
  min-height: 100vh;
}

.page-header {
  background: var(--gradient-secondary);
  color: white;
  padding: var(--spacing-2xl) var(--spacing-lg);
  text-align: center;
}

.page-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: var(--spacing-md);
}

.title-icon {
  font-size: 2.5rem;
}

.page-description {
  font-size: 1.1rem;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

.page-content {
  padding: var(--spacing-2xl) var(--spacing-lg);
}

.about-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.about-section {
  padding: var(--spacing-xl);
}

.section-header {
  margin-bottom: var(--spacing-lg);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.section-content {
  line-height: 1.8;
  color: var(--text-secondary);
}

.section-content p {
  margin-bottom: var(--spacing-md);
}

.section-content p:last-child {
  margin-bottom: 0;
}

.author-profile {
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  flex-shrink: 0;
  box-shadow: var(--shadow-md);
  animation: float 3s ease-in-out infinite;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.profile-bio {
  color: var(--text-secondary);
  line-height: 1.8;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.feature-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.feature-item:hover {
  background: var(--primary-color);
  color: white;
  transform: translateX(8px);
}

.feature-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
}

.feature-desc {
  font-size: 0.95rem;
  opacity: 0.9;
}

.tech-stack {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.tech-category {
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
}

.category-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.tech-items {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tech-tag {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--gradient-primary);
  color: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all var(--transition-normal);
}

.tech-tag:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-sm);
}

.contact-links {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: var(--spacing-md);
}

.contact-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  color: var(--text-primary);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.contact-item:hover {
  background: var(--gradient-primary);
  color: white;
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.contact-icon {
  font-size: 1.5rem;
}

.contact-text {
  font-weight: 500;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .author-profile {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
    font-size: 2.5rem;
  }

  .feature-item {
    flex-direction: column;
    text-align: center;
  }

  .contact-links {
    grid-template-columns: 1fr;
  }
}
</style>
