package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {
    
    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(@Param("id") Long id);
    
    @Select("<script>" +
            "SELECT * FROM comment WHERE article_id = #{articleId} AND status = 0 " +
            "<if test='parentId == null'> AND parent_id IS NULL </if>" +
            "<if test='parentId != null'> AND parent_id = #{parentId} </if>" +
            " ORDER BY ${orderBy} ${order} LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Comment> findByArticleId(@Param("articleId") Long articleId,
                                   @Param("parentId") Long parentId,
                                   @Param("orderBy") String orderBy,
                                   @Param("order") String order,
                                   @Param("offset") Integer offset,
                                   @Param("pageSize") Integer pageSize);
    
    @Select("SELECT COUNT(*) FROM comment WHERE article_id = #{articleId} AND status = 0")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Select("SELECT * FROM comment WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);
    
    @Insert("INSERT INTO comment(content, user_id, article_id, parent_id, reply_to_id, status, create_time) " +
            "VALUES(#{content}, #{userId}, #{articleId}, #{parentId}, #{replyToId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    @Update("UPDATE comment SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE comment SET like_count = like_count + #{delta} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);
    
    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
