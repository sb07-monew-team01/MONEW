package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.userActivity.dto.UserActivityCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserActivityCommentDtoMapper {
    private final CommentUserLikeRepository repository;

    public UserActivityCommentDto toDto(Comment comment) {
        return new UserActivityCommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getArticle().getTitle(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                repository.countByCommentId(comment.getId()),
                comment.getCreatedAt()
        );
    }
}
