package com.codeit.monew.domain.article.infrastructure.exception.rss;

public class RssFetchException extends RssException {
    public RssFetchException(String message) {
        super(message);
    }

    public RssFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
