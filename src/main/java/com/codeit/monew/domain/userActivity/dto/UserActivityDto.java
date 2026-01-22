package com.codeit.monew.domain.userActivity.dto;

import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.interest.dto.response.InterestSubscriptionResponse;

import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID id,
        String email,
        String nickname,
        String createdAt,
        List<InterestSubscriptionResponse> subscriptions,
        List<UserActivityCommentDto> comments,
        List<UserActivityCommentLikeDto> commentLikes,
        List<ArticleViewDto> articleViews
) {



}
