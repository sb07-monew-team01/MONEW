package com.codeit.monew.domain.article.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleDto (
        UUID id,
        String source,
        String sourceUrl,
        String title,
        LocalDateTime publishDate,
        String summary,
        Long commentCount,
        Long viewCount,
        Boolean viewedByMe
){
}
