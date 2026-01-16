package com.codeit.monew.domain.commentuserlike.service;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.exception.CommentNotFoundException;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.mapper.CommentUserLikeMapper;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();

        if (commentUserLikeRepository.findByUserIdAndCommentId(userId, commentId).isPresent()) {
            throw new IllegalArgumentException();
        }

        CommentUserLike like = CommentUserLike.create(user, comment);
        commentUserLikeRepository.save(like);

        long likeCount = commentUserLikeRepository.countByCommentId(comment.getId());
        return CommentUserLikeMapper.toDto(like, likeCount);
    }

}

