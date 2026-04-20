<template>
  <div class="edit-article-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">编辑文章</h1>
        <p class="page-subtitle">修改你的文章</p>
      </div>

      <div v-if="loading" class="loading-state">
        <span class="loading-spinner"></span>
        <p>加载中...</p>
      </div>

      <form v-else class="article-form" @submit.prevent="handleSubmit">
        <div class="form-main">
          <div class="form-group">
            <label class="form-label">标题 *</label>
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
              <label class="form-label">Tags</label>
              <div class="tags-field">
                <div class="selected-tags-inline">
                  <span v-for="tagId in form.tagIds" :key="tagId" class="selected-tag-inline">
                    {{ getTagName(tagId) }}
                    <button type="button" class="remove-tag" @click.stop="removeTag(tagId)">x</button>
                  </span>
                </div>
                <div class="tags-input-wrap">
                  <input
                    ref="tagInputRef"
                    type="text"
                    class="tag-input-field"
                    v-model="tagInputText"
                    @input="onTagInput"
                    @keydown.enter.prevent="handleTagEnter"
                    @keydown.down.prevent="moveTagSuggestion(1)"
                    @keydown.up.prevent="moveTagSuggestion(-1)"
                    @focus="onTagFocus"
                    placeholder="输入标签..."
                  />
                </div>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">摘要</label>
            <textarea
              v-model="form.summary"
              class="form-textarea"
              rows="3"
              placeholder="请输入文章摘要(可选)"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">内容 *</label>
            <div class="editor-toolbar">
              <button type="button" class="toolbar-btn" @click="insertMarkdown('**', '**')" title="Bold">
                <strong>B</strong>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('*', '*')" title="Italic">
                <em>I</em>
              </button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('# ', '')" title="H1">H</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('## ', '')" title="H2">H2</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('`', '`')" title="Code">&lt;/&gt;</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('```\n', '\n```')" title="Code Block">Code</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('- ', '')" title="List">List</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('> ', '')" title="Quote">Quote</button>
              <button type="button" class="toolbar-btn" @click="insertMarkdown('[', '](url)')" title="Link">Link</button>
              <label class="toolbar-btn image-upload-label" title="Upload Image">
                Image
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
              placeholder="使用 Markdown 格式编写文章内容"
              required
            ></textarea>
          </div>
        </div>

        <div class="floating-bar" :class="{ visible: showFloatingBar }">
          <button type="button" class="btn btn-secondary btn-sm" @click="saveDraft" :disabled="submitting">
            保存草稿
          </button>
          <button type="submit" class="btn btn-primary btn-sm" :disabled="submitting">
            {{ submitting ? '发布中...' : '发布' }}
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
                {{ submitting ? '发布中...' : '发布' }}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'
import { articleApi } from '@/api/article'
import { tagApi } from '@/api/tag'
import { fileApi } from '@/api/file'
import { useAuth } from '@/composables/useApi'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()
const { isLoggedIn } = useAuth()

const contentEditor = ref<HTMLTextAreaElement | null>(null)
const tagInputRef = ref<HTMLInputElement | null>(null)
const imageInputRef = ref<HTMLInputElement | null>(null)
const uploadingImage = ref(false)
const loading = ref(true)
const submitting = ref(false)
const tagInputText = ref('')
const showTagSuggestions = ref(false)
const tagSuggestionIndex = ref(0)
const dropdownPos = ref({ top: 0, left: 0, width: 0 })
const showFloatingBar = ref(false)

const dropdownStyle = computed(() => ({
  top: `${dropdownPos.value.top}px`,
  left: `${dropdownPos.value.left}px`,
  width: `${dropdownPos.value.width}px`,
}))

const articleTagNames = ref<Map<number, string>>(new Map())

const form = reactive({
  title: '',
  summary: '',
  content: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
  status: 1,
})

const categories = computed(() => blogStore.categories)
const allTags = computed(() => Array.isArray(blogStore.tags) ? blogStore.tags : [])

const availableTags = computed(() => {
  const tagList = Array.isArray(allTags.value) ? allTags.value : []
  return tagList.filter(t => !form.tagIds.includes(t.id))
})

const filteredTagSuggestions = computed(() => {
  const keyword = tagInputText.value.trim().toLowerCase()
  const result: Array<{ id: number; name: string; isNew: boolean }> = []
  const tagList = Array.isArray(allTags.value) ? allTags.value : []

  if (keyword) {
    const matched = tagList
      .filter(t => !form.tagIds.includes(t.id) && t.name.toLowerCase().includes(keyword))
      .slice(0, 5)
      .map(t => ({ ...t, isNew: false }))
    result.push(...matched)

    if (!tagList.some(t => t.name.toLowerCase() === keyword)) {
      result.push({ id: -1, name: keyword, isNew: true })
    }
  } else {
    const shown = tagList
      .filter(t => !form.tagIds.includes(t.id))
      .slice(0, 8)
      .map(t => ({ ...t, isNew: false }))
    result.push(...shown)
  }

  return result
})

