package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Article;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ArticleMapper {
    
    @Select("SELECT * FROM article WHERE id = #{id}")
    Article findById(@Param("id") Long id);
    
    @Select("SELECT * FROM article WHERE slug = #{slug}")
    Article findBySlug(@Param("slug") String slug);
    
    @Select("<script>" +
            "SELECT * FROM article WHERE 1=1 " +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='status == null'> AND status != 2 </if>" +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "<if test='tagId != null'> AND id IN (SELECT article_id FROM article_tag WHERE tag_id = #{tagId}) </if>" +
            "<if test='keyword != null'> AND (title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%')) </if>" +
            "<if test='userId != null'> AND user_id = #{userId} </if>" +
            " ORDER BY is_top DESC, create_time DESC " +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Article> findByCondition(@Param("categoryId") Long categoryId,
                                   @Param("tagId") Long tagId,
                                   @Param("keyword") String keyword,
                                   @Param("status") Integer status,
                                   @Param("userId") Long userId,
                                   @Param("offset") Integer offset,
                                   @Param("pageSize") Integer pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM article WHERE 1=1 " +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='status == null'> AND status != 2 </if>" +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "<if test='tagId != null'> AND id IN (SELECT article_id FROM article_tag WHERE tag_id = #{tagId}) </if>" +
            "<if test='keyword != null'> AND (title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%')) </if>" +
            "<if test='userId != null'> AND user_id = #{userId} </if>" +
            "</script>")
    Long countByCondition(@Param("categoryId") Long categoryId,
                          @Param("tagId") Long tagId,
                          @Param("keyword") String keyword,
                          @Param("status") Integer status,
                          @Param("userId") Long userId);
    
    @Select("SELECT * FROM article WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Article> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM article WHERE category_id = #{categoryId} ORDER BY create_time DESC")
    List<Article> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Insert("INSERT INTO article(title, slug, summary, content, cover_image, user_id, category_id, status, " +
            "view_count, like_count, comment_count, is_top, publish_time, create_time, update_time) " +
            "VALUES(#{title}, #{slug}, #{summary}, #{content}, #{coverImage}, #{userId}, #{categoryId}, #{status}, " +
            "#{viewCount}, #{likeCount}, #{commentCount}, #{isTop}, #{publishTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);
    
    @Update("UPDATE article SET title=#{title}, slug=#{slug}, summary=#{summary}, content=#{content}, " +
            "cover_image=#{coverImage}, category_id=#{categoryId}, status=#{status}, is_top=#{isTop}, " +
            "update_time=NOW() WHERE id=#{id}")
    int update(Article article);
    
    @Update("UPDATE article SET status = #{status}, update_time=NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE article SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE article SET like_count = like_count + #{delta} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("delta") Integer delta);
    
    @Update("UPDATE article SET comment_count = comment_count + #{delta} WHERE id = #{id}")
    int updateCommentCount(@Param("id") Long id, @Param("delta") Integer delta);
    
    @Delete("DELETE FROM article WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
