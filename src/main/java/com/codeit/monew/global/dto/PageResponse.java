package com.codeit.monew.global.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PageResponse<T>(
    List<T> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
}
