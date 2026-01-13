package com.codeit.monew.domain.article.infrastructure.exception;

public class ArticleCollectionException extends RuntimeException {
    public ArticleCollectionException(String message) {
        super(message);
    }

    public ArticleCollectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
