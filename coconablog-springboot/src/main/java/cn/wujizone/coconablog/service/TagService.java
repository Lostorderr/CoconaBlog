package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.dto.TagVO;
import cn.wujizone.coconablog.entity.Tag;
import cn.wujizone.coconablog.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    
    private final TagMapper tagMapper;
    
    public PageResult<TagVO> getTagList(String keyword, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        List<Tag> tags = tagMapper.findByKeyword(keyword, offset, pageSize);
        Long total = tagMapper.countByKeyword(keyword);
        
        List<TagVO> voList = tags.stream()
                .map(this::toTagVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, pageSize);
    }
    
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagMapper.findAllWithArticleCount();
        return tags.stream()
                .map(this::toTagVO)
                .collect(Collectors.toList());
    }
    
    public TagVO getTagById(Long id) {
        Tag tag = tagMapper.findById(id);
        return toTagVO(tag);
    }
    
    @Transactional
    public TagVO createTag(TagVO request) {
        if (tagMapper.findBySlug(request.getSlug()) != null) {
            log.error("创建标签失败, slug已存在, slug={}", request.getSlug());
            throw new RuntimeException("slug已存在");
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setSlug(request.getSlug());
        tagMapper.insert(tag);
        return getTagById(tag.getId());
    }
    
    @Transactional
    public TagVO updateTag(Long id, TagVO request) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            log.error("更新标签失败, 标签不存在, id={}", id);
            throw new RuntimeException("标签不存在");
        }
        if (request.getName() != null) tag.setName(request.getName());
        if (request.getSlug() != null && !request.getSlug().equals(tag.getSlug())) {
            if (tagMapper.findBySlug(request.getSlug()) != null) {
                log.error("更新标签失败, slug已存在, slug={}", request.getSlug());
                throw new RuntimeException("slug已存在");
            }
            tag.setSlug(request.getSlug());
        }
        tagMapper.update(tag);
        return getTagById(id);
    }
    
    @Transactional
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }
    
    public TagVO toTagVO(Tag tag) {
        if (tag == null) return null;
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug());
        vo.setCreateTime(tag.getCreateTime());
        return vo;
    }
}
