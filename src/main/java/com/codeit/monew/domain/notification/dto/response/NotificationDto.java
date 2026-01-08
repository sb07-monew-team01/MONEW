package com.codeit.monew.domain.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean confirmed,
        UUID userId,
        String content,
        String resourceType,
        UUID resourceId

) {

}
