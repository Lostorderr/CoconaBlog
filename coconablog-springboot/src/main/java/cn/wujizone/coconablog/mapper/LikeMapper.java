package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    
    @Select("SELECT * FROM `like` WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    Like findByUserAndTarget(@Param("userId") Long userId,
                             @Param("targetId") Long targetId,
                             @Param("targetType") Integer targetType);
    
    @Insert("INSERT INTO `like`(user_id, target_id, target_type, create_time) " +
            "VALUES(#{userId}, #{targetId}, #{targetType}, NOW())")
    int insert(Like like);
    
    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    int delete(@Param("userId") Long userId,
               @Param("targetId") Long targetId,
               @Param("targetType") Integer targetType);
    
    @Select("SELECT COUNT(*) FROM `like` WHERE target_id = #{targetId} AND target_type = #{targetType}")
    Long countByTarget(@Param("targetId") Long targetId, @Param("targetType") Integer targetType);
}
