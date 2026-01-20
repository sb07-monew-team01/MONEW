package com.codeit.monew.domain.userActivity.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto(
        UUID id,
        UUID interestId,
        String interestName,
        List<String> interestKeywords,
        int interestSubscriberCount,
        LocalDateTime createdAt
) {
}
