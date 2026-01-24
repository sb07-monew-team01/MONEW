package com.codeit.monew.domain.article.infrastructure.exception.naver;

import com.codeit.monew.domain.article.infrastructure.exception.ArticleCollectionException;

public class NaverApiException extends ArticleCollectionException {
    public NaverApiException(String message) {
        super(message);
    }

    public NaverApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
