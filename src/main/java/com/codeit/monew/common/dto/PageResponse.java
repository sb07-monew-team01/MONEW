package com.codeit.monew.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
}
