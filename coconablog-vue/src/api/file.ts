import { request } from './index'
import type { ApiResponse } from './types'

export const fileApi = {
  uploadImage(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<string>('/files/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
