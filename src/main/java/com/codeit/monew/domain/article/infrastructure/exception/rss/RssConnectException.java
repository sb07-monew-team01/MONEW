package com.codeit.monew.domain.article.infrastructure.exception.rss;

public class RssConnectException extends RssException {
    public RssConnectException(String message) {
        super(message);
    }

    public RssConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
