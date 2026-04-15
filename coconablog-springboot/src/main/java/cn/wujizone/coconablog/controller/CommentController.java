package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.CommentRequest;
import cn.wujizone.coconablog.dto.CommentVO;
import cn.wujizone.coconablog.service.CommentService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String order) {
        return Result.success(commentService.getCommentsByArticleId(articleId, page, pageSize, orderBy, order));
    }
    
    @PostMapping
    public Result<CommentVO> createComment(@AuthenticationPrincipal Long userId,
                                            @Valid @RequestBody CommentRequest request) {
        CommentVO vo = new CommentVO();
        vo.setContent(request.getContent());
        vo.setArticleId(request.getArticleId());
        vo.setParentId(request.getParentId());
        vo.setReplyToId(request.getReplyToId());
        return Result.success(commentService.createComment(userId, vo));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        commentService.deleteComment(id, userId);
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
