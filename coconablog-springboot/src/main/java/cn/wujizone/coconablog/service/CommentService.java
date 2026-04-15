package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.common.PageResult;
import cn.wujizone.coconablog.dto.CommentVO;
import cn.wujizone.coconablog.dto.UserVO;
import cn.wujizone.coconablog.entity.Comment;
import cn.wujizone.coconablog.mapper.ArticleMapper;
import cn.wujizone.coconablog.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final UserService userService;
    
    public PageResult<CommentVO> getCommentsByArticleId(Long articleId, Integer page, Integer pageSize,
                                                         String orderBy, String order) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (orderBy == null) orderBy = "create_time";
        if (order == null) order = "desc";
        
        int offset = (page - 1) * pageSize;
        List<Comment> comments = commentMapper.findByArticleId(articleId, null, orderBy, order, offset, pageSize);
        Long total = commentMapper.countByArticleId(articleId);
        
        List<CommentVO> voList = comments.stream()
                .map(c -> toCommentVO(c, true))
                .collect(Collectors.toList());
        
        return new PageResult<>(voList, total, page, pageSize);
    }
    
    @Transactional
    public CommentVO createComment(Long userId, CommentVO request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setArticleId(request.getArticleId());
        comment.setParentId(request.getParentId());
        comment.setReplyToId(request.getReplyToId());
        comment.setStatus(0);
        comment.setLikeCount(0);
        commentMapper.insert(comment);
        
        articleMapper.updateCommentCount(request.getArticleId(), 1);
        
        return toCommentVO(comment, true);
    }
    
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        commentMapper.deleteById(id);
        articleMapper.updateCommentCount(comment.getArticleId(), -1);
    }
    
    @Transactional
    public void updateLikeCount(Long id, Integer delta) {
        commentMapper.updateLikeCount(id, delta);
    }
    
    private CommentVO toCommentVO(Comment comment, boolean withUser) {
        if (comment == null) return null;
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setUserId(comment.getUserId());
        vo.setArticleId(comment.getArticleId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToId(comment.getReplyToId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setStatus(comment.getStatus());
        vo.setCreateTime(comment.getCreateTime());
        
        if (withUser) {
            vo.setUser(userService.getUserById(comment.getUserId()));
            if (comment.getReplyToId() != null) {
                vo.setReplyTo(userService.getUserById(comment.getReplyToId()));
            }
        }
        
        return vo;
    }
}
