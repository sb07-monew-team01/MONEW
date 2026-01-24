package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ArticleSearchRequest(
        @Schema(description = "검색어(제목, 요약)", example = "스포츠")
        String keyword,
        @Schema(description = "관심사 ID", example = "111c88f3-c4f1-4817-a08d-a385daf60000")
        UUID interestId,
        @Schema(description = "출처(포함)")
        List<ArticleSource> sourceIn,
        @Schema(description = "날짜 시작(부터)", example = "2026-01-01T00:00:00")
        LocalDateTime publishDateFrom,
        @Schema(description = "날짜 끝(까지)", example = "2026-01-31T23:59:59")
        LocalDateTime publishDateTo,
        @Schema(description = "정렬 기준", allowableValues = {"publishDate", "commentCount", "viewCount"}, defaultValue = "publishDate", requiredMode = Schema.RequiredMode.REQUIRED)
        String orderBy,
        @Schema(description = "정렬 방향", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC", requiredMode = Schema.RequiredMode.REQUIRED)
        String direction,
        @Schema(description = "커서 값")
        String cursor,
        @Schema(description ="보조 커서(생성일자), 동일 정렬 값 내 순서 보장용", example = "2026-01-01T00:00:00")
        LocalDateTime after,
        @Schema(description = "커서 페이지 크기", example = "10")
        Integer limit
) {
    public ArticleSearchRequest {
        if (orderBy == null) orderBy = "publishDate";
        if (direction == null) direction = "DESC";
        if (limit == null) limit = 10;
    }
}