onMounted(async () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }

  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    if (user.role === 0) {
      alert('您暂无发帖权限，请联系管理员申请成为授权用户')
      router.push('/articles')
      return
    }
  }

  await blogStore.fetchCategories()
  await blogStore.fetchTags()
  await loadArticle()

  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleScroll() {
  showFloatingBar.value = window.scrollY > 200
}

async function loadArticle() {
  const id = parseInt(route.params.id as string)
  if (isNaN(id)) {
    router.push('/profile')
    return
  }

  loading.value = true
  try {
    const response = await articleApi.getById(id)
    const article = response.data
    form.title = article.title
    form.summary = article.summary || ''
    form.content = article.content
    form.categoryId = article.categoryId ? Number(article.categoryId) : null
    form.tagIds = article.tags?.map((t: any) => t.id) || []

    const nameMap = new Map<number, string>()
    article.tags?.forEach((t: any) => {
      if (t.id && t.name) nameMap.set(t.id, t.name)
    })
    articleTagNames.value = nameMap
    form.status = article.status
  } catch {
    alert('文章不存在或无权访问')
    router.push('/profile')
  } finally {
    loading.value = false
  }
}

function getTagName(id: number): string {
  if (id < 0) return ''
  if (articleTagNames.value.has(id)) {
    return articleTagNames.value.get(id) || ''
  }
  const tagList = Array.isArray(allTags.value) ? allTags.value : []
  return tagList.find(t => t.id === id)?.name || ''
}

function onTagInput() {
  showTagSuggestions.value = true
  tagSuggestionIndex.value = 0
  updateDropdownPosition()
}

function onTagFocus() {
  showTagSuggestions.value = true
  updateDropdownPosition()
}

function updateDropdownPosition() {
  nextTick(() => {
    const input = tagInputRef.value
    if (!input) return
    const rect = input.getBoundingClientRect()
    dropdownPos.value = {
      top: rect.bottom + window.scrollY + 4,
      left: rect.left + window.scrollX,
      width: rect.width
    }
  })
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
      const msg = e.response?.data?.message || e.message || '标签创建失败'
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
    insertMarkdown(`![image](${imageUrl})`, '')
  } catch (e: any) {
    alert(e.response?.data?.message || e.message || '图片上传失败')
  } finally {
    uploadingImage.value = false
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
    const id = parseInt(route.params.id as string)
    await articleApi.update(id, {
      title: form.title.trim(),
      summary: form.summary.trim() || undefined,
      content: form.content.trim(),
      categoryId: form.categoryId || undefined,
      tagIds: form.tagIds.length > 0 ? form.tagIds : undefined,
      status: 1,
    })

    alert('发布成功!')
    router.push('/profile')
  } catch (e: any) {
    alert(e.response?.data?.message || '更新失败')
  } finally {
    submitting.value = false
  }
}

async function saveDraft() {
  if (!form.title.trim() || !form.content.trim()) {
    alert('请填写文章标题和内容')
    return
  }

  submitting.value = true
  try {
    const id = parseInt(route.params.id as string)
    await articleApi.update(id, {
      title: form.title.trim(),
      summary: form.summary.trim() || undefined,
      content: form.content.trim(),
      categoryId: form.categoryId || undefined,
      tagIds: form.tagIds.length > 0 ? form.tagIds : undefined,
      status: 0,
    })

    alert('草稿保存成功!')
    router.push('/profile')
  } catch (e: any) {
    alert(e.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.edit-article-page {
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

.loading-state {
  text-align: center;
  padding: var(--spacing-2xl);
}

.loading-spinner {
  font-size: 3rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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
  box-sizing: border-box;
  color: var(--text-primary);
}

.form-select {
  height: 40px;
  appearance: auto;
  -webkit-appearance: menulist;
}

.form-select option {
  color: var(--text-primary);
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

.tags-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: var(--spacing-lg);
}

.selected-tags-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 24px;
}

.selected-tag-inline {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 1px 8px;
  background: rgba(255, 107, 157, 0.12);
  color: var(--primary-color);
  border-radius: 10px;
  font-size: 0.8rem;
  line-height: 20px;
}

.tags-input-wrap {
  position: relative;
}

.tag-input-field {
  width: 100%;
  height: 40px;
  padding: 6px 10px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
  background: var(--bg-card);
  transition: all var(--transition-normal);
  box-sizing: border-box;
}

.tag-input-field:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
}

.remove-tag {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
}

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
