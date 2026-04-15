package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.CategoryRequest;
import cn.wujizone.coconablog.dto.CategoryVO;
import cn.wujizone.coconablog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public Result<List<CategoryVO>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }
    
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }
    
    @PostMapping
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryRequest request) {
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
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
