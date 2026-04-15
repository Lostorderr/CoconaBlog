package cn.wujizone.coconablog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String content;
    private Long userId;
    private Long articleId;
    private Long parentId;
    private Long replyToId;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
}
