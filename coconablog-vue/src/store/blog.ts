import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import { commentApi } from '@/api/comment'
import { likeApi } from '@/api/like'
import type { 
  Article, 
  ArticleDetail, 
  ArticleQuery, 
  CategoryInfo, 
  TagInfo, 
  CommentInfo,
  PageData
} from '@/api/types'

export const useBlogStore = defineStore('blog', () => {
  const articles = ref<Article[]>([])
  const currentArticle = ref<ArticleDetail | null>(null)
  const categories = ref<CategoryInfo[]>([])
  const tags = ref<TagInfo[]>([])
  const comments = ref<CommentInfo[]>([])
  
  const loading = ref(false)
  const articleLoading = ref(false)
  
  const pagination = ref<PageData<Article>>({
    list: [],
    total: 0,
    page: 1,
    pageSize: 10,
    totalPages: 0
  })

  const totalArticles = computed(() => pagination.value.total)
  const totalViews = computed(() => 
    articles.value.reduce((sum, article) => sum + article.viewCount, 0)
  )
  const totalLikes = computed(() => 
    articles.value.reduce((sum, article) => sum + article.likeCount, 0)
  )
  const allTags = computed(() => tags.value)

  async function fetchArticles(params?: ArticleQuery) {
    loading.value = true
    try {
      const response = await articleApi.getList(params)
      pagination.value = response.data
      articles.value = response.data.list
      return response.data
    } finally {
      loading.value = false
    }
  }

  async function fetchArticleById(id: number) {
    articleLoading.value = true
    try {
      const response = await articleApi.getById(id)
      currentArticle.value = response.data
      return response.data
    } finally {
      articleLoading.value = false
    }
  }

  async function fetchArticleBySlug(slug: string) {
    articleLoading.value = true
    try {
      const response = await articleApi.getBySlug(slug)
      currentArticle.value = response.data
      return response.data
    } finally {
      articleLoading.value = false
    }
  }

  async function fetchCategories() {
    try {
      const response = await categoryApi.getList()
      categories.value = response.data
      return response.data
    } catch {
      return []
    }
  }

  async function fetchTags() {
    try {
      const response = await tagApi.getAll()
      tags.value = response.data
      return response.data
    } catch {
      return []
    }
  }

  async function fetchComments(articleId: number, page = 1, pageSize = 10) {
    try {
      const response = await commentApi.getByArticleId(articleId, { page, pageSize })
      comments.value = response.data.list
      return response.data
    } catch {
      return null
    }
  }

  async function viewArticle(id: number) {
    try {
      await articleApi.incrementView(id)
    } catch {
    }
  }

  async function likeArticle(id: number) {
    return articleApi.like(id)
  }

  async function unlikeArticle(id: number) {
    return articleApi.unlike(id)
  }

  async function checkArticleLiked(id: number) {
    try {
      const response = await likeApi.check({ targetId: id, targetType: 0 })
      return response.data.isLiked
    } catch {
      return false
    }
  }

  async function createComment(data: { content: string; articleId: number; parentId?: number; replyToId?: number }) {
    return commentApi.create(data)
  }

  async function deleteComment(id: number) {
    return commentApi.delete(id)
  }

  async function likeComment(id: number) {
    return commentApi.like(id)
  }

  async function unlikeComment(id: number) {
    return commentApi.unlike(id)
  }

  function getArticleById(id: number) {
    return articles.value.find(a => a.id === id) || null
  }

  function getArticlesByCategory(categoryId: number) {
    return articles.value.filter(a => a.categoryId === categoryId)
  }

  function getArticlesByTag(tagId: number) {
    return articles.value.filter(a => 
      a.tags?.some(t => t.id === tagId)
    )
  }

  return {
    articles,
    currentArticle,
    categories,
    tags,
    comments,
    loading,
    articleLoading,
    pagination,
    totalArticles,
    totalViews,
    totalLikes,
    allTags,
    fetchArticles,
    fetchArticleById,
    fetchArticleBySlug,
    fetchCategories,
    fetchTags,
    fetchComments,
    viewArticle,
    likeArticle,
    unlikeArticle,
    checkArticleLiked,
    createComment,
    deleteComment,
    likeComment,
    unlikeComment,
    getArticleById,
    getArticlesByCategory,
    getArticlesByTag
  }
})

export type { Article, ArticleDetail, CategoryInfo, TagInfo, CommentInfo }
