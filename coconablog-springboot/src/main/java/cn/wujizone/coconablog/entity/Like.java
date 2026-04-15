package cn.wujizone.coconablog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private Long id;
    private Long userId;
    private Long targetId;
    private Integer targetType;
    private LocalDateTime createTime;
}
