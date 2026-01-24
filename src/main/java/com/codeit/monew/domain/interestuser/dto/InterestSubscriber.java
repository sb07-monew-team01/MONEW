package com.codeit.monew.domain.interestuser.dto;

import java.util.UUID;

public record InterestSubscriber(
        UUID interestId,
        UUID userId
) {}
