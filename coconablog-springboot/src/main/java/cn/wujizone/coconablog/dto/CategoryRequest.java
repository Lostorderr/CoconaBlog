package cn.wujizone.coconablog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @NotBlank(message = "slug不能为空")
    private String slug;
    
    private String description;
    
    private Long parentId;
    
    private Integer sort = 0;
}
