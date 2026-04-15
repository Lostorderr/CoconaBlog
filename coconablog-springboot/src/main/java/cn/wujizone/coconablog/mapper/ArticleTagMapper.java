package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.ArticleTag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ArticleTagMapper {
    
    @Select("SELECT * FROM article_tag WHERE article_id = #{articleId}")
    List<ArticleTag> findByArticleId(@Param("articleId") Long articleId);
    
    @Select("SELECT * FROM article_tag WHERE tag_id = #{tagId}")
    List<ArticleTag> findByTagId(@Param("tagId") Long tagId);
    
    @Insert("INSERT INTO article_tag(article_id, tag_id) VALUES(#{articleId}, #{tagId})")
    int insert(ArticleTag articleTag);
    
    @Insert("<script>" +
            "INSERT INTO article_tag(article_id, tag_id) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.articleId}, #{item.tagId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<ArticleTag> list);
    
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
    
    @Delete("DELETE FROM article_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);
}
