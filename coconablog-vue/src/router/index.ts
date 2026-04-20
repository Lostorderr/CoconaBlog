import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/Articles.vue'),
    meta: { title: '文章列表' }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/ArticleDetail.vue'),
    meta: { title: '文章详情' }
  },
  {
    path: '/create-article',
    name: 'CreateArticle',
    component: () => import('@/views/CreateArticle.vue'),
    meta: { title: '发布文章', requiresAuth: true }
  },
  {
    path: '/edit-article/:id',
    name: 'EditArticle',
    component: () => import('@/views/EditArticle.vue'),
    meta: { title: '编辑文章', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue'),
    meta: { title: '关于' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { title: '找回密码' }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/ArticleManage.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'tags',
        name: 'AdminTags',
        component: () => import('@/views/admin/TagManage.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/CommentManage.vue'),
        meta: { title: '评论管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'config',
        name: 'AdminConfig',
        component: () => import('@/views/admin/ConfigManage.vue'),
        meta: { title: '系统配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach(async (to, from, next) => {
  document.title = `${to.meta.title || '博客'} - Cocona Blog`
  
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  let isAdmin = false
  
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      isAdmin = user.role === 1
    } catch {
    }
  }
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 发帖/编辑文章需要 role >= 1 (非普通用户)
  const isAuthorPage = to.path === '/create-article' || to.path.startsWith('/edit-article')
  if (isAuthorPage && token && userStr) {
    try {
      const user = JSON.parse(userStr)
      // role: 0=普通用户, 1=管理员, 2=授权用户
      if (user.role === 0) {
        alert('您暂无发帖权限，请联系管理员申请成为授权用户')
        next('/articles')
        return
      }
    } catch {}
  }
  
  if (to.meta.requiresAdmin && !isAdmin) {
    alert('无权访问管理后台')
    next('/')
    return
  }
  
  next()
})

export default router
