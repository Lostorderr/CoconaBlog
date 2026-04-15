package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.TagRequest;
import cn.wujizone.coconablog.dto.TagVO;
import cn.wujizone.coconablog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    @GetMapping
    public Result<PageResult<TagVO>> getTagList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(tagService.getTagList(keyword, page, pageSize));
    }
    
    @GetMapping("/all")
    public Result<List<TagVO>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }
    
    @GetMapping("/{id}")
    public Result<TagVO> getTagById(@PathVariable Long id) {
        return Result.success(tagService.getTagById(id));
    }
    
    @PostMapping
    public Result<TagVO> createTag(@Valid @RequestBody TagRequest request) {
        TagVO vo = new TagVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        return Result.success(tagService.createTag(vo));
    }
    
    @PutMapping("/{id}")
    public Result<TagVO> updateTag(@PathVariable Long id,
                                    @Valid @RequestBody TagRequest request) {
        TagVO vo = new TagVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        return Result.success(tagService.updateTag(id, vo));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
