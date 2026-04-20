package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.CommentVO;
import cn.wujizone.coconablog.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/article/{articleId}")
    public Result<PageResult<CommentVO>> getCommentsByArticleId(
            @PathVariable Long articleId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("获取文章评论, articleId={}, page={}, pageSize={}", articleId, page, pageSize);
        return Result.success(commentService.getCommentsByArticleId(articleId, page, pageSize, null, null));
    }
    
    @GetMapping
    public Result<PageResult<CommentVO>> getAllComments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        log.info("获取所有评论, page={}, pageSize={}, status={}", page, pageSize, status);
        return Result.success(commentService.getAllComments(page, pageSize, status));
    }
    
    @GetMapping("/my")
    public Result<PageResult<CommentVO>> getMyComments(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("获取我的评论, userId={}, page={}, pageSize={}", userId, page, pageSize);
        return Result.success(commentService.getMyComments(userId, page, pageSize));
    }
    
    @PostMapping
    public Result<CommentVO> createComment(@AuthenticationPrincipal Long userId,
                                            @RequestBody CommentVO request) {
        log.info("创建评论, userId={}, articleId={}", userId, request.getArticleId());
        return Result.success(commentService.createComment(userId, request));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        log.info("删除评论, id={}, userId={}", id, userId);
        commentService.deleteComment(id, userId);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long id,
                                             @RequestBody CommentVO request) {
        log.info("更新评论状态, id={}, status={}", id, request.getStatus());
        commentService.updateCommentStatus(id, request.getStatus());
        return Result.success();
    }
    
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@PathVariable Long id) {
        log.info("点赞评论, id={}", id);
        commentService.updateLikeCount(id, 1);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeComment(@PathVariable Long id) {
        log.info("取消点赞评论, id={}", id);
        commentService.updateLikeCount(id, -1);
        return Result.success();
    }
}
