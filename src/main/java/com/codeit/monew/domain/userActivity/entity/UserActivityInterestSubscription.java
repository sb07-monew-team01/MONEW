package com.codeit.monew.domain.userActivity.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserActivityInterestSubscription(
        UUID id,
        UUID interestId,
        String interestName,
        List<String> interestKeywords,
        long interestSubscriberCount,
        LocalDateTime createdAt
) {
}
