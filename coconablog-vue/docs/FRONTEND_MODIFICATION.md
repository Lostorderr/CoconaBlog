# CoconaBlog 前端修改文档

## 一、数据库结构分析

根据 `coconablog.sql` 数据库设计，系统包含以下核心表：

### 1. 数据表概览

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user` | 用户表 | id, username, password, email, avatar, role, status |
| `article` | 文章表 | id, title, slug, summary, content, cover_image, user_id, category_id, status, view_count, like_count, comment_count, is_top, publish_time |
| `category` | 分类表 | id, name, slug, description, parent_id, sort |
| `tag` | 标签表 | id, name, slug |
| `article_tag` | 文章-标签关联表 | article_id, tag_id |
| `comment` | 评论表 | id, content, user_id, article_id, parent_id, reply_to_id, like_count, status |
| `like` | 点赞表 | id, user_id, target_id, target_type |
| `config` | 系统配置表 | id, key, value, description |

---

## 二、数据模型修改

### 2.1 现有前端模型 vs 数据库模型对比

#### Article 模型

**现有前端模型 (`src/types/index.ts`):**
```typescript
export interface Article {
  id: number
  title: string
  summary: string
  content: string
  cover: string          // 需改为 cover_image
  author: string         // 需改为 user_id + 关联用户信息
  category: string       // 需改为 category_id + 关联分类信息
  tags: string[]         // 需改为关联查询
  views: number          // 需改为 view_count
  likes: number          // 需改为 like_count
  createdAt: string      // 需改为 create_time
  updatedAt: string      // 需改为 update_time
}
```

**数据库模型映射:**
```typescript
export interface Article {
  id: number
  title: string
  slug: string                    // 新增: URL友好标识
  summary: string | null
  content: string
  coverImage: string | null       // cover_image
  userId: number                  // user_id
  categoryId: number | null       // category_id
  status: ArticleStatus           // 新增: 文章状态
  viewCount: number               // view_count
  likeCount: number               // like_count
  commentCount: number            // 新增: comment_count
  isTop: boolean                  // 新增: is_top
  publishTime: string | null      // 新增: publish_time
  createTime: string              // create_time
  updateTime: string              // update_time
  // 关联数据
  author?: UserInfo               // 关联用户信息
  category?: CategoryInfo         // 关联分类信息
  tags?: TagInfo[]                // 关联标签列表
}
```

#### 新增模型定义

```typescript
// 文章状态枚举
export enum ArticleStatus {
  DRAFT = 0,       // 草稿
  PUBLISHED = 1,   // 已发布
  TRASH = 2        // 回收站
}

// 用户角色枚举
export enum UserRole {
  USER = 0,        // 普通用户
  ADMIN = 1        // 管理员
}

// 用户状态枚举
export enum UserStatus {
  NORMAL = 0,      // 正常
  DISABLED = 1     // 禁用
}

// 点赞目标类型枚举
export enum LikeTargetType {
  ARTICLE = 0,     // 文章
  COMMENT = 1      // 评论
}

// 评论状态枚举
export enum CommentStatus {
  NORMAL = 0,      // 正常
  PENDING = 1,     // 待审核
  DELETED = 2      // 已删除
}
```

---

## 三、API 接口设计

### 3.1 基础配置

**Base URL:** `/api/v1`

**通用响应格式:**
```typescript
interface ApiResponse<T> {
  code: number        // 状态码: 200成功, 400参数错误, 401未授权, 403禁止访问, 404未找到, 500服务器错误
  message: string     // 提示信息
  data: T            // 响应数据
  timestamp: number   // 时间戳
}

interface PageResponse<T> {
  code: number
  message: string
  data: {
    list: T[]
    total: number
    page: number
    pageSize: number
    totalPages: number
  }
  timestamp: number
}
```

### 3.2 接口列表

---

#### 用户模块 `/api/v1/users`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | `/auth/login` | 用户登录 | 否 |
| POST | `/auth/register` | 用户注册 | 否 |
| POST | `/auth/logout` | 用户登出 | 是 |
| GET | `/auth/profile` | 获取当前用户信息 | 是 |
| PUT | `/auth/profile` | 更新用户信息 | 是 |
| GET | `/{id}` | 获取用户公开信息 | 否 |

**接口详情:**

```typescript
// POST /api/v1/users/auth/login
// 请求体
interface LoginRequest {
  username: string
  password: string
}
// 响应体
interface LoginResponse {
  token: string
  user: UserInfo
}

// POST /api/v1/users/auth/register
// 请求体
interface RegisterRequest {
  username: string
  password: string
  email: string
}

