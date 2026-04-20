package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.dto.TagRequest;
import cn.wujizone.coconablog.dto.TagVO;
import cn.wujizone.coconablog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.info("获取标签列表, keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        return Result.success(tagService.getTagList(keyword, page, pageSize));
    }
    
    @GetMapping("/all")
    public Result<List<TagVO>> getAllTags() {
        log.info("获取所有标签");
        return Result.success(tagService.getAllTags());
    }
    
    @GetMapping("/{id}")
    public Result<TagVO> getTagById(@PathVariable Long id) {
        log.info("获取标签详情, id={}", id);
        return Result.success(tagService.getTagById(id));
    }
    
    @PostMapping
    public Result<TagVO> createTag(@Valid @RequestBody TagRequest request) {
        log.info("创建标签, name={}, slug={}", request.getName(), request.getSlug());
        TagVO vo = new TagVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        return Result.success(tagService.createTag(vo));
    }
    
    @PutMapping("/{id}")
    public Result<TagVO> updateTag(@PathVariable Long id,
                                    @Valid @RequestBody TagRequest request) {
        log.info("更新标签, id={}, name={}, slug={}", id, request.getName(), request.getSlug());
        TagVO vo = new TagVO();
        vo.setName(request.getName());
        vo.setSlug(request.getSlug());
        return Result.success(tagService.updateTag(id, vo));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        log.info("删除标签, id={}", id);
        tagService.deleteTag(id);
        return Result.success();
    }
}
