package com.codeit.monew.domain.notification.dto.request;

import java.util.UUID;

public record NotificationUpdateAllRequest(
        UUID userId
) {
}
