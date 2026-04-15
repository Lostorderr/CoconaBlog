package cn.wujizone.coconablog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    @NotNull(message = "文章ID不能为空")
    private Long articleId;
    
    private Long parentId;
    
    private Long replyToId;
}
