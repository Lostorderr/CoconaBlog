import { request } from './index'
import type { 
  TagInfo, 
  TagQuery, 
  CreateTagRequest,
  UpdateTagRequest,
  ApiResponse,
  PageResponse
} from './types'

export const tagApi = {
  getList(params?: TagQuery): Promise<PageResponse<TagInfo>> {
    return request.getPage<TagInfo>('/tags', { params })
  },

  getAll(): Promise<ApiResponse<TagInfo[]>> {
    return request.get<TagInfo[]>('/tags', { params: { pageSize: 1000 } })
  },

  getById(id: number): Promise<ApiResponse<TagInfo>> {
    return request.get<TagInfo>(`/tags/${id}`)
  },

  create(data: CreateTagRequest): Promise<ApiResponse<TagInfo>> {
    return request.post<TagInfo>('/tags', data)
  },

  update(id: number, data: UpdateTagRequest): Promise<ApiResponse<TagInfo>> {
    return request.put<TagInfo>(`/tags/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/tags/${id}`)
  }
}
