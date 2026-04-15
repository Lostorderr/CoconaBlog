# CoconaBlog 后端 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api/v1`
- **认证方式**: JWT Bearer Token
- **Content-Type**: `application/json`

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1713123456789
}
```

### 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  },
  "timestamp": 1713123456789
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 用户模块 `/api/v1/users`

### 1.1 用户登录

**POST** `/auth/login`

**请求体:**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "cocona",
      "avatar": "https://example.com/avatar.png",
      "email": "cocona@example.com",
      "role": 0,
      "createTime": "2024-01-01T00:00:00",
      "lastLogin": "2024-04-15T12:00:00"
    }
  }
}
```

### 1.2 用户注册

**POST** `/auth/register`

**请求体:**
```json
{
  "username": "string",
  "password": "string",
  "email": "string"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success"
}
```

### 1.3 用户登出

**POST** `/auth/logout`

**Headers:** `Authorization: Bearer {token}`

**响应:**
```json
{
  "code": 200,
  "message": "success"
}
```

### 1.4 获取当前用户信息

**GET** `/auth/profile`

**Headers:** `Authorization: Bearer {token}`

**响应:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "cocona",
    "avatar": "https://example.com/avatar.png",
    "email": "cocona@example.com",
    "role": 0,
    "createTime": "2024-01-01T00:00:00",
    "lastLogin": "2024-04-15T12:00:00"
  }
}
```

### 1.5 更新用户信息

**PUT** `/auth/profile`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "avatar": "https://example.com/new-avatar.png"
}
```

### 1.6 获取用户公开信息

**GET** `/{id}`

**响应:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "cocona",
    "avatar": "https://example.com/avatar.png",
    "email": "cocona@example.com",
    "role": 0,
    "createTime": "2024-01-01T00:00:00",
    "lastLogin": "2024-04-15T12:00:00"
  }
}
```

---

## 2. 文章模块 `/api/v1/articles`

### 2.1 获取文章列表

**GET** `/`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认10 |
| categoryId | long | 否 | 分类ID筛选 |
| tagId | long | 否 | 标签ID筛选 |
| status | int | 否 | 状态筛选(需管理员权限) |
| keyword | string | 否 | 关键词搜索 |
| orderBy | string | 否 | 排序字段(publish_time/view_count/like_count) |
| order | string | 否 | 排序方式(asc/desc)，默认desc |

**响应:**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "slug": "article-slug",
        "summary": "文章摘要",
        "content": "文章内容...",
        "coverImage": "https://example.com/cover.png",
        "userId": 1,
        "categoryId": 1,
        "status": 1,
        "viewCount": 100,
        "likeCount": 50,
        "commentCount": 10,
        "isTop": false,
        "publishTime": "2024-04-15T12:00:00",
        "createTime": "2024-04-15T10:00:00",
        "updateTime": "2024-04-15T12:00:00",
        "author": {
          "id": 1,
          "username": "cocona"
        },
        "category": {
          "id": 1,
          "name": "技术"
        },
        "tags": [
          {"id": 1, "name": "Vue"},
          {"id": 2, "name": "前端"}
        ]
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10
  }
}
```

### 2.2 获取文章详情(通过ID)

**GET** `/{id}`

**响应:** 同上单个文章对象

### 2.3 获取文章详情(通过Slug)

**GET** `/slug/{slug}`

**响应:** 同上单个文章对象

### 2.4 创建文章

**POST** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "title": "文章标题",
  "slug": "article-slug",
  "summary": "文章摘要",
  "content": "文章内容(Markdown)",
  "coverImage": "https://example.com/cover.png",
  "categoryId": 1,
  "tagIds": [1, 2, 3],
  "status": 1,
  "isTop": false
}
```

### 2.5 更新文章

**PUT** `/{id}`

**Headers:** `Authorization: Bearer {token}`

**请求体:** 同创建文章

### 2.6 删除文章

**DELETE** `/{id}`

**Headers:** `Authorization: Bearer {token}`

### 2.7 增加浏览次数

**POST** `/{id}/view`

### 2.8 点赞文章

**POST** `/{id}/like`

**Headers:** `Authorization: Bearer {token}`

### 2.9 取消点赞

**DELETE** `/{id}/like`

**Headers:** `Authorization: Bearer {token}`

---

## 3. 分类模块 `/api/v1/categories`

### 3.1 获取分类列表

**GET** `/`

**响应:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "技术",
      "slug": "tech",
      "description": "技术相关文章",
      "parentId": null,
      "sort": 0,
      "articleCount": 50,
      "createTime": "2024-01-01T00:00:00"
    }
  ]
}
```

### 3.2 获取分类详情

**GET** `/{id}`

### 3.3 创建分类

**POST** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "name": "技术",
  "slug": "tech",
  "description": "技术相关文章",
  "parentId": null,
  "sort": 0
}
```

### 3.4 更新分类

**PUT** `/{id}`

**Headers:** `Authorization: Bearer {token}`

### 3.5 删除分类

**DELETE** `/{id}`

**Headers:** `Authorization: Bearer {token}`

---

## 4. 标签模块 `/api/v1/tags`

### 4.1 获取标签列表(分页)

**GET** `/`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 4.2 获取所有标签

**GET** `/all`

**响应:**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Vue",
      "slug": "vue",
      "articleCount": 20,
      "createTime": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4.3 获取标签详情

**GET** `/{id}`

### 4.4 创建标签

**POST** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "name": "Vue",
  "slug": "vue"
}
```

