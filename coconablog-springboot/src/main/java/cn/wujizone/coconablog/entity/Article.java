package cn.wujizone.coconablog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    private Long userId;
    private Long categoryId;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isTop;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
