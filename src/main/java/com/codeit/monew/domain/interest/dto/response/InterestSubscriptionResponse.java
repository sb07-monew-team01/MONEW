package com.codeit.monew.domain.interest.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InterestSubscriptionResponse(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    long interestSubscriberCount,
    LocalDateTime createdAt
){}
