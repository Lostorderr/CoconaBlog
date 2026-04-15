export * from '../api/types'

export interface Article {
  id: number
  title: string
  slug: string
  summary: string | null
  content: string
  coverImage: string | null
  userId: number
  categoryId: number | null
  status: number
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: boolean
  publishTime: string | null
  createTime: string
  updateTime: string
  author?: UserInfo
  category?: CategoryInfo
  tags?: TagInfo[]
}

export interface UserInfo {
  id: number
  username: string
  avatar: string | null
  email: string
  role: number
  createTime: string
  lastLogin: string | null
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

export interface TagInfo {
  id: number
  name: string
  slug: string
  articleCount: number
  createTime: string
}

export interface CommentInfo {
  id: number
  content: string
  userId: number
  articleId: number
  parentId: number | null
  replyToId: number | null
  likeCount: number
  status: number
  createTime: string
  user: UserInfo
  replyTo?: UserInfo
  children?: CommentInfo[]
}

export interface SiteConfig {
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
