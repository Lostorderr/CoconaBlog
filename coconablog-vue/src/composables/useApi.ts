import { ref, reactive, computed } from 'vue'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import { commentApi } from '@/api/comment'
import { authApi } from '@/api/auth'
import { likeApi } from '@/api/like'
import { configApi } from '@/api/config'
import type { 
  Article, 
  ArticleDetail, 
  ArticleQuery, 
  CategoryInfo, 
  TagInfo, 
  CommentInfo,
  UserInfo,
  LoginRequest,
  RegisterRequest,
  CreateArticleRequest,
  CreateCommentRequest,
  LikeRequest
} from '@/api/types'

export function useArticles() {
  const articles = ref<Article[]>([])
  const currentArticle = ref<ArticleDetail | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  const pagination = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
    totalPages: 0
  })

  async function fetchArticles(params?: ArticleQuery) {
    loading.value = true
    error.value = null
    try {
      const response = await articleApi.getList(params)
      articles.value = response.data.list
      pagination.page = response.data.page
      pagination.pageSize = response.data.pageSize
      pagination.total = response.data.total
      pagination.totalPages = response.data.totalPages
      return response.data
    } catch (e) {
      error.value = '获取文章列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function fetchArticleById(id: number) {
    loading.value = true
    error.value = null
    try {
      const response = await articleApi.getById(id)
      currentArticle.value = response.data
      return response.data
    } catch (e) {
      error.value = '获取文章详情失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function fetchArticleBySlug(slug: string) {
    loading.value = true
    error.value = null
    try {
      const response = await articleApi.getBySlug(slug)
      currentArticle.value = response.data
      return response.data
    } catch (e) {
      error.value = '获取文章详情失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function incrementView(id: number) {
    try {
      await articleApi.incrementView(id)
    } catch {
    }
  }

  return {
    articles,
    currentArticle,
    loading,
    error,
    pagination,
    fetchArticles,
    fetchArticleById,
    fetchArticleBySlug,
    incrementView
  }
}

export function useCategories() {
  const categories = ref<CategoryInfo[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getList()
      categories.value = response.data
      return response.data
    } catch (e) {
      error.value = '获取分类列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    categories,
    loading,
    error,
    fetchCategories
  }
}

export function useTags() {
  const tags = ref<TagInfo[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchTags() {
    loading.value = true
    error.value = null
    try {
      const response = await tagApi.getAll()
      tags.value = response.data
      return response.data
    } catch (e) {
      error.value = '获取标签列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    tags,
    loading,
    error,
    fetchTags
  }
}

export function useComments() {
  const comments = ref<CommentInfo[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  const pagination = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
    totalPages: 0
  })

  async function fetchComments(articleId: number, page = 1, pageSize = 10) {
    loading.value = true
    error.value = null
    try {
      const response = await commentApi.getByArticleId(articleId, { page, pageSize })
      comments.value = response.data.list
      pagination.page = response.data.page
      pagination.pageSize = response.data.pageSize
      pagination.total = response.data.total
      pagination.totalPages = response.data.totalPages
      return response.data
    } catch (e) {
      error.value = '获取评论失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function createComment(data: CreateCommentRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await commentApi.create(data)
      return response.data
    } catch (e) {
      error.value = '发表评论失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    comments,
    loading,
    error,
    pagination,
    fetchComments,
    createComment
  }
}

const _authUser = ref<UserInfo | null>(null)
const _authToken = ref<string | null>(localStorage.getItem('token'))
const _authLoading = ref(false)
const _authError = ref<string | null>(null)

;(function initAuthFromStorage() {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    try {
      _authUser.value = JSON.parse(storedUser)
    } catch {
      localStorage.removeItem('user')
    }
  }
})()

const _isLoggedIn = computed(() => !!_authToken.value && !!_authUser.value)
const _isAdmin = computed(() => _authUser.value?.role === 1)

export function useAuth() {
  const user = _authUser
  const token = _authToken
  const loading = _authLoading
  const error = _authError

  const isLoggedIn = _isLoggedIn
  const isAdmin = _isAdmin

  async function login(data: LoginRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await authApi.login(data)
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      return response.data
    } catch (e) {
      error.value = '登录失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function register(data: RegisterRequest) {
    loading.value = true
    error.value = null
    try {
      await authApi.register(data)
    } catch (e) {
      error.value = '注册失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } catch {
    } finally {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  async function fetchProfile() {
    if (!token.value) return
    loading.value = true
    error.value = null
    try {
      const response = await authApi.getProfile()
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
    } catch {
      token.value = null
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    } finally {
      loading.value = false
    }
  }

  function initFromStorage() {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        localStorage.removeItem('user')
      }
    }
  }

  return {
    user,
    token,
    loading,
    error,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchProfile,
    initFromStorage
  }
}

export function useLikes() {
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function checkLike(targetId: number, targetType: number) {
    loading.value = true
    error.value = null
    try {
      const response = await likeApi.check({ targetId, targetType })
      return response.data.isLiked
    } catch (e) {
      error.value = '检查点赞状态失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function like(data: LikeRequest) {
    loading.value = true
    error.value = null
    try {
      await likeApi.like(data)
    } catch (e) {
      error.value = '点赞失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function unlike(data: { targetId: number; targetType: number }) {
    loading.value = true
    error.value = null
    try {
      await likeApi.unlike(data)
    } catch (e) {
      error.value = '取消点赞失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    checkLike,
    like,
    unlike
  }
}

export function useConfig() {
  const config = reactive({
    siteName: 'Cocona Blog',
    siteDescription: '',
    siteKeywords: '',
    footerText: '',
    socialLinks: {
      github: '',
      twitter: '',
      email: ''
    }
  })
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchConfig() {
    loading.value = true
    error.value = null
    try {
      const response = await configApi.getPublic()
      Object.assign(config, response.data)
      return response.data
    } catch (e) {
      error.value = '获取配置失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    config,
    loading,
    error,
    fetchConfig
  }
}
