package com.codeit.monew.domain.notification.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NotificationPageResponse<T>(
        List<T> content,
        String nextCursor,
        UUID nextAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}