package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.ArticleRequest;
import cn.wujizone.coconablog.dto.ArticleVO;
import cn.wujizone.coconablog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {
    
    private final ArticleService articleService;
    
    @GetMapping
    public Result<PageResult<ArticleVO>> getArticleList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "publish_time") String orderBy,
            @RequestParam(defaultValue = "desc") String order) {
        log.info("获取文章列表, page={}, pageSize={}, categoryId={}, tagId={}, status={}, keyword={}, orderBy={}, order={}",
                page, pageSize, categoryId, tagId, status, keyword, orderBy, order);
        return Result.success(articleService.getArticleList(page, pageSize, categoryId, tagId, status, keyword, orderBy, order));
    }
    
    @GetMapping("/my")
    public Result<PageResult<ArticleVO>> getMyArticles(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("获取我的文章, userId={}, page={}, pageSize={}", userId, page, pageSize);
        return Result.success(articleService.getMyArticles(userId, page, pageSize));
    }
    
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleById(@PathVariable Long id) {
        log.info("获取文章详情, id={}", id);
        return Result.success(articleService.getArticleById(id));
    }
    
    @GetMapping("/slug/{slug}")
    public Result<ArticleVO> getArticleBySlug(@PathVariable String slug) {
        log.info("通过slug获取文章, slug={}", slug);
        return Result.success(articleService.getArticleBySlug(slug));
    }
    
    @PostMapping
    public Result<ArticleVO> createArticle(@AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody ArticleRequest request) {
        log.info("创建文章, userId={}, title={}", userId, request.getTitle());
        return Result.success(articleService.createArticle(userId, request));
    }
    
    @PutMapping("/{id}")
    public Result<ArticleVO> updateArticle(@PathVariable Long id,
                                            @AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody ArticleRequest request) {
        log.info("更新文章, id={}, userId={}, title={}", id, userId, request.getTitle());
        return Result.success(articleService.updateArticle(id, userId, request));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        log.info("删除文章, id={}, userId={}", id, userId);
        articleService.softDeleteArticle(id, userId);
        return Result.success();
    }
    
    @PostMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        log.info("增加文章浏览量, id={}", id);
        articleService.incrementViewCount(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id,
                                     @AuthenticationPrincipal Long userId) {
        log.info("点赞文章, id={}, userId={}", id, userId);
        articleService.updateLikeCount(id, 1);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeArticle(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        log.info("取消点赞文章, id={}, userId={}", id, userId);
        articleService.updateLikeCount(id, -1);
        return Result.success();
    }
}
