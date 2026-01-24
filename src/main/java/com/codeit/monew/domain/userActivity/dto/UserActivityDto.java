package com.codeit.monew.domain.userActivity.dto;

import com.codeit.monew.domain.userActivity.entity.UserActivityArticleView;
import com.codeit.monew.domain.userActivity.entity.UserActivityComment;
import com.codeit.monew.domain.userActivity.entity.UserActivityCommentLike;
import com.codeit.monew.domain.userActivity.entity.UserActivityInterestSubscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID userId,
        String email,
        String nickname,
        LocalDateTime createdAt,
        List<UserActivityInterestSubscription>subscriptions,
        List<UserActivityComment> comments,
        List<UserActivityCommentLike> commentLikes,
        List<UserActivityArticleView> articleViews
) {
}
