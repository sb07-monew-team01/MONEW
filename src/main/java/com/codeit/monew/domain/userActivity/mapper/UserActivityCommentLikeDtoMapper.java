package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.repository.CommentUserLikeRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.entity.UserActivityCommentLike;
import org.springframework.stereotype.Component;

@Component
public class UserActivityCommentLikeDtoMapper {
    private final CommentUserLikeRepository commentUserLikeRepository;

    public UserActivityCommentLikeDtoMapper(CommentUserLikeRepository commentUserLikeRepository) {
        this.commentUserLikeRepository = commentUserLikeRepository;
    }

    public UserActivityCommentLike toDto(CommentUserLike commentUserLike) {
        Comment comment = commentUserLike.getComment();
        Article article = comment.getArticle();
        User user = commentUserLike.getUser();
        return new UserActivityCommentLike(
                commentUserLike.getId(),
                commentUserLike.getCreatedAt(),
                comment.getId(),
                article.getId(),
                article.getTitle(),
                user.getId(),
                user.getNickname(),
                comment.getContent(),
                commentUserLikeRepository.countByCommentId(comment.getId()),
                comment.getCreatedAt()
        );
    }
}
