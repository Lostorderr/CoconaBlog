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

export interface Category {
  name: string
  count: number
}

export interface Tag {
  name: string
  count: number
}

export interface UserInfo {
  id: number
  username: string
  avatar: string
  email: string
  bio: string
}
