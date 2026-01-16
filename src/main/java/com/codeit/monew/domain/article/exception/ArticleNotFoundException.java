package com.codeit.monew.domain.article.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ArticleNotFoundException extends ArticleException {

    public ArticleNotFoundException(UUID articleId) {
        super(ErrorCode.ARTICLE_NOT_FOUND, Map.of("articleId", articleId));
    }
}
