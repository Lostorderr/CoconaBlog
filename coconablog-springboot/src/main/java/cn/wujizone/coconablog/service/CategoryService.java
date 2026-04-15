package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.dto.CategoryVO;
import cn.wujizone.coconablog.entity.Category;
import cn.wujizone.coconablog.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = categoryMapper.findAllWithArticleCount();
        return categories.stream()
                .map(this::toCategoryVO)
                .collect(Collectors.toList());
    }
    
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        return toCategoryVO(category);
    }
    
    @Transactional
    public CategoryVO createCategory(CategoryVO request) {
        if (categoryMapper.findBySlug(request.getSlug()) != null) {
            throw new RuntimeException("slug已存在");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setSort(request.getSort() != null ? request.getSort() : 0);
        categoryMapper.insert(category);
        return getCategoryById(category.getId());
    }
    
    @Transactional
    public CategoryVO updateCategory(Long id, CategoryVO request) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (request.getName() != null) category.setName(request.getName());
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())) {
            if (categoryMapper.findBySlug(request.getSlug()) != null) {
                throw new RuntimeException("slug已存在");
            }
            category.setSlug(request.getSlug());
        }
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getParentId() != null) category.setParentId(request.getParentId());
        if (request.getSort() != null) category.setSort(request.getSort());
        
        categoryMapper.update(category);
        return getCategoryById(id);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
    
    public CategoryVO toCategoryVO(Category category) {
        if (category == null) return null;
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSlug(category.getSlug());
        vo.setDescription(category.getDescription());
        vo.setParentId(category.getParentId());
        vo.setSort(category.getSort());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }
}
