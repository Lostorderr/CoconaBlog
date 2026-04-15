import { request } from './index'
import type { 
  PublicConfig, 
  ConfigItem, 
  UpdateConfigRequest,
  ApiResponse
} from './types'

export const configApi = {
  getPublic(): Promise<ApiResponse<PublicConfig>> {
    return request.get<PublicConfig>('/configs/public')
  },

  getAll(): Promise<ApiResponse<ConfigItem[]>> {
    return request.get<ConfigItem[]>('/configs')
  },

  update(key: string, data: UpdateConfigRequest): Promise<ApiResponse<ConfigItem>> {
    return request.put<ConfigItem>(`/configs/${key}`, data)
  }
}
