import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js/lib/core'
import javascript from 'highlight.js/lib/languages/javascript'
import typescript from 'highlight.js/lib/languages/typescript'
import java from 'highlight.js/lib/languages/java'
import python from 'highlight.js/lib/languages/python'
import css from 'highlight.js/lib/languages/css'
import xml from 'highlight.js/lib/languages/xml'
import sql from 'highlight.js/lib/languages/sql'
import json from 'highlight.js/lib/languages/json'
import bash from 'highlight.js/lib/languages/bash'
import markdown from 'highlight.js/lib/languages/markdown'

hljs.registerLanguage('javascript', javascript)
hljs.registerLanguage('js', javascript)
hljs.registerLanguage('typescript', typescript)
hljs.registerLanguage('ts', typescript)
hljs.registerLanguage('java', java)
hljs.registerLanguage('python', python)
hljs.registerLanguage('py', python)
hljs.registerLanguage('css', css)
hljs.registerLanguage('html', xml)
hljs.registerLanguage('xml', xml)
hljs.registerLanguage('sql', sql)
hljs.registerLanguage('json', json)
hljs.registerLanguage('bash', bash)
hljs.registerLanguage('shell', bash)
hljs.registerLanguage('markdown', markdown)
hljs.registerLanguage('md', markdown)

export interface TocItem {
  id: string
  text: string
  level: number
}

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight(str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch {
      }
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

const defaultImageRender = md.renderer.rules.image || function(tokens, idx, options, env, self) {
  return self.renderToken(tokens, idx, options)
}

md.renderer.rules.image = (tokens, idx, options, env, self) => {
  const token = tokens[idx]
  const src = token.attrGet('src') || ''
  const alt = token.content || ''

  token.attrSet('class', 'markdown-image')
  token.attrSet('data-src', src)
  // 用 data-full-src 存储原图 URL，src 先留空由 CSS 控制占位
  token.attrSet('data-full-src', src)

  const imgHtml = defaultImageRender(tokens, idx, options as any, env, self)
  // 包裹在可点击容器中
  return `<span class="img-lightbox-trigger">${imgHtml}</span>`
}

export function renderMarkdown(content: string): { html: string; toc: TocItem[] } {
  const toc: TocItem[] = []
  let headingIndex = 0

  md.renderer.rules.heading_open = (tokens, idx) => {
    const token = tokens[idx]
    if (!token?.tag) return ''
    const level = token.tag.match(/h(\d)/)
    if (!level || !level[1]) return ''
    const hLevel = parseInt(level[1])
    const nextToken = tokens[idx + 1]
    const text = nextToken ? nextToken.content : ''
    const id = `heading-${headingIndex++}`
    toc.push({ id, text, level: hLevel })
    return `<${token.tag} id="${id}">`
  }

  const html = md.render(content)
  return { html, toc }
}
