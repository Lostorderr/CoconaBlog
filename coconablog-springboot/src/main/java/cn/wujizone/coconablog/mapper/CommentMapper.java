package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {
    
    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(@Param("id") Long id);
    
    @Select("<script>SELECT * FROM comment WHERE article_id = #{articleId} <if test='status != null'> AND status = #{status} </if> ORDER BY ${orderBy} ${order} LIMIT #{offset}, #{pageSize}</script>")
    List<Comment> findByArticleId(@Param("articleId") Long articleId, @Param("status") Integer status, @Param("orderBy") String orderBy, @Param("order") String order, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    
    @Select("SELECT COUNT(*) FROM comment WHERE article_id = #{articleId}")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Select("<script>SELECT * FROM comment WHERE 1=1 <if test='status != null'> AND status = #{status} </if> ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}</script>")
    List<Comment> findAll(@Param("status") Integer status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    
    @Select("<script>SELECT COUNT(*) FROM comment WHERE 1=1 <if test='status != null'> AND status = #{status} </if></script>")
    Long countAll(@Param("status") Integer status);
    
    @Select("SELECT * FROM comment WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM comment WHERE parent_id = #{parentId} ORDER BY create_time ASC")
    List<Comment> findByParentId(@Param("parentId") Long parentId);
    
    @Insert("INSERT INTO comment(content, user_id, article_id, parent_id, reply_to_id, like_count, status, create_time) VALUES(#{content}, #{userId}, #{articleId}, #{parentId}, #{replyToId}, #{likeCount}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);
    
    @Delete("DELETE FROM comment WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Update("UPDATE comment SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE comment SET like_count = like_count + #{delta} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);
}
