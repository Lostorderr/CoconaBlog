package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.*;
import cn.wujizone.coconablog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }
    
    @PostMapping("/auth/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }
    
    @PostMapping("/auth/logout")
    public Result<Void> logout(@AuthenticationPrincipal Long userId) {
        userService.logout(userId);
        return Result.success();
    }
    
    @GetMapping("/auth/profile")
    public Result<UserVO> getProfile(@AuthenticationPrincipal Long userId) {
        return Result.success(userService.getProfile(userId));
    }
    
    @PutMapping("/auth/profile")
    public Result<UserVO> updateProfile(@AuthenticationPrincipal Long userId,
                                         @RequestBody UserVO request) {
        return Result.success(userService.updateProfile(userId, request.getAvatar()));
    }
    
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }
}
