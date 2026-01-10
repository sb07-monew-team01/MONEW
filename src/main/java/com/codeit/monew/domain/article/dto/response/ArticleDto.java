package com.codeit.monew.domain.article.dto.response;

import java.time.LocalDateTime;

public record ArticleDto (
        String title,
        String summary,
        String source,
        LocalDateTime publishDate
){
}
