package cn.wujizone.coconablog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ArticleRequest {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "slug不能为空")
    private String slug;
    
    private String summary;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String coverImage;
    
    private Long categoryId;
    
    private List<Long> tagIds;
    
    private Integer status = 0;
    
    private Boolean isTop = false;
}