### 4.5 更新标签

**PUT** `/{id}`

**Headers:** `Authorization: Bearer {token}`

### 4.6 删除标签

**DELETE** `/{id}`

**Headers:** `Authorization: Bearer {token}`

---

## 5. 评论模块 `/api/v1/comments`

### 5.1 获取文章评论

**GET** `/article/{articleId}`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| orderBy | string | 否 | 排序字段(create_time/like_count) |
| order | string | 否 | 排序方式(asc/desc) |

**响应:**
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "content": "很棒的文章！",
        "userId": 2,
        "articleId": 1,
        "parentId": null,
        "replyToId": null,
        "likeCount": 5,
        "status": 0,
        "createTime": "2024-04-15T12:00:00",
        "user": {
          "id": 2,
          "username": "reader"
        }
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "totalPages": 5
  }
}
```

### 5.2 发表评论

**POST** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "content": "很棒的文章！",
  "articleId": 1,
  "parentId": null,
  "replyToId": null
}
```

### 5.3 删除评论

**DELETE** `/{id}`

**Headers:** `Authorization: Bearer {token}`

### 5.4 点赞评论

**POST** `/{id}/like`

**Headers:** `Authorization: Bearer {token}`

### 5.5 取消点赞评论

**DELETE** `/{id}/like`

**Headers:** `Authorization: Bearer {token}`

---

## 6. 点赞模块 `/api/v1/likes`

### 6.1 检查是否已点赞

**GET** `/check`

**Headers:** `Authorization: Bearer {token}`

**查询参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| targetId | long | 是 | 目标ID |
| targetType | int | 是 | 目标类型(0-文章, 1-评论) |

**响应:**
```json
{
  "code": 200,
  "data": {
    "isLiked": true
  }
}
```

### 6.2 点赞

**POST** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "targetId": 1,
  "targetType": 0
}
```

### 6.3 取消点赞

**DELETE** `/`

**Headers:** `Authorization: Bearer {token}`

**请求体:**
```json
{
  "targetId": 1,
  "targetType": 0
}
```

---

## 7. 配置模块 `/api/v1/configs`

### 7.1 获取公开配置

**GET** `/public`

**响应:**
```json
{
  "code": 200,
  "data": {
    "siteName": "Cocona Blog",
    "siteDescription": "一个充满二次元风格的博客系统",
    "siteKeywords": "博客,技术,二次元",
    "footerText": "© 2024 Cocona Blog",
    "socialLinks": {
      "github": "https://github.com/cocona",
      "twitter": "",
      "email": "cocona@example.com"
    }
  }
}
```

### 7.2 获取所有配置

**GET** `/`

**Headers:** `Authorization: Bearer {token}` (需管理员权限)

### 7.3 更新配置

**PUT** `/{key}`

**Headers:** `Authorization: Bearer {token}` (需管理员权限)

**请求体:**
```json
{
  "value": "新的配置值"
}
```

---

## 项目结构

```
src/main/java/cn/wujizone/coconablog/
├── CoconaBlogApplication.java    # 启动类
├── common/                       # 通用类
│   ├── PageResult.java          # 分页结果
│   └── Result.java              # 统一响应
├── config/                       # 配置类
│   └── SecurityConfig.java      # 安全配置
├── controller/                   # 控制器层
│   ├── ArticleController.java
│   ├── CategoryController.java
│   ├── CommentController.java
│   ├── ConfigController.java
│   ├── LikeController.java
│   ├── TagController.java
│   └── UserController.java
├── dto/                          # 数据传输对象
│   ├── ArticleRequest.java
│   ├── ArticleVO.java
│   ├── CategoryRequest.java
│   ├── CategoryVO.java
│   ├── CommentRequest.java
│   ├── CommentVO.java
│   ├── LikeCheckResponse.java
│   ├── LikeRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── TagRequest.java
│   ├── TagVO.java
│   └── UserVO.java
├── entity/                       # 实体类
│   ├── Article.java
│   ├── ArticleTag.java
│   ├── Category.java
│   ├── Comment.java
│   ├── Config.java
│   ├── Like.java
│   ├── Tag.java
│   └── User.java
├── exception/                    # 异常处理
│   └── GlobalExceptionHandler.java
├── filter/                       # 过滤器
│   └── JwtAuthenticationFilter.java
├── mapper/                       # Mapper接口
│   ├── ArticleMapper.java
│   ├── ArticleTagMapper.java
│   ├── CategoryMapper.java
│   ├── CommentMapper.java
│   ├── ConfigMapper.java
│   ├── LikeMapper.java
│   ├── TagMapper.java
│   └── UserMapper.java
├── service/                      # 服务层
│   ├── ArticleService.java
│   ├── CategoryService.java
│   ├── CommentService.java
│   ├── ConfigService.java
│   ├── LikeService.java
│   ├── TagService.java
│   └── UserService.java
└── util/                         # 工具类
    └── JwtUtil.java
```

## 启动说明

1. 确保MySQL数据库已启动，并创建了`coconablog`数据库
2. 执行 `docs/coconablog.sql` 创建数据表
3. 修改 `application.yml` 中的数据库连接配置
4. 运行 `CoconaBlogApplication.java` 启动服务
5. 服务默认运行在 `http://localhost:8080`
