import { request } from './index'
import type { 
  CategoryInfo, 
  CategoryTree, 
  CreateCategoryRequest,
  UpdateCategoryRequest,
  ApiResponse
} from './types'

export const categoryApi = {
  getList(): Promise<ApiResponse<CategoryInfo[]>> {
    return request.get<CategoryInfo[]>('/categories')
  },

  getById(id: number): Promise<ApiResponse<CategoryInfo>> {
    return request.get<CategoryInfo>(`/categories/${id}`)
  },

  getTree(): Promise<ApiResponse<CategoryTree[]>> {
    return request.get<CategoryTree[]>('/categories/tree')
  },

  create(data: CreateCategoryRequest): Promise<ApiResponse<CategoryInfo>> {
    return request.post<CategoryInfo>('/categories', data)
  },

  update(id: number, data: UpdateCategoryRequest): Promise<ApiResponse<CategoryInfo>> {
    return request.put<CategoryInfo>(`/categories/${id}`, data)
  },

  delete(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/categories/${id}`)
  }
}
