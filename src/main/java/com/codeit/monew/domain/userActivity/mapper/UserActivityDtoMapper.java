package com.codeit.monew.domain.userActivity.mapper;

import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.entity.UserActivity;
import org.springframework.stereotype.Component;

@Component
public class UserActivityDtoMapper {
    public UserActivityDto toDto(UserActivity entity) {
        return new UserActivityDto(
                entity.getUserId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getCreatedAt(),
                entity.getSubscriptions(),
                entity.getComments(),
                entity.getCommentLikes(),
                entity.getArticleViews()
        );
    }
}
