package com.codeit.monew.domain.article.infrastructure.exception.rss;

import com.codeit.monew.domain.article.infrastructure.exception.ArticleCollectionException;

public class RssException extends ArticleCollectionException {
    public RssException(String message) {
        super(message);
    }

    public RssException(String message, Throwable cause) {
        super(message, cause);
    }
}
