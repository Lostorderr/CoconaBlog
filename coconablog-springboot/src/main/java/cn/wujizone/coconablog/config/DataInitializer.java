package cn.wujizone.coconablog.config;

import cn.wujizone.coconablog.entity.Article;
import cn.wujizone.coconablog.entity.ArticleTag;
import cn.wujizone.coconablog.entity.Category;
import cn.wujizone.coconablog.entity.Tag;
import cn.wujizone.coconablog.entity.User;
import cn.wujizone.coconablog.mapper.ArticleMapper;
import cn.wujizone.coconablog.mapper.ArticleTagMapper;
import cn.wujizone.coconablog.mapper.CategoryMapper;
import cn.wujizone.coconablog.mapper.TagMapper;
import cn.wujizone.coconablog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        initAdminUser();
//        initCategories();
//        initTags();
        // 文章不再自动创建，通过管理后台手动添加
        // initArticles();
    }

    private void initAdminUser() {
        if (userMapper.findByUsername("admin") == null) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setEmail("admin@example.com");
            user.setRole(1);
            user.setStatus(0);
            userMapper.insert(user);
            System.out.println("Created admin user");
        }
    }

    private void initCategories() {
        if (categoryMapper.findBySlug("tech") == null) {
            Category cat = new Category();
            cat.setName("技术");
            cat.setSlug("tech");
            cat.setDescription("技术相关文章");
            cat.setSort(1);
            categoryMapper.insert(cat);
        }
        if (categoryMapper.findBySlug("life") == null) {
            Category cat = new Category();
            cat.setName("生活");
            cat.setSlug("life");
            cat.setDescription("生活感悟分享");
            cat.setSort(2);
            categoryMapper.insert(cat);
        }
        if (categoryMapper.findBySlug("frontend") == null) {
            Category cat = new Category();
            cat.setName("前端");
            cat.setSlug("frontend");
            cat.setDescription("前端开发");
            cat.setSort(3);
            categoryMapper.insert(cat);
        }
        if (categoryMapper.findBySlug("backend") == null) {
            Category cat = new Category();
            cat.setName("后端");
            cat.setSlug("backend");
            cat.setDescription("后端开发");
            cat.setSort(4);
            categoryMapper.insert(cat);
        }
    }

    private void initTags() {
        String[][] tags = {{"Vue", "vue"}, {"React", "react"}, {"Java", "java"}, {"Spring Boot", "spring-boot"}, {"TypeScript", "typescript"}};
        for (String[] tag : tags) {
            if (tagMapper.findBySlug(tag[1]) == null) {
                Tag t = new Tag();
                t.setName(tag[0]);
                t.setSlug(tag[1]);
                tagMapper.insert(t);
            }
        }
    }

    private void initArticles() {
        if (articleMapper.findBySlug("welcome") == null) {
            Article article = new Article();
            article.setTitle("欢迎来到 Cocona Blog");
            article.setSlug("welcome");
            article.setSummary("这是我的第一篇博客文章，欢迎大家访问！");
            article.setContent("## 欢迎来到 Cocona Blog\n\n这是一个充满二次元风格的博客系统，使用 Vue 3 和 Spring Boot 构建。\n\n### 功能特性\n\n- 🎨 可爱的UI设计，梦幻的配色方案\n- 💻 前端、后端技术分享\n- 💖 点赞、评论互动交流\n- 🌙 持续更新优质内容\n\n希望大家喜欢！");
            article.setUserId(1L);
            article.setCategoryId(1L);
            article.setStatus(1);
            article.setViewCount(10);
            article.setLikeCount(5);
            article.setCommentCount(2);
            article.setIsTop(true);
            article.setPublishTime(LocalDateTime.now());
            articleMapper.insert(article);
            
            ArticleTag at = new ArticleTag();
            at.setArticleId(article.getId());
            at.setTagId(1L);
            articleTagMapper.insert(at);
        }

        if (articleMapper.findBySlug("vue3-guide") == null) {
            Article article = new Article();
            article.setTitle("Vue 3 入门指南");
            article.setSlug("vue3-guide");
            article.setSummary("从零开始学习 Vue 3，掌握组合式API");
            article.setContent("## Vue 3 入门指南\n\nVue 3 是一款用于构建用户界面的渐进式 JavaScript 框架。\n\n### 组合式API\n\n```javascript\nimport { ref, computed } from 'vue'\n\nconst count = ref(0)\nconst doubled = computed(() => count.value * 2)\n```\n\n### 核心特性\n\n- 响应式系统\n- 组件化开发\n- 虚拟DOM优化");
            article.setUserId(1L);
            article.setCategoryId(3L);
            article.setStatus(1);
            article.setViewCount(50);
            article.setLikeCount(12);
            article.setCommentCount(8);
            article.setIsTop(false);
            article.setPublishTime(LocalDateTime.now());
            articleMapper.insert(article);
            
            ArticleTag at1 = new ArticleTag();
            at1.setArticleId(article.getId());
            at1.setTagId(1L);
            articleTagMapper.insert(at1);
            
            ArticleTag at2 = new ArticleTag();
            at2.setArticleId(article.getId());
            at2.setTagId(5L);
            articleTagMapper.insert(at2);
        }

        if (articleMapper.findBySlug("springboot-practice") == null) {
            Article article = new Article();
            article.setTitle("Spring Boot 实战");
            article.setSlug("springboot-practice");
            article.setSummary("Spring Boot 后端开发最佳实践");
            article.setContent("## Spring Boot 实战\n\nSpring Boot 简化了 Spring 应用的创建和部署。\n\n### 主要优点\n\n- 自动配置\n- 嵌入式服务器\n- 约定优于配置\n- 丰富的starter依赖\n\n### 常用注解\n\n- @RestController\n- @Service\n- @Repository\n- @Autowired");
            article.setUserId(1L);
            article.setCategoryId(4L);
            article.setStatus(1);
            article.setViewCount(30);
            article.setLikeCount(8);
            article.setCommentCount(5);
            article.setIsTop(false);
            article.setPublishTime(LocalDateTime.now());
            articleMapper.insert(article);
            
            ArticleTag at1 = new ArticleTag();
            at1.setArticleId(article.getId());
            at1.setTagId(3L);
            articleTagMapper.insert(at1);
            
            ArticleTag at2 = new ArticleTag();
            at2.setArticleId(article.getId());
            at2.setTagId(4L);
            articleTagMapper.insert(at2);
        }
    }
}
