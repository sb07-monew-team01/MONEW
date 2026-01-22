package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.articleView.dto.mapper.ArticleViewMapper;
import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.interest.dto.response.InterestSubScriptionResponse;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.mapper.InterestSubscriptionMapper;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.UserActivityCommentDto;
import com.codeit.monew.domain.userActivity.dto.UserActivityCommentLikeDto;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserActivityMapper {

    private final InterestSubscriptionMapper subscriptionMapper;
    private final ArticleViewMapper articleViewMapper;
    private final UserActivityCommentDtoMapper commentDtoMapper;
    private final UserActivityCommentLikeDtoMapper commentUserLikeMapper;

    // 다바꿔
    public UserActivityDto toDto(
            User user,
            List<Interest> subscriptions,
            List<Comment> comments,
            List<CommentUserLike> commentLikes,
            List<ArticleView> articleViews
    ) {
        return new UserActivityDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt().toString(),
                toSubscriptionDtos(user, subscriptions),
                toCommentDtos(comments),
                toCommentUserLikeDtos(commentLikes),
                toArticleViewDtos(articleViews)
        );
    }

    private List<InterestSubScriptionResponse> toSubscriptionDtos(User user, List<Interest> interests) {
        return interests.stream()
                .map(i -> subscriptionMapper.toDto(i, new InterestUser(user, i)))
                .collect(Collectors.toList());
    }

    private List<UserActivityCommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(commentDtoMapper::toDto)
                .toList();
    }

    private List<UserActivityCommentLikeDto> toCommentUserLikeDtos(List<CommentUserLike> commentLikes) {
        return commentLikes.stream()
                .map(commentUserLikeMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<ArticleViewDto> toArticleViewDtos(List<ArticleView> articleViews) {
        return articleViews.stream()
                .map(articleViewMapper::toDto)
                .collect(Collectors.toList());
    }
}
