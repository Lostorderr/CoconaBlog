import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResponse, PageResponse } from './types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

const instance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          window.location.href = '/login'
          break
        case 403:
          console.error('没有权限访问该资源')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
    } else {
      console.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

async function get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response: AxiosResponse<ApiResponse<T>> = await instance.get(url, config)
  return response.data
}

async function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response: AxiosResponse<ApiResponse<T>> = await instance.post(url, data, config)
  return response.data
}

async function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response: AxiosResponse<ApiResponse<T>> = await instance.put(url, data, config)
  return response.data
}

async function del<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response: AxiosResponse<ApiResponse<T>> = await instance.delete(url, config)
  return response.data
}

async function getPage<T>(url: string, config?: AxiosRequestConfig): Promise<PageResponse<T>> {
  const response: AxiosResponse<PageResponse<T>> = await instance.get(url, config)
  return response.data
}

export const request = {
  get,
  post,
  put,
  delete: del,
  getPage
}

export { instance as axiosInstance, BASE_URL }
