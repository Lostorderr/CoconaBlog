<template>
  <div class="create-article-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">发布新文章</h1>
        <p class="page-subtitle">分享你的想法和知识</p>
        <!-- 草稿加载提示 -->
        <div v-if="loadedDraftId" class="draft-loaded-hint">
          <span class="draft-icon">📝</span>
          <span>已自动加载最近保存的草稿</span>
          <button type="button" class="btn-clear-draft" @click="clearDraft">清除草稿</button>
        </div>
      </div>

      <form class="article-form" @submit.prevent="handleSubmit">
        <div class="form-main">
          <div class="form-group">
            <label class="form-label">文章标题 *</label>
            <input
              v-model="form.title"
              type="text"
              class="form-input"
              placeholder="请输入文章标题"
              required
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">分类</label>
              <select v-model="form.categoryId" class="form-select">
                <option :value="null">选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">标签</label>
              <div class="tags-input-inline">
                <div class="selected-tags-inline">
                  <span v-for="tagId in form.tagIds" :key="tagId" class="selected-tag-inline">
                    {{ getTagName(tagId) }}
                    <button type="button" class="remove-tag" @click="removeTag(tagId)">×</button>
                  </span>
                </div>
                <div class="tag-input-wrapper">
                  <input
                    ref="tagInputRef"
                    type="text"
                    class="tag-input tag-input-inline"
                    v-model="tagInputText"
                    @input="onTagInput"
                    @keydown.enter.prevent="handleTagEnter"
                    @keydown.down.prevent="moveTagSuggestion(1)"
                    @keydown.up.prevent="moveTagSuggestion(-1)"
                    @focus="showTagSuggestions = true"
                    @blur="hideTagSuggestions"
                    placeholder="输入标签..."
                  />
                  <ul v-if="showTagSuggestions && filteredTagSuggestions.length > 0" class="tag-suggestions tag-suggestions-inline">
                    <li
                      v-for="(suggestion, index) in filteredTagSuggestions"
                      :key="suggestion.id ?? 'new-' + index"
                      :class="{ active: tagSuggestionIndex === index }"
                      @mousedown.prevent="selectSuggestion(suggestion)"
                    >
                      {{ suggestion.isNew ? '+ 创建: ' + suggestion.name : suggestion.name }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">文章摘要</label>
            <textarea
              v-model="form.summary"
              class="form-textarea"
              rows="3"
              placeholder="请输入文章摘要（可选）"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">文章内容 *</label>
            <div class="editor-toolbar">
              <button type="button" class="toolbar-btn" @click="insertMarkdown('**', '**')" title="粗体">
                <strong>B</strong>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('*', '*')" title="斜体">
                <em>I</em>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('# ', '')" title="标题">
                H
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('## ', '')" title="二级标题">
                H2
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('`', '`')" title="代码">
                &lt;/&gt;
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('```\n', '\n```')" title="代码块">
                Code
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('- ', '')" title="列表">
                •
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('> ', '')" title="引用">
                "
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('[', '](url)')" title="链接">
                链接
              </button>
              <label class="toolbar-btn image-upload-label" title="上传图片">
                图片
                <input
                  ref="imageInputRef"
                  type="file"
                  accept="image/*"
                  style="display: none;"
                  @change="handleImageUpload"
                />
              </label>
            </div>
            <textarea
              ref="contentEditor"
              v-model="form.content"
              class="form-textarea content-editor"
              rows="20"
              placeholder="请输入文章内容，支持Markdown格式"
              required
            ></textarea>
          </div>
        </div>

        <!-- 固定悬浮的发布按钮栏 -->
        <div class="floating-bar" :class="{ visible: showFloatingBar }">
          <button type="button" class="btn btn-secondary btn-sm" @click="saveDraft" :disabled="submitting">
            保存草稿
          </button>
          <button type="submit" class="btn btn-primary btn-sm" :disabled="submitting">
            {{ submitting ? '发布中...' : '发布文章' }}
          </button>
        </div>

        <div class="form-sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">发布设置</h3>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="saveDraft" :disabled="submitting">
                保存草稿
              </button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">
                {{ submitting ? '发布中...' : '发布文章' }}
              </button>
            </div>
          </div>

          <div class="sidebar-card tips-card">
            <h3 class="sidebar-title">写作提示</h3>
            <ul class="tips-list">
              <li>使用 Markdown 格式编写文章</li>
              <li>标题使用 # 符号标记</li>
              <li>代码块使用三个反引号包裹</li>
              <li>图片使用 ![](url) 格式</li>
              <li>链接使用 [文字](url) 格式</li>
            </ul>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'
import { articleApi } from '@/api/article'
import { tagApi } from '@/api/tag'
import { fileApi } from '@/api/file'
import { useAuth } from '@/composables/useApi'

const router = useRouter()
const blogStore = useBlogStore()
const { isLoggedIn } = useAuth()

const contentEditor = ref<HTMLTextAreaElement | null>(null)
const tagInputRef = ref<HTMLInputElement | null>(null)
const imageInputRef = ref<HTMLInputElement | null>(null)
const uploadingImage = ref(false)
const submitting = ref(false)
const tagInputText = ref('')
const showTagSuggestions = ref(false)
const tagSuggestionIndex = ref(0)
const showFloatingBar = ref(false)
const loadedDraftId = ref<number | null>(null)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
  status: 1,
})

