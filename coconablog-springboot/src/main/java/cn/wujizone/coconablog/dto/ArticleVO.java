package cn.wujizone.coconablog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
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
    private UserVO author;
    private CategoryVO category;
    private List<TagVO> tags;
}
