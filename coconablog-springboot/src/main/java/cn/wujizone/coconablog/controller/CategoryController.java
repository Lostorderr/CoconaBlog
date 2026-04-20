package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.CategoryRequest;
import cn.wujizone.coconablog.dto.CategoryVO;
import cn.wujizone.coconablog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public Result<List<CategoryVO>> getAllCategories() {
        log.info("获取所有分类");
        return Result.success(categoryService.getAllCategories());
    }
    
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        log.info("获取分类详情, id={}", id);
        return Result.success(categoryService.getCategoryById(id));
    }
    
    @PostMapping
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("创建分类, name={}, slug={}", request.getName(), request.getSlug());
        CategoryVO vo = new CategoryVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        vo.setDescription(request.getDescription());
        vo.setParentId(request.getParentId());
        vo.setSort(request.getSort());
        return Result.success(categoryService.createCategory(vo));
    }
    
    @PutMapping("/{id}")
    public Result<CategoryVO> updateCategory(@PathVariable Long id,
                                              @Valid @RequestBody CategoryRequest request) {
        log.info("更新分类, id={}, name={}, slug={}", id, request.getName(), request.getSlug());
        CategoryVO vo = new CategoryVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        vo.setDescription(request.getDescription());
        vo.setParentId(request.getParentId());
        vo.setSort(request.getSort());
        return Result.success(categoryService.updateCategory(id, vo));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("删除分类, id={}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
