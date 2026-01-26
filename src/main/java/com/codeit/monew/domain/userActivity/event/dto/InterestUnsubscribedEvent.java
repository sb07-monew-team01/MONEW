package com.codeit.monew.domain.userActivity.event.dto;

import java.util.UUID;

public record InterestUnsubscribedEvent(
        UUID userId,
        UUID interestId
) {
}