const categories = computed(() => Array.isArray(blogStore.categories) ? blogStore.categories : [])
const allTags = computed(() => Array.isArray(blogStore.tags) ? blogStore.tags : [])

const availableTags = computed(() => 
  allTags.value.filter(t => !form.tagIds.includes(t.id))
)

// 过滤匹配的已有标签 + 新建建议
const filteredTagSuggestions = computed(() => {
  const keyword = tagInputText.value.trim().toLowerCase()
  const result: Array<{ id: number; name: string; isNew: boolean }> = []
  const tagList = Array.isArray(allTags.value) ? allTags.value : []

  if (keyword) {
    // 先显示匹配的已有标签（优先展示）
    const matched = tagList
      .filter(t => !form.tagIds.includes(t.id) && t.name.toLowerCase().includes(keyword))
      .slice(0, 5)
      .map(t => ({ ...t, isNew: false }))
    result.push(...matched)

    // 再追加"创建新标签"选项（仅当输入内容与所有已有标签不完全匹配时）
    if (!tagList.some(t => t.name.toLowerCase() === keyword)) {
      result.push({ id: -1, name: keyword, isNew: true })
    }
  } else {
    // 未输入时：显示可选的已有标签供选择
    const shown = tagList
      .filter(t => !form.tagIds.includes(t.id))
      .slice(0, 8)
      .map(t => ({ ...t, isNew: false }))
    result.push(...shown)
  }

  return result
})