// GET /api/v1/users/{id}
// 响应体
interface UserInfo {
  id: number
  username: string
  avatar: string | null
  email: string
  role: UserRole
  createTime: string
  lastLogin: string | null
}
```

---

#### 文章模块 `/api/v1/articles`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/` | 获取文章列表(分页) | 否 |
| GET | `/{id}` | 获取文章详情 | 否 |
| GET | `/slug/{slug}` | 通过slug获取文章 | 否 |
| POST | `/` | 创建文章 | 是(管理员) |
| PUT | `/{id}` | 更新文章 | 是(管理员) |
| DELETE | `/{id}` | 删除文章 | 是(管理员) |
| POST | `/{id}/view` | 增加浏览次数 | 否 |
| POST | `/{id}/like` | 点赞文章 | 是 |
| DELETE | `/{id}/like` | 取消点赞 | 是 |
| GET | `/search` | 搜索文章 | 否 |

**接口详情:**

```typescript
// GET /api/v1/articles
// 查询参数
interface ArticleQuery {
  page?: number           // 页码, 默认1
  pageSize?: number       // 每页数量, 默认10
  categoryId?: number     // 分类ID筛选
  tagId?: number          // 标签ID筛选
  status?: ArticleStatus  // 状态筛选(管理员可用)
  keyword?: string        // 关键词搜索
  orderBy?: 'publish_time' | 'view_count' | 'like_count'  // 排序字段
  order?: 'asc' | 'desc'  // 排序方式, 默认desc
}

// GET /api/v1/articles/{id}
// 响应体
interface ArticleDetail extends Article {
  author: UserInfo
  category: CategoryInfo | null
  tags: TagInfo[]
}

// POST /api/v1/articles
// 请求体
interface CreateArticleRequest {
  title: string
  slug: string
  summary?: string
  content: string
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  status: ArticleStatus
  isTop?: boolean
}

// PUT /api/v1/articles/{id}
// 请求体
interface UpdateArticleRequest {
  title?: string
  slug?: string
  summary?: string
  content?: string
  coverImage?: string
  categoryId?: number
  tagIds?: number[]
  status?: ArticleStatus
  isTop?: boolean
}
```

---

#### 分类模块 `/api/v1/categories`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/` | 获取分类列表 | 否 |
| GET | `/{id}` | 获取分类详情 | 否 |
| GET | `/tree` | 获取分类树 | 否 |
| POST | `/` | 创建分类 | 是(管理员) |
| PUT | `/{id}` | 更新分类 | 是(管理员) |
| DELETE | `/{id}` | 删除分类 | 是(管理员) |

**接口详情:**

```typescript
// GET /api/v1/categories
// 响应体
interface CategoryInfo {
  id: number
  name: string
  slug: string
  description: string | null
  parentId: number | null
  sort: number
  articleCount: number    // 文章数量(冗余字段)
  createTime: string
}

// GET /api/v1/categories/tree
// 响应体
interface CategoryTree extends CategoryInfo {
  children: CategoryTree[]
}

// POST /api/v1/categories
// 请求体
interface CreateCategoryRequest {
  name: string
  slug: string
  description?: string
  parentId?: number
  sort?: number
}
```

---

#### 标签模块 `/api/v1/tags`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/` | 获取标签列表 | 否 |
| GET | `/{id}` | 获取标签详情 | 否 |
| POST | `/` | 创建标签 | 是(管理员) |
| PUT | `/{id}` | 更新标签 | 是(管理员) |
| DELETE | `/{id}` | 删除标签 | 是(管理员) |

**接口详情:**

```typescript
// GET /api/v1/tags
// 查询参数
interface TagQuery {
  keyword?: string
  page?: number
  pageSize?: number
}

// 响应体
interface TagInfo {
  id: number
  name: string
  slug: string
  articleCount: number    // 文章数量
  createTime: string
}
```

---

#### 评论模块 `/api/v1/comments`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/` | 获取评论列表 | 否 |
| GET | `/article/{articleId}` | 获取文章评论 | 否 |
| POST | `/` | 发表评论 | 是 |
| DELETE | `/{id}` | 删除评论 | 是(本人/管理员) |
| POST | `/{id}/like` | 点赞评论 | 是 |
| DELETE | `/{id}/like` | 取消点赞 | 是 |

**接口详情:**

```typescript
// GET /api/v1/comments/article/{articleId}
// 查询参数
interface CommentQuery {
  page?: number
  pageSize?: number
  orderBy?: 'create_time' | 'like_count'
  order?: 'asc' | 'desc'
}

// 响应体
interface CommentInfo {
  id: number
  content: string
  userId: number
  articleId: number
  parentId: number | null
  replyToId: number | null
  likeCount: number
  status: CommentStatus
  createTime: string
  // 关联数据
  user: UserInfo
  replyTo?: UserInfo    // 回复目标用户
  children?: CommentInfo[]  // 子评论
}

// POST /api/v1/comments
// 请求体
interface CreateCommentRequest {
  content: string
  articleId: number
  parentId?: number
  replyToId?: number
}
```

