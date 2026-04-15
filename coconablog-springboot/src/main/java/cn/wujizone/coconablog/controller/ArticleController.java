package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.ArticleRequest;
import cn.wujizone.coconablog.dto.ArticleVO;
import cn.wujizone.coconablog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String order) {
        return Result.success(articleService.getArticleList(page, pageSize, categoryId, tagId, status, keyword, orderBy, order));
    }
    
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleById(@PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }
    
    @GetMapping("/slug/{slug}")
    public Result<ArticleVO> getArticleBySlug(@PathVariable String slug) {
        return Result.success(articleService.getArticleBySlug(slug));
    }
    
    @PostMapping
    public Result<ArticleVO> createArticle(@AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody ArticleRequest request) {
        return Result.success(articleService.createArticle(userId, request));
    }
    
    @PutMapping("/{id}")
    public Result<ArticleVO> updateArticle(@PathVariable Long id,
                                            @AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody ArticleRequest request) {
        return Result.success(articleService.updateArticle(id, userId, request));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        articleService.deleteArticle(id, userId);
        return Result.success();
    }
    
    @PostMapping("/{id}/view")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id,
                                     @AuthenticationPrincipal Long userId) {
        articleService.updateLikeCount(id, 1);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeArticle(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        articleService.updateLikeCount(id, -1);
        return Result.success();
    }
}
