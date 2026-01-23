package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.exception.CommentAlreadyLikedException;
import com.codeit.monew.domain.commentuserlike.exception.CommentUserLikeNotFoundException;
import com.codeit.monew.domain.commentuserlike.mapper.CommentUserLikeMapper;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.notification.service.NotificationService;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentUserLikeServiceImpl implements CommentUserLikeService {
    private final CommentUserLikeRepository commentUserLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public CommentUserLikeDto like(UUID userId, UUID commentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentUserLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new CommentAlreadyLikedException(ErrorCode.COMMENT_ALREADY_LIKED);
        }

        CommentUserLike like = CommentUserLike.create(user, comment);
        commentUserLikeRepository.save(like);

        notificationService.createByCommentLike(
                comment.getUser().getId(),
                comment.getId(),
                user.getNickname()
        );

        long likeCount = commentUserLikeRepository.countByCommentId(commentId);
        return CommentUserLikeMapper.toDto(like, likeCount);
    }

    @Transactional
    @Override
    public void unlike(UUID userId, UUID commentId) {
        commentUserLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
    }

}