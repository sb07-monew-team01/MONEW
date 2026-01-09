package com.codeit.monew.domain.article.dto.request;


import com.codeit.monew.domain.article.entity.ArticleSource;

import java.time.LocalDateTime;

public record ArticleCreateRequest(
        ArticleSource source,
        String sourceUrl,
        String title,
        LocalDateTime publishDate,
        String summary
) {
}
