package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Config;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ConfigMapper {
    
    @Select("SELECT * FROM config WHERE id = #{id}")
    Config findById(@Param("id") Integer id);
    
    @Select("SELECT * FROM config WHERE `key` = #{key}")
    Config findByKey(@Param("key") String key);
    
    @Select("SELECT * FROM config")
    List<Config> findAll();
    
    @Insert("INSERT INTO config(`key`, `value`, description) VALUES(#{key}, #{value}, #{description})")
    int insert(Config config);
    
    @Update("UPDATE config SET `value` = #{value} WHERE `key` = #{key}")
    int updateByKey(@Param("key") String key, @Param("value") String value);
    
    @Delete("DELETE FROM config WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
