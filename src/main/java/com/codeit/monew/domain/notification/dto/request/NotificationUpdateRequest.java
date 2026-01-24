package com.codeit.monew.domain.notification.dto.request;

import java.util.UUID;

public record NotificationUpdateRequest(
        UUID userId,
        UUID notificationId
) {
}
