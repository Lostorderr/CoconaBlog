import { request } from './index'
import type { 
  Article, 
  ArticleDetail, 
  ArticleQuery, 
  CreateArticleRequest, 
  UpdateArticleRequest,
  ApiResponse,
  PageResponse
} from './types'

export const articleApi = {
  getList(params?: ArticleQuery): Promise<PageResponse<Article>> {
    return request.getPage<Article>('/articles', { params })
  },

  getById(id: number): Promise<ApiResponse<ArticleDetail>> {
    return request.get<ArticleDetail>(`/articles/${id}`)
  },

  getBySlug(slug: string): Promise<ApiResponse<ArticleDetail>> {
    return request.get<ArticleDetail>(`/articles/slug/${slug}`)
  },

  create(data: CreateArticleRequest): Promise<ApiResponse<Article>> {
    return request.post<Article>('/articles', data)
  },

  update(id: number, data: UpdateArticleRequest): Promise<ApiResponse<Article>> {
    return request.put<Article>(`/articles/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/articles/${id}`)
  },

  incrementView(id: number): Promise<ApiResponse<null>> {
    return request.post<null>(`/articles/${id}/view`)
  },

  like(id: number): Promise<ApiResponse<null>> {
    return request.post<null>(`/articles/${id}/like`)
  },

  unlike(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/articles/${id}/like`)
  },

  search(params: { keyword: string; page?: number; pageSize?: number }): Promise<PageResponse<Article>> {
    return request.getPage<Article>('/articles/search', { params })
  }
}