onMounted(async () => {
  try {
    if (!isLoggedIn.value) {
      router.push('/login')
      return
    }
    // 检查发帖权限
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      if (user.role === 0) {
        alert('您暂无发帖权限，请联系管理员申请成为授权用户')
        router.push('/articles')
        return
      }
    }
    await Promise.allSettled([
      blogStore.fetchCategories(),
      blogStore.fetchTags()
    ])
    // 加载最新草稿
    await loadLatestDraft()
  } catch (e) {
    console.error('CreateArticle 初始化失败:', e)
  }

  // 滚动时显示/隐藏悬浮发布按钮
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleScroll() {
  showFloatingBar.value = window.scrollY > 200
}

async function loadLatestDraft() {
  try {
    const res = await articleApi.getMyArticles({ page: 1, pageSize: 1 })
    const articles = res.data?.list || res.data || []
    // 筛选草稿状态的文章，取最新的一篇
    const draftArticles = Array.isArray(articles) ? (articles).filter((a: any) => a.status === 0) : []
    if (draftArticles.length > 0) {
      fillFormWithDraft(draftArticles[0])
    }
  } catch (e) {
    console.error('加载草稿失败:', e)
  }
}

function fillFormWithDraft(article: any) {
  form.title = article.title || ''
  form.summary = article.summary || ''
  form.content = article.content || ''
  form.categoryId = article.categoryId || null
  form.status = article.status ?? 0
  loadedDraftId.value = article.id
  // 填充标签 ID
  if (article.tags && Array.isArray(article.tags)) {
    form.tagIds = (article.tags as any[]).map(t => t.id).filter(Boolean)
  }
}

function clearDraft() {
  form.title = ''
  form.summary = ''
  form.content = ''
  form.categoryId = null
  form.tagIds = []
  form.status = 1
  loadedDraftId.value = null
}

function getTagName(id: number): string {
  // 检查是否是临时标签（id为负数表示新建）
  if (id < 0) return ''
  const tagList = Array.isArray(allTags.value) ? allTags.value : []
  return tagList.find(t => t.id === id)?.name || ''
}

function onTagInput() {
  showTagSuggestions.value = true
  tagSuggestionIndex.value = 0
}

async function handleTagEnter() {
  if (filteredTagSuggestions.value.length > 0) {
    const suggestion = filteredTagSuggestions.value[tagSuggestionIndex.value] || filteredTagSuggestions.value[0]
    await selectSuggestion(suggestion)
  }
}

async function selectSuggestion(suggestion: { id: number; name: string; isNew: boolean }) {
  if (suggestion.isNew) {
    try {
      const rawSlug = suggestion.name.trim()
        .toLowerCase()
        .replace(/\s+/g, '-')
        .replace(/[^\w\u4e00-\u9fff-]/g, '')
        .replace(/^-+|-+$/g, '')
      const slug = rawSlug || ('tag-' + Date.now())
      
      const res = await tagApi.create({ name: suggestion.name, slug })
      form.tagIds.push(res.data.id)
      await blogStore.fetchTags()
    } catch (e: any) {
      const msg = e.response?.data?.message || e.message || '创建标签失败'
      alert(msg)
    }
  } else {
    if (!form.tagIds.includes(suggestion.id)) {
      form.tagIds.push(suggestion.id)
    }
  }
  tagInputText.value = ''
  showTagSuggestions.value = false
  tagSuggestionIndex.value = 0
  tagInputRef.value?.focus()
}

function moveTagSuggestion(delta: number) {
  const len = filteredTagSuggestions.value.length
  if (len === 0) return
  tagSuggestionIndex.value = (tagSuggestionIndex.value + delta + len) % len
}

function hideTagSuggestions() {
  setTimeout(() => {
    showTagSuggestions.value = false
  }, 200)
}

function removeTag(id: number) {
  form.tagIds = form.tagIds.filter(t => t !== id)
}

function insertMarkdown(before: string, after: string) {
  if (!contentEditor.value) return
  
  const textarea = contentEditor.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = form.content
  const selected = text.substring(start, end)
  
  form.content = text.substring(0, start) + before + selected + after + text.substring(end)
  
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selected.length)
  }, 0)
}

async function handleImageUpload(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  uploadingImage.value = true
  try {
    const res = await fileApi.uploadImage(file)
    const imageUrl = res.data
    // 在光标位置插入 Markdown 图片语法
    insertMarkdown(`![图片](${imageUrl})`, '')
  } catch (e: any) {
    alert(e.response?.data?.message || e.message || '图片上传失败')
  } finally {
    uploadingImage.value = false
    // 清空 input 以允许重复选择同一文件
    input.value = ''
  }
}

