package cn.wujizone.coconablog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private Integer sort;
    private Integer articleCount;
    private LocalDateTime createTime;
}
