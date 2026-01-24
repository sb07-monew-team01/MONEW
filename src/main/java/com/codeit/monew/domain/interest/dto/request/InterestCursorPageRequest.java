package com.codeit.monew.domain.interest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public record InterestCursorPageRequest(
        @Schema(description = "정렬 기준", allowableValues = {"name", "subscriberCount"}, defaultValue = "name", requiredMode = Schema.RequiredMode.REQUIRED)
        String orderBy,
        @Schema(description = "정렬 방향", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC", requiredMode = Schema.RequiredMode.REQUIRED)
        String direction,
        @Schema(description = "커서 값")
        String cursor,
        @Schema(description ="보조 커서(생성일자), 동일 정렬 값 내 순서 보장용", example = "2026-01-01T00:00:00")
        LocalDateTime after,
        @Schema(description = "커서 페이지 크기", example = "6")
        Integer limit,
        @Schema(description = "검색어(관심사 이름, 키워드)", example = "스포츠")
        String keyword
) {
    public InterestCursorPageRequest {
        // 기본값 처리 (문자열 그대로)
        if (orderBy == null) orderBy = "name";
        if (direction == null) direction = "desc";
        if (limit == null) limit = 6;
    }

    public String keywordValue() {
        return StringUtils.hasText(keyword) ? keyword.trim() : null;
    }
}
