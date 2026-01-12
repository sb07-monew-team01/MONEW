package com.codeit.monew.domain.article.infrastructure.naver.dto;

import java.util.List;

public record NaverApiResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<NaverItem> items
) {
}
