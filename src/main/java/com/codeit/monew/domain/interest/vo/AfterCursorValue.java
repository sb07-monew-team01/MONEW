package com.codeit.monew.domain.interest.vo;

import java.time.LocalDateTime;
import java.util.UUID;

public record AfterCursorValue(
    LocalDateTime createdAt,
    UUID id
) {}