async function handleSubmit() {
  if (!form.title.trim() || !form.content.trim()) {
    alert('请填写文章标题和内容')
    return
  }

  submitting.value = true
  try {
    // 从标题自动生成 slug（不再暴露给用户）
    const slug = form.title.trim()
      .toLowerCase()
      .replace(/\s+/g, '-')
      .replace(/[^\w\u4e00-\u9fff-]/g, '')
      .replace(/^-+|-+$/g, '')
      || ('post-' + Date.now())

    if (loadedDraftId.value) {
      // 已有草稿 → 更新
      await articleApi.update(loadedDraftId.value, {
        title: form.title.trim(),
        slug,
        summary: form.summary.trim() || undefined,
        content: form.content.trim(),
        categoryId: form.categoryId || undefined,
        tagIds: form.tagIds.length > 0 ? form.tagIds : undefined,
        status: form.status,
      })
      alert(form.status === 1 ? '文章发布成功！' : '草稿保存成功！')
      // 发布成功后清除草稿引用
      if (form.status === 1) {
        loadedDraftId.value = null
      }
    } else {
      // 新文章 → 创建
      await articleApi.create({
        title: form.title.trim(),
        slug,
        summary: form.summary.trim() || undefined,
        content: form.content.trim(),
        categoryId: form.categoryId || undefined,
        tagIds: form.tagIds.length > 0 ? form.tagIds : undefined,
        status: form.status,
      })
      alert(form.status === 1 ? '文章发布成功！' : '草稿保存成功！')
    }
    
    if (form.status === 1) {
      router.push('/articles')
    }
  } catch (e: any) {
    alert(e.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

async function saveDraft() {
  form.status = 0
  await handleSubmit()
}
</script>

<style scoped>
.create-article-page {
  min-height: 100vh;
  padding: var(--spacing-xl) 0;
  background: var(--bg-secondary);
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.page-subtitle {
  color: var(--text-secondary);
}

.draft-loaded-hint {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: linear-gradient(135deg, rgba(234, 179, 8, 0.12), rgba(251, 191, 36, 0.08));
  border: 1px solid rgba(234, 179, 8, 0.3);
  border-radius: var(--border-radius);
  font-size: 0.9rem;
  color: #b8860b;
}

.draft-icon {
  font-size: 1rem;
}

.btn-clear-draft {
  background: none;
  border: none;
  color: #b8860b;
  cursor: pointer;
  font-size: 0.85rem;
  text-decoration: underline;
  padding: 0 4px;
}

.btn-clear-draft:hover {
  color: #d4a017;
}

.article-form {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--spacing-xl);
}

.form-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.sidebar-card {
  background: var(--bg-card);
  border-radius: var(--border-radius);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.sidebar-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 0.95rem;
}

.form-input,
.form-select,
.form-textarea {
  padding: var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 1rem;
  font-family: var(--font-sans);
  transition: all var(--transition-normal);
  background: var(--bg-card);
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm);
  background: var(--bg-hover);
  border-radius: var(--border-radius-sm);
  margin-bottom: var(--spacing-sm);
}

.toolbar-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.toolbar-btn:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.image-upload-label {
  cursor: pointer;
}

.image-upload-label input {
  display: none;
}

.content-editor {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.95rem;
  line-height: 1.6;
}

.tags-input {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: rgba(255, 107, 157, 0.1);
  color: var(--primary-color);
  border-radius: var(--border-radius-xs);
  font-size: 0.85rem;
}

.remove-tag {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
}

.tag-input-wrapper {
  position: relative;
}

.tag-input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
  background: var(--bg-card);
  transition: all var(--transition-normal);
}

.tag-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.tag-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 10;
  max-height: 180px;
  overflow-y: auto;
  margin-top: 4px;
  padding: var(--spacing-xs) 0;
  background: var(--bg-card);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  box-shadow: var(--shadow-md);
  list-style: none;
}

.tag-suggestions li {
  padding: var(--spacing-sm) var(--spacing-md);
  cursor: pointer;
  font-size: 0.9rem;
  color: var(--text-primary);
  transition: background 0.15s;
}

/* 内联标签输入（用于 form-row 中的紧凑布局） */
.tags-input-inline {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.selected-tags-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 28px;
}

.selected-tag-inline {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 2px 8px;
  background: rgba(255, 107, 157, 0.1);
  color: var(--primary-color);
  border-radius: 12px;
  font-size: 0.8rem;
}

.tag-input-inline {
  width: 100%;
  height: 40px;
  padding: 6px 10px;
  font-size: 0.9rem;
  box-sizing: border-box;
}

.tag-suggestions-inline {
  min-width: 200px;
}

.tag-suggestions li:hover,
.tag-suggestions li.active {
  background: rgba(255, 107, 157, 0.08);
  color: var(--primary-color);
}

/* 固定悬浮发布按钮栏 */
.floating-bar {
  position: fixed;
  bottom: -80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--bg-card);
  border-radius: var(--border-radius);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.12);
  transition: bottom 0.3s ease;
}

.floating-bar.visible {
  bottom: var(--spacing-lg);
}

.floating-bar .btn {
  min-width: 120px;
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: 0.95rem;
}

.btn-sm {
  font-size: 0.9rem;
  padding: var(--spacing-sm) var(--spacing-lg);
}

.form-actions {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}

.form-actions .btn {
  flex: 1;
}

.tips-card {
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%);
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.tips-list li {
  font-size: 0.9rem;
  color: var(--text-secondary);
  padding-left: var(--spacing-md);
  position: relative;
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--primary-color);
}

@media (max-width: 1024px) {
  .article-form {
    grid-template-columns: 1fr;
  }

  .form-sidebar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
