package cn.wujizone.coconablog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private String content;
    private Long userId;
    private Long articleId;
    private Long parentId;
    private Long replyToId;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private UserVO user;
    private UserVO replyTo;
    private ArticleInfo article;
    private List<CommentVO> children;
    
    @Data
    public static class ArticleInfo {
        private Long id;
        private String title;
    }
}
