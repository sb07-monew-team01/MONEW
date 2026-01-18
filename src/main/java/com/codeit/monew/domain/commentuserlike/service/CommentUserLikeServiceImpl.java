package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.mapper.CommentUserLikeMapper;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentUserLikeServiceImpl implements CommentUserLikeService {
    private final CommentUserLikeRepository commentUserLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public CommentUserLikeDto like(UUID userId, UUID commentId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        Optional<CommentUserLike> existingLike =
                commentUserLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingLike.isPresent()) {
            commentUserLikeRepository.delete(existingLike.get());
            return null; // 취소는 반환 없이 걍 삭제
        }

        CommentUserLike like = CommentUserLike.create(user, comment);
        commentUserLikeRepository.save(like);

        long likeCount = commentUserLikeRepository.countByCommentId(commentId);
        return CommentUserLikeMapper.toDto(like, likeCount);
    }
}