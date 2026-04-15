import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Article {
  id: number
  title: string
  summary: string
  content: string
  cover: string
  author: string
  category: string
  tags: string[]
  views: number
  likes: number
  createdAt: string
  updatedAt: string
}

export const useBlogStore = defineStore('blog', () => {
  const articles = ref<Article[]>([
    {
      id: 1,
      title: '欢迎来到Cocona博客',
      summary: '这是一个充满二次元风格的博客系统，让我们一起探索这个奇妙的世界吧！',
      content: `
# 欢迎来到Cocona博客 ✨

这是一个充满二次元风格的博客系统，让我们一起探索这个奇妙的世界吧！

## 特色功能 🌟

- 🎨 **二次元风格设计**：可爱的UI界面，让你感受到动漫的魅力
- 📝 **Markdown支持**：轻松编写格式丰富的文章
- 🏷️ **标签分类**：方便地管理和查找文章
- 💖 **互动功能**：点赞、评论、分享你的想法

## 技术栈 💻

- **前端**：Vue 3 + TypeScript + Vite
- **后端**：Spring Boot
- **样式**：自定义二次元风格CSS

希望你能喜欢这个博客系统！
      `,
      cover: 'https://picsum.photos/seed/anime1/800/400',
      author: 'Cocona',
      category: '公告',
      tags: ['公告', '欢迎'],
      views: 1234,
      likes: 567,
      createdAt: '2026-04-15',
      updatedAt: '2026-04-15'
    },
    {
      id: 2,
      title: 'Vue 3 组合式API入门指南',
      summary: '深入了解Vue 3的组合式API，掌握现代Vue开发的精髓。',
      content: `
# Vue 3 组合式API入门指南 🚀

Vue 3引入了组合式API（Composition API），这是一种全新的组织组件逻辑的方式。

## 什么是组合式API？ 🤔

组合式API是一组函数，允许你在setup()函数中组织组件逻辑。

## 基础用法 📚

\`\`\`typescript
import { ref, computed, onMounted } from 'vue'

export default {
  setup() {
    const count = ref(0)
    const doubled = computed(() => count.value * 2)
    
    onMounted(() => {
      console.log('组件已挂载！')
    })
    
    return { count, doubled }
  }
}
\`\`\`

## 优势 ✨

1. **更好的逻辑复用**
2. **更灵活的代码组织**
3. **更好的TypeScript支持**
4. **更小的打包体积**

开始你的Vue 3之旅吧！
      `,
      cover: 'https://picsum.photos/seed/vue3/800/400',
      author: 'Cocona',
      category: '技术',
      tags: ['Vue', '前端', 'JavaScript'],
      views: 2345,
      likes: 890,
      createdAt: '2026-04-14',
      updatedAt: '2026-04-14'
    },
    {
      id: 3,
      title: '二次元文化与现代设计',
      summary: '探索二次元文化如何影响现代UI/UX设计理念。',
      content: `
# 二次元文化与现代设计 🎨

二次元文化已经深深影响了现代设计领域，让我们一起探索这个有趣的话题。

## 设计元素 🌈

### 色彩运用
- 柔和的渐变色
- 鲜艳的对比色
- 梦幻般的配色方案

### 图形设计
- 可爱的卡通形象
- 简洁的线条
- 圆润的边角

## 动画效果 ✨

- 平滑的过渡动画
- 弹性效果
- 粒子特效

## 字体选择 📝

- 圆润的无衬线字体
- 可爱的手写风格
- 清晰易读

二次元风格让设计更加生动有趣！
      `,
      cover: 'https://picsum.photos/seed/design/800/400',
      author: 'Cocona',
      category: '设计',
      tags: ['设计', '二次元', 'UI'],
      views: 1890,
      likes: 678,
      createdAt: '2026-04-13',
      updatedAt: '2026-04-13'
    }
  ])

  const currentArticle = ref<Article | null>(null)
  const loading = ref(false)

  const totalArticles = computed(() => articles.value.length)
  const totalViews = computed(() => 
    articles.value.reduce((sum, article) => sum + article.views, 0)
  )
  const totalLikes = computed(() => 
    articles.value.reduce((sum, article) => sum + article.likes, 0)
  )

  const categories = computed(() => {
    const cats = new Set(articles.value.map(a => a.category))
    return Array.from(cats)
  })

  const allTags = computed(() => {
    const tags = new Set<string>()
    articles.value.forEach(a => a.tags.forEach(t => tags.add(t)))
    return Array.from(tags)
  })

  function getArticleById(id: number) {
    return articles.value.find(a => a.id === id) || null
  }

  function getArticlesByCategory(category: string) {
    return articles.value.filter(a => a.category === category)
  }

  function getArticlesByTag(tag: string) {
    return articles.value.filter(a => a.tags.includes(tag))
  }

  function likeArticle(id: number) {
    const article = getArticleById(id)
    if (article) {
      article.likes++
    }
  }

  function viewArticle(id: number) {
    const article = getArticleById(id)
    if (article) {
      article.views++
    }
  }

  return {
    articles,
    currentArticle,
    loading,
    totalArticles,
    totalViews,
    totalLikes,
    categories,
    allTags,
    getArticleById,
    getArticlesByCategory,
    getArticlesByTag,
    likeArticle,
    viewArticle
  }
})
