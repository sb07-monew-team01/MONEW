package com.codeit.monew.domain.notification.service;

import com.codeit.monew.domain.notification.dto.response.NotificationDto;

import java.util.UUID;

public class NotificationServiceImp implements NotificationService {

    @Override
    public NotificationDto createByInterest(UUID userId, UUID resourceId, String resourceName, int resourceCont) {
        return null;
    }

    @Override
    public NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name) {
        return null;
    }
}
