package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.dto.*;
import cn.wujizone.coconablog.entity.User;
import cn.wujizone.coconablog.mapper.UserMapper;
import cn.wujizone.coconablog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
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
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(passwordEncoder.encode(request.getSecurityAnswer()));
        user.setRole(0);
        user.setStatus(0);
        userMapper.insert(user);
    }
    
    public UserVO getUserById(Long id) {
        User user = userMapper.findById(id);
        return toUserVO(user);
    }
    
    public UserVO updateProfile(Long userId, UserVO request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        userMapper.update(user);
        
        UserVO updated = toUserVO(user);
        return updated;
    }
    
    public PageResult<UserVO> getAllUsers(Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 20;
        
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.findAll();
        Long total = (long) users.size();
        
        int end = Math.min(offset + pageSize, users.size());
        List<User> paged = users.subList(offset, end);
        
        List<UserVO> voList = paged.stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, pageSize);
    }
    
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        userMapper.update(user);
    }
    
    @Transactional
    public void updateUserRole(Long id, Integer role) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRole(role);
        userMapper.update(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public UserVO getSecurityInfo(String username) {
        User user = userMapper.findByUsernameForSecurity(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setSecurityQuestion(user.getSecurityQuestion());
        // 返回密保问题但不返回答案
        return vo;
    }
    
    public boolean verifySecurityAnswer(String username, String answer) {
        User user = userMapper.findByUsernameForSecurity(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 兼容：BCrypt哈希 或 明文
        if (passwordEncoder.matches(answer, user.getSecurityAnswer())) {
            return true;
        }
        return answer.equals(user.getSecurityAnswer());
    }
    
    @Transactional
    public void resetPassword(String username, String newPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updatePasswordByUsername(username, passwordEncoder.encode(newPassword));
    }
    
    private UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setLastLogin(user.getLastLogin());
        return vo;
    }
}
