package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.*;
import cn.wujizone.coconablog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录, username={}", request.getUsername());
        return Result.success(userService.login(request));
    }
    
    @PostMapping("/auth/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册, username={}", request.getUsername());
        userService.register(request);
        return Result.success();
    }
    
    @PostMapping("/auth/logout")
    public Result<Void> logout() {
        log.info("用户登出");
        return Result.success();
    }

    @GetMapping("/auth/forgot-password/question")
    public Result<UserVO> getSecurityQuestion(@RequestParam String username) {
        log.info("获取安全问题, username={}", username);
        return Result.success(userService.getSecurityInfo(username));
    }

    @PostMapping("/auth/forgot-password/verify")
    public Result<Boolean> verifySecurity(@Valid @RequestBody VerifySecurityRequest request) {
        log.info("验证安全问题, username={}", request.getUsername());
        return Result.success(userService.verifySecurityAnswer(request.getUsername(), request.getSecurityAnswer()));
    }

    @PostMapping("/auth/forgot-password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("重置密码, username={}", request.getUsername());
        userService.resetPassword(request.getUsername(), request.getNewPassword());
        return Result.success();
    }
    
    @GetMapping("/profile")
    public Result<UserVO> getProfile(@AuthenticationPrincipal Long userId) {
        log.info("获取个人资料, userId={}", userId);
        return Result.success(userService.getUserById(userId));
    }
    
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@AuthenticationPrincipal Long userId,
                                         @RequestBody UserVO request) {
        log.info("更新个人资料, userId={}", userId);
        return Result.success(userService.updateProfile(userId, request));
    }
    
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        log.info("获取用户详情, id={}", id);
        return Result.success(userService.getUserById(id));
    }
    
    @GetMapping
    public Result<PageResult<UserVO>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("获取所有用户, page={}, pageSize={}", page, pageSize);
        return Result.success(userService.getAllUsers(page, pageSize));
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @RequestBody UserVO request) {
        log.info("更新用户状态, id={}, status={}", id, request.getStatus());
        userService.updateUserStatus(id, request.getStatus());
        return Result.success();
    }
    
    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id,
                                        @RequestBody UserVO request) {
        log.info("更新用户角色, id={}, role={}", id, request.getRole());
        userService.updateUserRole(id, request.getRole());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户, id={}", id);
        userService.deleteUser(id);
        return Result.success();
    }
}
