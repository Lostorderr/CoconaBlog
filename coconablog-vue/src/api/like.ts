import { request } from './index'
import type { 
  LikeCheckQuery, 
  LikeCheckResponse, 
  LikeRequest,
  LikeQuery,
  ApiResponse
} from './types'

export const likeApi = {
  check(params: LikeCheckQuery): Promise<ApiResponse<LikeCheckResponse>> {
    return request.get<LikeCheckResponse>('/likes/check', { params })
  },

  like(data: LikeRequest): Promise<ApiResponse<null>> {
    return request.post<null>('/likes', data)
  },

  unlike(data: LikeQuery): Promise<ApiResponse<null>> {
    return request.delete<null>('/likes', { data })
  }
}