---

#### 点赞模块 `/api/v1/likes`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/check` | 检查是否已点赞 | 是 |
| POST | `/` | 点赞 | 是 |
| DELETE | `/` | 取消点赞 | 是 |

**接口详情:**

```typescript
// GET /api/v1/likes/check
// 查询参数
interface LikeCheckQuery {
  targetId: number
  targetType: LikeTargetType
}
// 响应体
interface LikeCheckResponse {
  isLiked: boolean
}

// POST /api/v1/likes
// 请求体
interface LikeRequest {
  targetId: number
  targetType: LikeTargetType
}
```

---

#### 配置模块 `/api/v1/configs`

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | `/public` | 获取公开配置 | 否 |
| GET | `/` | 获取所有配置 | 是(管理员) |
| PUT | `/{key}` | 更新配置 | 是(管理员) |

**接口详情:**

```typescript
// GET /api/v1/configs/public
// 响应体
interface PublicConfig {
  siteName: string
  siteDescription: string
  siteKeywords: string
  footerText: string
  socialLinks: {
    github?: string
    twitter?: string
    email?: string
  }
}
```

---

## 四、前端文件修改清单

### 4.1 需要新增的文件

```
src/
├── api/
│   ├── index.ts          # API基础配置(axios实例、拦截器)
│   ├── types.ts          # API响应类型定义
│   ├── auth.ts           # 认证相关API
│   ├── article.ts        # 文章相关API
│   ├── category.ts       # 分类相关API
│   ├── tag.ts            # 标签相关API
│   ├── comment.ts        # 评论相关API
│   ├── like.ts           # 点赞相关API
│   └── config.ts         # 配置相关API
├── composables/
│   ├── useAuth.ts        # 认证组合式函数
│   └── usePagination.ts  # 分页组合式函数
├── views/
│   ├── Login.vue         # 登录页面(新增)
│   ├── Register.vue      # 注册页面(新增)
│   └── Profile.vue       # 个人中心(新增)
└── components/
    ├── Comment.vue       # 评论组件(新增)
    ├── CommentList.vue   # 评论列表组件(新增)
    └── Pagination.vue    # 分页组件(新增)
```

### 4.2 需要修改的文件

| 文件路径 | 修改内容 |
|----------|----------|
| `src/types/index.ts` | 更新所有类型定义以匹配数据库模型 |
| `src/store/blog.ts` | 重构为API调用模式,移除mock数据 |
| `src/store/index.ts` | 添加auth store |
| `src/router/index.ts` | 添加登录、注册、个人中心路由;添加路由守卫 |
| `src/views/Home.vue` | 使用API获取最新文章 |
| `src/views/Articles.vue` | 使用API获取文章列表,添加分页 |
| `src/views/ArticleDetail.vue` | 使用API获取文章详情,添加评论功能 |
| `src/components/ArticleCard.vue` | 更新字段映射 |
| `src/components/Navbar.vue` | 添加登录/用户菜单 |
| `src/components/Sidebar.vue` | 使用API获取统计数据 |
| `src/App.vue` | 添加全局加载状态 |

---

## 五、实施步骤建议

### 阶段一: 基础设施搭建
1. 创建API服务层 (`src/api/`)
2. 更新类型定义 (`src/types/index.ts`)
3. 配置axios实例和拦截器

### 阶段二: 状态管理重构
1. 创建auth store (用户认证状态)
2. 重构blog store (使用API调用)
3. 添加全局loading状态

### 阶段三: 页面组件更新
1. 更新首页文章展示
2. 更新文章列表页(分页、筛选)
3. 更新文章详情页(评论、点赞)

### 阶段四: 用户功能实现
1. 实现登录/注册页面
2. 实现个人中心
3. 添加路由守卫

### 阶段五: 优化完善
1. 添加评论功能
2. 完善点赞功能
3. 添加错误处理和提示

---

## 六、注意事项

1. **字段命名转换**: 数据库使用snake_case, 前端使用camelCase, 需要在API层做转换
2. **时间格式**: 数据库返回的时间需要格式化显示
3. **分页处理**: 所有列表接口都需要支持分页
4. **权限控制**: 管理员功能需要在前端做权限校验
5. **错误处理**: 统一处理API错误,显示友好提示
6. **缓存策略**: 合理使用浏览器缓存,减少重复请求
