package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.dto.*;
import cn.wujizone.coconablog.entity.Article;
import cn.wujizone.coconablog.entity.ArticleTag;
import cn.wujizone.coconablog.entity.Tag;
import cn.wujizone.coconablog.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;
    
    public PageResult<ArticleVO> getArticleList(Integer page, Integer pageSize, Long categoryId,
                                                  Long tagId, Integer status, String keyword) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        List<Article> articles = articleMapper.findByCondition(categoryId, tagId, keyword, status, null, offset, pageSize);
        Long total = articleMapper.countByCondition(categoryId, tagId, keyword, status, null);
        
        List<ArticleVO> voList = articles.stream()
                .map(this::toArticleVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, pageSize);
    }
    
    public PageResult<ArticleVO> getMyArticles(Long userId, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        List<Article> articles = articleMapper.findByCondition(null, null, null, null, userId, offset, pageSize);
        Long total = articleMapper.countByCondition(null, null, null, null, userId);
        
        List<ArticleVO> voList = articles.stream()
                .map(this::toArticleVODetail)
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, pageSize);
    }
    
    public ArticleVO getArticleById(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return toArticleVODetail(article);
    }
    
    public ArticleVO getArticleBySlug(String slug) {
        Article article = articleMapper.findBySlug(slug);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return toArticleVODetail(article);
    }
    
    @Transactional
    public ArticleVO createArticle(Long userId, ArticleRequest request) {
        if (request.getSlug() != null && articleMapper.findBySlug(request.getSlug()) != null) {
            throw new RuntimeException("slug已存在");
        }
        
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setUserId(userId);
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus());
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setIsTop(request.getIsTop());
        
        if (request.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }
        
        articleMapper.insert(article);
        
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<ArticleTag> articleTags = request.getTagIds().stream()
                    .map(tagId -> {
                        ArticleTag at = new ArticleTag();
                        at.setArticleId(article.getId());
                        at.setTagId(tagId);
                        return at;
                    })
                    .collect(Collectors.toList());
            articleTagMapper.batchInsert(articleTags);
        }
        
        return getArticleById(article.getId());
    }
    
    @Transactional
    public ArticleVO updateArticle(Long id, Long userId, ArticleRequest request) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此文章");
        }
        
        if (request.getTitle() != null) article.setTitle(request.getTitle());
        if (request.getSlug() != null && !request.getSlug().equals(article.getSlug())) {
            if (articleMapper.findBySlug(request.getSlug()) != null) {
                throw new RuntimeException("slug已存在");
            }
            article.setSlug(request.getSlug());
        }
        if (request.getSummary() != null) article.setSummary(request.getSummary());
        if (request.getContent() != null) article.setContent(request.getContent());
        if (request.getCoverImage() != null) article.setCoverImage(request.getCoverImage());
        if (request.getCategoryId() != null) article.setCategoryId(request.getCategoryId());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
            if (request.getStatus() == 1 && article.getPublishTime() == null) {
                article.setPublishTime(LocalDateTime.now());
            }
        }
        if (request.getIsTop() != null) article.setIsTop(request.getIsTop());
        
        articleMapper.update(article);
        
        if (request.getTagIds() != null) {
            articleTagMapper.deleteByArticleId(id);
            if (!request.getTagIds().isEmpty()) {
                List<ArticleTag> articleTags = request.getTagIds().stream()
                        .map(tagId -> {
                            ArticleTag at = new ArticleTag();
                            at.setArticleId(id);
                            at.setTagId(tagId);
                            return at;
                        })
                        .collect(Collectors.toList());
                articleTagMapper.batchInsert(articleTags);
            }
        }
        
        return getArticleById(id);
    }
    
    @Transactional
    public void softDeleteArticle(Long id, Long userId) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此文章");
        }
        articleMapper.updateStatus(id, 2);
    }
    
    @Transactional
    public void incrementViewCount(Long id) {
        articleMapper.incrementViewCount(id);
    }
    
    @Transactional
    public void updateLikeCount(Long id, Integer delta) {
        articleMapper.updateLikeCount(id, delta);
    }
    
    private ArticleVO toArticleVO(Article article) {
        if (article == null) return null;
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSlug(article.getSlug());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCoverImage(article.getCoverImage());
        vo.setUserId(article.getUserId());
        vo.setCategoryId(article.getCategoryId());
        vo.setStatus(article.getStatus());
        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setIsTop(article.getIsTop());
        vo.setPublishTime(article.getPublishTime());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        return vo;
    }
    
    private ArticleVO toArticleVODetail(Article article) {
        ArticleVO vo = toArticleVO(article);
        if (vo != null) {
            vo.setAuthor(userService.getUserById(article.getUserId()));
            if (article.getCategoryId() != null) {
                vo.setCategory(categoryService.getCategoryById(article.getCategoryId()));
            }
            List<Tag> tags = tagMapper.findByArticleId(article.getId());
            vo.setTags(tags.stream().map(tagService::toTagVO).collect(Collectors.toList()));
        }
        return vo;
    }
}
