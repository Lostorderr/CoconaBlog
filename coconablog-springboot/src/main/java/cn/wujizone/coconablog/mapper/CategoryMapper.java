package cn.wujizone.coconablog.mapper;

import cn.wujizone.coconablog.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(@Param("id") Long id);
    
    @Select("SELECT * FROM category WHERE slug = #{slug}")
    Category findBySlug(@Param("slug") String slug);
    
    @Select("SELECT * FROM category ORDER BY sort ASC")
    List<Category> findAll();
    
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} ORDER BY sort ASC")
    List<Category> findByParentId(@Param("parentId") Long parentId);
    
    @Select("SELECT c.*, COUNT(a.id) as article_count FROM category c " +
            "LEFT JOIN article a ON c.id = a.category_id AND a.status = 1 " +
            "GROUP BY c.id ORDER BY c.sort ASC")
    List<Category> findAllWithArticleCount();
    
    @Insert("INSERT INTO category(name, slug, description, parent_id, sort, create_time) " +
            "VALUES(#{name}, #{slug}, #{description}, #{parentId}, #{sort}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    @Update("UPDATE category SET name=#{name}, slug=#{slug}, description=#{description}, " +
            "parent_id=#{parentId}, sort=#{sort} WHERE id=#{id}")
    int update(Category category);
    
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
