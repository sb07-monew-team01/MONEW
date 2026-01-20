package com.codeit.monew.domain.userActivity.dto;

import com.codeit.monew.domain.articleView.dto.response.ArticleViewDto;
import com.codeit.monew.domain.comment.dto.response.CommentDto;
import com.codeit.monew.domain.commentuserlike.dto.CommentUserLikeDto;

import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID id,
        String email,
        String nickname,
        String createdAt,
        List<SubscriptionDto> subscriptions,
        List<CommentDto> comments,
        List<CommentUserLikeDto> commentLikes,
        List<ArticleViewDto> articleViews
) {
}
