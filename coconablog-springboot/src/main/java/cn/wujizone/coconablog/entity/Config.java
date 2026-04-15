package cn.wujizone.coconablog.entity;

import lombok.Data;

@Data
public class Config {
    private Integer id;
    private String key;
    private String value;
    private String description;
}
