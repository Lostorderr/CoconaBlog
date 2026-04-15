package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Tag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TagMapper {
    
    @Select("SELECT * FROM tag WHERE id = #{id}")
    Tag findById(@Param("id") Long id);
    
    @Select("SELECT * FROM tag WHERE slug = #{slug}")
    Tag findBySlug(@Param("slug") String slug);
    
    @Select("SELECT * FROM tag ORDER BY create_time DESC")
    List<Tag> findAll();
    
    @Select("<script>" +
            "SELECT * FROM tag WHERE 1=1 " +
            "<if test='keyword != null'> AND (name LIKE CONCAT('%',#{keyword},'%') OR slug LIKE CONCAT('%',#{keyword},'%')) </if>" +
            " ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Tag> findByKeyword(@Param("keyword") String keyword,
                            @Param("offset") Integer offset,
                            @Param("pageSize") Integer pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM tag WHERE 1=1 " +
            "<if test='keyword != null'> AND (name LIKE CONCAT('%',#{keyword},'%') OR slug LIKE CONCAT('%',#{keyword},'%')) </if>" +
            "</script>")
    Long countByKeyword(@Param("keyword") String keyword);
    
    @Select("SELECT t.*, COUNT(at.article_id) as article_count FROM tag t " +
            "LEFT JOIN article_tag at ON t.id = at.tag_id " +
            "LEFT JOIN article a ON at.article_id = a.id AND a.status = 1 " +
            "GROUP BY t.id ORDER BY t.create_time DESC")
    List<Tag> findAllWithArticleCount();
    
    @Select("SELECT t.* FROM tag t " +
            "INNER JOIN article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId}")
    List<Tag> findByArticleId(@Param("articleId") Long articleId);
    
    @Insert("INSERT INTO tag(name, slug, create_time) VALUES(#{name}, #{slug}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);
    
    @Update("UPDATE tag SET name=#{name}, slug=#{slug} WHERE id=#{id}")
    int update(Tag tag);
    
    @Delete("DELETE FROM tag WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
