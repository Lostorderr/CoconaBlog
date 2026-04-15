import { request } from './index'
import type { 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest, 
  UserInfo,
  ApiResponse 
} from './types'

export const authApi = {
  login(data: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return request.post<LoginResponse>('/users/auth/login', data)
  },

  register(data: RegisterRequest): Promise<ApiResponse<null>> {
    return request.post<null>('/users/auth/register', data)
  },

  logout(): Promise<ApiResponse<null>> {
    return request.post<null>('/users/auth/logout')
  },

  getProfile(): Promise<ApiResponse<UserInfo>> {
    return request.get<UserInfo>('/users/auth/profile')
  },

  updateProfile(data: Partial<Pick<UserInfo, 'avatar'>>): Promise<ApiResponse<UserInfo>> {
    return request.put<UserInfo>('/users/auth/profile', data)
  },

  getUserById(id: number): Promise<ApiResponse<UserInfo>> {
    return request.get<UserInfo>(`/users/${id}`)
  }
}
