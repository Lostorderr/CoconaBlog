package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.CommentVO;
import cn.wujizone.coconablog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return Result.success(commentService.getCommentsByArticleId(articleId, page, pageSize, null, null));
    }
    
    @GetMapping
    public Result<PageResult<CommentVO>> getAllComments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.getAllComments(page, pageSize, status));
    }
    
    @GetMapping("/my")
    public Result<PageResult<CommentVO>> getMyComments(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(commentService.getMyComments(userId, page, pageSize));
    }
    
    @PostMapping
    public Result<CommentVO> createComment(@AuthenticationPrincipal Long userId,
                                            @RequestBody CommentVO request) {
        return Result.success(commentService.createComment(userId, request));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        commentService.deleteComment(id, userId);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long id,
                                             @RequestBody CommentVO request) {
        commentService.updateCommentStatus(id, request.getStatus());
        return Result.success();
    }
    
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@PathVariable Long id) {
        commentService.updateLikeCount(id, 1);
        return Result.success();
    }
    
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeComment(@PathVariable Long id) {
        commentService.updateLikeCount(id, -1);
        return Result.success();
    }
}
