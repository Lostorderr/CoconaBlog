package cn.wujizone.coconablog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TagVO {
    private Long id;
    private String name;
    private String slug;
    private Integer articleCount;
    private LocalDateTime createTime;
}
