package cn.wujizone.coconablog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String avatar;
    private String email;
    private Integer role;
    private Integer status;
    private String securityQuestion;
    private LocalDateTime createTime;
    private LocalDateTime lastLogin;
}
