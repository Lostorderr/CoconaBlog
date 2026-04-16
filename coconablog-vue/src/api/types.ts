export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageData<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface PageResponse<T> {
  code: number
  message: string
  data: PageData<T>
  timestamp: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  user: UserInfo
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  securityQuestion: string
  securityAnswer: string
}

export interface VerifySecurityRequest {
  username: string
  securityAnswer: string
}

export interface ResetPasswordRequest {
  username: string
  newPassword: string
}

export interface UserInfo {
  id: number
  username: string
  avatar: string | null
  email: string
  role: number
  status: number
  securityQuestion?: string | null
  createTime: string
  lastLogin: string | null
}

export enum ArticleStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  TRASH = 2
}

export interface Article {
  id: number
  title: string
  slug: string
  summary: string | null
  content: string
  coverImage: string | null
  userId: number
  categoryId: number | null
  status: ArticleStatus
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: boolean
  publishTime: string | null
  createTime: string
  updateTime: string
  author?: UserInfo
  category?: CategoryInfo | null
  tags?: TagInfo[]
}

export interface ArticleDetail extends Article {
  author: UserInfo
  category: CategoryInfo | null
  tags: TagInfo[]
}

export interface ArticleQuery {
  page?: number
  pageSize?: number
  categoryId?: number
  tagId?: number
  status?: ArticleStatus
  keyword?: string
  orderBy?: 'publish_time' | 'view_count' | 'like_count'
  order?: 'asc' | 'desc'
}

export interface CreateArticleRequest {
  title: string
  slug: string
  summary?: string
  content: string
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  status: ArticleStatus
  isTop?: boolean
}

export interface UpdateArticleRequest {
  title?: string
  slug?: string
  summary?: string
  content?: string
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  status?: ArticleStatus
  isTop?: boolean
}

export interface CategoryInfo {
  id: number
  name: string
  slug: string
  description: string | null
  parentId: number | null
  sort: number
  articleCount: number
  createTime: string
}

export interface CategoryTree extends CategoryInfo {
  children: CategoryTree[]
}

export interface CreateCategoryRequest {
  name: string
  slug: string
  description?: string
  parentId?: number
  sort?: number
}

export interface UpdateCategoryRequest {
  name?: string
  slug?: string
  description?: string
  parentId?: number
  sort?: number
}

export interface TagInfo {
  id: number
  name: string
  slug: string
  articleCount: number
  createTime: string
}

export interface TagQuery {
  keyword?: string
  page?: number
  pageSize?: number
}

export interface CreateTagRequest {
  name: string
  slug: string
}

export interface UpdateTagRequest {
  name?: string
  slug?: string
}

export enum CommentStatus {
  NORMAL = 0,
  PENDING = 1,
  DELETED = 2
}

export interface CommentInfo {
  id: number
  content: string
  userId: number
  articleId: number
  parentId: number | null
  replyToId: number | null
  likeCount: number
  status: CommentStatus
  createTime: string
  user: UserInfo
  replyTo?: UserInfo
  article?: { id: number; title: string }
  isLiked?: boolean
  children?: CommentInfo[]
}

export interface CommentQuery {
  page?: number
  pageSize?: number
  orderBy?: 'create_time' | 'like_count'
  order?: 'asc' | 'desc'
}

export interface CreateCommentRequest {
  content: string
  articleId: number
  parentId?: number
  replyToId?: number
}

export enum LikeTargetType {
  ARTICLE = 0,
  COMMENT = 1
}

export interface LikeCheckQuery {
  targetId: number
  targetType: LikeTargetType
}

export interface LikeCheckResponse {
  isLiked: boolean
}

export interface LikeRequest {
  targetId: number
  targetType: LikeTargetType
}

export interface LikeQuery {
  targetId: number
  targetType: LikeTargetType
}

export interface PublicConfig {
  siteName: string
  siteDescription: string
  siteKeywords: string
  footerText: string
  socialLinks: {
    github?: string
    twitter?: string
    email?: string
  }
}

export interface ConfigItem {
  id: number
  key: string
  value: string | null
  description: string | null
}

export interface UpdateConfigRequest {
  value: string
}
