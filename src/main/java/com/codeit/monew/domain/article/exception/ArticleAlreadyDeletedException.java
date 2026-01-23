package com.codeit.monew.domain.article.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ArticleAlreadyDeletedException extends ArticleException {
    public ArticleAlreadyDeletedException(UUID articleId) {
        super(ErrorCode.ARTICLE_ALREADY_DELETED, Map.of("articleId", articleId));
    }
}
