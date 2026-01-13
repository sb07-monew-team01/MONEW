package com.codeit.monew.domain.notification.dto.request;

import java.util.UUID;

public record NotificationCreateRequest(
        UUID userId,
        UUID resourceId,
        String resourceName,
        int resourceCount
) {
    public static NotificationCreateRequest of(UUID userId, UUID resourceId, String resourceName, int resourceCount) {
        return new NotificationCreateRequest(userId, resourceId, resourceName, resourceCount);
    }
}
