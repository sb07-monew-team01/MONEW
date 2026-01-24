package com.codeit.monew.domain.userActivity.event;

import java.util.UUID;

public record InterestUnsubscribedEvent(
        UUID userId,
        UUID interestId
) {
}
