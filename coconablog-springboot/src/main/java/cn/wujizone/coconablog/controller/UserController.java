package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
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
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/auth/forgot-password/question")
    public Result<UserVO> getSecurityQuestion(@RequestParam String username) {
        return Result.success(userService.getSecurityInfo(username));
    }

    @PostMapping("/auth/forgot-password/verify")
    public Result<Boolean> verifySecurity(@Valid @RequestBody VerifySecurityRequest request) {
        return Result.success(userService.verifySecurityAnswer(request.getUsername(), request.getSecurityAnswer()));
    }

    @PostMapping("/auth/forgot-password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getUsername(), request.getNewPassword());
        return Result.success();
    }
    
    @GetMapping("/profile")
    public Result<UserVO> getProfile(@AuthenticationPrincipal Long userId) {
        return Result.success(userService.getUserById(userId));
    }
    
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@AuthenticationPrincipal Long userId,
                                         @RequestBody UserVO request) {
        return Result.success(userService.updateProfile(userId, request));
    }
    
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }
    
    @GetMapping
    public Result<PageResult<UserVO>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(userService.getAllUsers(page, pageSize));
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @RequestBody UserVO request) {
        userService.updateUserStatus(id, request.getStatus());
        return Result.success();
    }
    
    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id,
                                        @RequestBody UserVO request) {
        userService.updateUserRole(id, request.getRole());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
