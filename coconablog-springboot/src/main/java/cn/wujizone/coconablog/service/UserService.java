package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.dto.*;
import cn.wujizone.coconablog.entity.User;
import cn.wujizone.coconablog.mapper.UserMapper;
import cn.wujizone.coconablog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public UserVO getUserById(Long id) {
        User user = userMapper.findById(id);
        return toUserVO(user);
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 1) {
            throw new RuntimeException("用户已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        userMapper.updateLastLogin(user.getId());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(toUserVO(user));
        return response;
    }
    
    @Transactional
    public void register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(0);
        user.setStatus(0);
        userMapper.insert(user);
    }
    
    @Transactional
    public void logout(Long userId) {
    }
    
    public UserVO getProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toUserVO(user);
    }
    
    @Transactional
    public UserVO updateProfile(Long userId, String avatar) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(avatar);
        userMapper.update(user);
        return toUserVO(user);
    }
    
    private UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());
        vo.setLastLogin(user.getLastLogin());
        return vo;
    }
}
