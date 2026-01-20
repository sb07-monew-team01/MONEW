package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.articleView.dto.mapper.ArticleViewMapper;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.comment.mapper.CommentMapper;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.commentuserlike.mapper.CommentUserLikeMapper;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.SubscriptionDto;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserActivityMapper {

    private final SubscriptionMapper subscriptionMapper;
    private final ArticleViewMapper articleViewMapper;

    public UserActivityDto toDto(
            User user,
            List<InterestUser> interestUsers,
            List<Comment> comments,
            List<CommentUserLike> commentLikes,
            List<ArticleView> articleViews
    ) {
        return new UserActivityDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt().toString(),
                toSubscriptionDtos(interestUsers),
                toCommentDtos(comments),
                toCommentUserLikeDtos(commentLikes),
                toArticleViewDtos(articleViews)
        );
    }

    private List<SubscriptionDto> toSubscriptionDtos(List<InterestUser> interestUsers) {
        return interestUsers.stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<CommentUserLikeDto> toCommentUserLikeDtos(List<CommentUserLike> commentLikes) {
        return commentLikes.stream()
                .map(like -> CommentUserLikeMapper.toDto(like, 0L))
                .collect(Collectors.toList());
    }

    private List<ArticleViewDto> toArticleViewDtos(List<ArticleView> articleViews) {
        return articleViews.stream()
                .map(articleViewMapper::toDto)
                .collect(Collectors.toList());
    }
}
