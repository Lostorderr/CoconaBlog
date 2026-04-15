package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.LikeCheckResponse;
import cn.wujizone.coconablog.dto.LikeRequest;
import cn.wujizone.coconablog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    
    private final LikeService likeService;
    
    @GetMapping("/check")
    public Result<LikeCheckResponse> checkLike(@AuthenticationPrincipal Long userId,
                                                @RequestParam Long targetId,
                                                @RequestParam Integer targetType) {
        return Result.success(likeService.checkLike(userId, targetId, targetType));
    }
    
    @PostMapping
    public Result<Void> like(@AuthenticationPrincipal Long userId,
                              @RequestBody LikeRequest request) {
        likeService.like(userId, request.getTargetId(), request.getTargetType());
        return Result.success();
    }
    
    @DeleteMapping
    public Result<Void> unlike(@AuthenticationPrincipal Long userId,
                                @RequestBody LikeRequest request) {
        likeService.unlike(userId, request.getTargetId(), request.getTargetType());
        return Result.success();
    }
}
