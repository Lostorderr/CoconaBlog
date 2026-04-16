import { request } from './index'
import type { 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest,
  VerifySecurityRequest,
  ResetPasswordRequest,
  UserInfo,
  ApiResponse,
  PageResponse
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
    return request.get<UserInfo>('/users/profile')
  },

  updateProfile(data: Partial<UserInfo>): Promise<ApiResponse<UserInfo>> {
    return request.put<UserInfo>('/users/profile', data)
  },

  getUserById(id: number): Promise<ApiResponse<UserInfo>> {
    return request.get<UserInfo>(`/users/${id}`)
  },

  getAllUsers(params?: { page?: number; pageSize?: number }): Promise<PageResponse<UserInfo>> {
    return request.getPage<UserInfo>('/users', { params })
  },

  updateUserStatus(id: number, status: number): Promise<ApiResponse<null>> {
    return request.put<null>(`/users/${id}/status`, { status })
  },

  updateUserRole(id: number, role: number): Promise<ApiResponse<null>> {
    return request.put<null>(`/users/${id}/role`, { role })
  },

  deleteUser(id: number): Promise<ApiResponse<null>> {
    return request.delete<null>(`/users/${id}`)
  },

  getSecurityQuestion(username: string): Promise<ApiResponse<UserInfo>> {
    return request.get<UserInfo>('/users/auth/forgot-password/question', { params: { username } })
  },

  verifySecurity(data: VerifySecurityRequest): Promise<ApiResponse<boolean>> {
    return request.post<boolean>('/users/auth/forgot-password/verify', data)
  },

  resetPassword(data: ResetPasswordRequest): Promise<ApiResponse<null>> {
    return request.post<null>('/users/auth/forgot-password/reset', data)
  }
}
