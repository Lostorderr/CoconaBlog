package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.dto.LikeCheckResponse;
import cn.wujizone.coconablog.entity.Like;
import cn.wujizone.coconablog.mapper.ArticleMapper;
import cn.wujizone.coconablog.mapper.CommentMapper;
import cn.wujizone.coconablog.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    
    private final LikeMapper likeMapper;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    
    public LikeCheckResponse checkLike(Long userId, Long targetId, Integer targetType) {
        Like like = likeMapper.findByUserAndTarget(userId, targetId, targetType);
        LikeCheckResponse response = new LikeCheckResponse();
        response.setIsLiked(like != null);
        return response;
    }
    
    @Transactional
    public void like(Long userId, Long targetId, Integer targetType) {
        Like existingLike = likeMapper.findByUserAndTarget(userId, targetId, targetType);
        if (existingLike != null) {
            log.error("点赞失败, 已经点赞过了, userId={}, targetId={}, targetType={}", userId, targetId, targetType);
            throw new RuntimeException("已经点赞过了");
        }
        
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(targetId);
        like.setTargetType(targetType);
        likeMapper.insert(like);
        
        if (targetType == 0) {
            articleMapper.updateLikeCount(targetId, 1);
        } else if (targetType == 1) {
            commentMapper.updateLikeCount(targetId, 1);
        }
    }
    
    @Transactional
    public void unlike(Long userId, Long targetId, Integer targetType) {
        int deleted = likeMapper.delete(userId, targetId, targetType);
        if (deleted == 0) {
            log.error("取消点赞失败, 未点赞, userId={}, targetId={}, targetType={}", userId, targetId, targetType);
            throw new RuntimeException("未点赞");
        }
        
        if (targetType == 0) {
            articleMapper.updateLikeCount(targetId, -1);
        } else if (targetType == 1) {
            commentMapper.updateLikeCount(targetId, -1);
        }
    }
}
