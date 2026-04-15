INSERT INTO article(title, slug, summary, content, user_id, status, view_count, like_count, comment_count, is_top, publish_time) 
VALUES('欢迎来到 Cocona Blog', 'welcome', '这是我的第一篇博客文章，欢迎大家访问！', '## 欢迎来到 Cocona Blog\n\n这是一个充满二次元风格的博客系统，使用 Vue 3 和 Spring Boot 构建。\n\n### 功能特性\n\n- 🎨 可爱的UI设计，梦幻的配色方案\n- 💻 前端、后端技术分享\n- 💖 点赞、评论互动交流\n- 🌙 持续更新优质内容\n\n希望大家喜欢！', 1, 1, 10, 5, 2, 1, NOW());

INSERT INTO article(title, slug, summary, content, user_id, status, view_count, like_count, comment_count, is_top, publish_time) 
VALUES('Vue 3 入门指南', 'vue3-guide', '从零开始学习 Vue 3，掌握组合式API', '## Vue 3 入门指南\n\nVue 3 是一款用于构建用户界面的渐进式 JavaScript 框架。\n\n### 组合式API\n\n```javascript\nimport { ref, computed } from 'vue'\n\nconst count = ref(0)\nconst doubled = computed(() => count.value * 2)\n```\n\n### 核心特性\n\n- 响应式系统\n- 组件化开发\n- 虚拟DOM优化', 1, 1, 50, 12, 8, 0, NOW());

INSERT INTO article(title, slug, summary, content, user_id, status, view_count, like_count, comment_count, is_top, publish_time) 
VALUES('Spring Boot 实战', 'springboot-practice', 'Spring Boot 后端开发最佳实践', '## Spring Boot 实战\n\nSpring Boot 简化了 Spring 应用的创建和部署。\n\n### 主要优点\n\n- 自动配置\n- 嵌入式服务器\n- 约定优于配置\n- 丰富的starter依赖\n\n### 常用注解\n\n- @RestController\n- @Service\n- @Repository\n- @Autowired', 1, 1, 30, 8, 5, 0, NOW());

INSERT INTO category(name, slug, description, sort) VALUES('技术', 'tech', '技术相关文章', 1);
INSERT INTO category(name, slug, description, sort) VALUES('生活', 'life', '生活感悟分享', 2);
INSERT INTO category(name, slug, description, sort) VALUES('前端', 'frontend', '前端开发', 3);
INSERT INTO category(name, slug, description, sort) VALUES('后端', 'backend', '后端开发', 4);

INSERT INTO tag(name, slug) VALUES('Vue');
INSERT INTO tag(name, slug) VALUES('React');
INSERT INTO tag(name, slug) VALUES('Java');
INSERT INTO tag(name, slug) VALUES('Spring Boot');
INSERT INTO tag(name, slug) VALUES('TypeScript');

INSERT INTO article_tag(article_id, tag_id) VALUES(1, 1);
INSERT INTO article_tag(article_id, tag_id) VALUES(2, 1);
INSERT INTO article_tag(article_id, tag_id) VALUES(2, 5);
INSERT INTO article_tag(article_id, tag_id) VALUES(3, 3);
INSERT INTO article_tag(article_id, tag_id) VALUES(3, 4);

UPDATE article SET category_id = 1 WHERE id = 1;
UPDATE article SET category_id = 3 WHERE id = 2;
UPDATE article SET category_id = 4 WHERE id = 3;
