import { request } from './index'
import type { 
  CommentInfo, 
  CommentQuery, 
  CreateCommentRequest,
  ApiResponse,
  PageResponse
} from './types'

export const commentApi = {
  getByArticleId(articleId: number, params?: CommentQuery): Promise<PageResponse<CommentInfo>> {
    return request.getPage<CommentInfo>(`/comments/article/${articleId}`, { params })
  },

  create(data: CreateCommentRequest): Promise<ApiResponse<CommentInfo>> {
    return request.post<CommentInfo>('/comments', data)
  },

  delete(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/comments/${id}`)
  },

  like(id: number): Promise<ApiResponse<null>> {
    return request.post<null>(`/comments/${id}/like`)
  },

  unlike(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/comments/${id}/like`)
  },

  getMyComments(params?: { page?: number; pageSize?: number }): Promise<PageResponse<CommentInfo>> {
    return request.getPage<CommentInfo>('/comments/my', { params })
  },

  getAll(params?: { page?: number; pageSize?: number }): Promise<PageResponse<CommentInfo>> {
    return request.getPage<CommentInfo>('/comments', { params })
  },

  updateStatus(id: number, status: number): Promise<ApiResponse<null>> {
    return request.put<null>(`/comments/${id}/status`, { status })
  }
}
