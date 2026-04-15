<template>
  <div class="create-article-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">📝 发布新文章</h1>
        <p class="page-subtitle">分享你的想法和知识</p>
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
              <label class="form-label">URL标识</label>
              <input
                v-model="form.slug"
                type="text"
                class="form-input"
                placeholder="自动生成或手动输入"
              />
            </div>
            <div class="form-group">
              <label class="form-label">分类</label>
              <select v-model="form.categoryId" class="form-select">
                <option :value="null">选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
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
                🔗
              </button>
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

        <div class="form-sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">发布设置</h3>
            
            <div class="form-group">
              <label class="form-label">封面图片</label>
              <input
                v-model="form.coverImage"
                type="url"
                class="form-input"
                placeholder="图片URL地址"
              />
              <div v-if="form.coverImage" class="cover-preview">
                <img :src="form.coverImage" alt="封面预览" />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">标签</label>
              <div class="tags-input">
                <div class="selected-tags">
                  <span v-for="tagId in form.tagIds" :key="tagId" class="selected-tag">
                    {{ getTagName(tagId) }}
                    <button type="button" class="remove-tag" @click="removeTag(tagId)">×</button>
                  </span>
                </div>
                <select class="tag-select" @change="addTag($event)">
                  <option value="">选择标签</option>
                  <option v-for="tag in availableTags" :key="tag.id" :value="tag.id">
                    {{ tag.name }}
                  </option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">状态</label>
              <div class="status-options">
                <label class="status-option">
                  <input type="radio" v-model="form.status" :value="0" />
                  <span class="status-label">
                    <span class="status-icon">📝</span>
                    <span>草稿</span>
                  </span>
                </label>
                <label class="status-option">
                  <input type="radio" v-model="form.status" :value="1" />
                  <span class="status-label">
                    <span class="status-icon">✅</span>
                    <span>发布</span>
                  </span>
                </label>
              </div>
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="form.isTop" />
                <span>置顶文章</span>
              </label>
            </div>

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
            <h3 class="sidebar-title">💡 写作提示</h3>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore } from '@/store/blog'
import { articleApi } from '@/api/article'
import { useAuth } from '@/composables/useApi'

const router = useRouter()
const blogStore = useBlogStore()
const { isLoggedIn } = useAuth()

const contentEditor = ref<HTMLTextAreaElement | null>(null)
const submitting = ref(false)

const form = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
  status: 1,
  isTop: false
})

const categories = computed(() => blogStore.categories)
const tags = computed(() => blogStore.tags)

const availableTags = computed(() => 
  tags.value.filter(t => !form.tagIds.includes(t.id))
)

onMounted(async () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  await blogStore.fetchCategories()
  await blogStore.fetchTags()
})

function getTagName(id: number): string {
  return tags.value.find(t => t.id === id)?.name || ''
}

function addTag(event: Event) {
  const select = event.target as HTMLSelectElement
  const value = parseInt(select.value)
  if (value && !form.tagIds.includes(value)) {
    form.tagIds.push(value)
  }
  select.value = ''
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

async function handleSubmit() {
  if (!form.title.trim() || !form.content.trim()) {
    alert('请填写文章标题和内容')
    return
  }
  
  submitting.value = true
  try {
    const slug = form.slug.trim() || form.title.toLowerCase().replace(/\s+/g, '-').replace(/[^\w-]/g, '')
    
    await articleApi.create({
      title: form.title.trim(),
      slug,
      summary: form.summary.trim() || undefined,
      content: form.content.trim(),
      coverImage: form.coverImage.trim() || undefined,
      categoryId: form.categoryId || undefined,
      tagIds: form.tagIds.length > 0 ? form.tagIds : undefined,
      status: form.status,
      isTop: form.isTop
    })
    
    alert(form.status === 1 ? '文章发布成功！' : '草稿保存成功！')
    router.push('/articles')
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

.content-editor {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.95rem;
  line-height: 1.6;
}

.cover-preview {
  margin-top: var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  overflow: hidden;
}

.cover-preview img {
  width: 100%;
  height: 150px;
  object-fit: cover;
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

.tag-select {
  padding: var(--spacing-sm);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 0.9rem;
}

.status-options {
  display: flex;
  gap: var(--spacing-md);
}

.status-option {
  flex: 1;
  cursor: pointer;
}

.status-option input {
  display: none;
}

.status-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  background: var(--bg-hover);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.status-option input:checked + .status-label {
  background: var(--gradient-primary);
  color: white;
  border-color: transparent;
}

.status-icon {
  font-size: 1.2rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  accent-color: var(--primary-color);
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

  .status-options {
    flex-direction: column;
  }
}
</style>
