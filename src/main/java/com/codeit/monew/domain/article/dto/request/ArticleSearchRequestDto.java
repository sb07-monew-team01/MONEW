package com.codeit.monew.domain.article.dto.request;

import com.codeit.monew.domain.article.entity.ArticleOrderBy;
import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ArticleSearchRequestDto(
        String keyword,                 // 검색어
        UUID interestId,                // 관심사Id
        List<ArticleSource> sourceIn,   // 출처

        LocalDate publishDateFrom,      // 날짜 시작
        LocalDate publishDateTo,        // 날짜 끝

        ArticleOrderBy orderBy,         // 정렬 속성 이름 (publishDate, commentCount, viewCount)
        String direction                // 정렬 방향 (ASC, DESC)
) {
}
