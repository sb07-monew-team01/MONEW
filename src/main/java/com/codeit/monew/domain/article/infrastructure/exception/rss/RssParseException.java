package com.codeit.monew.domain.article.infrastructure.exception.rss;

public class RssParseException extends RssException{
    public RssParseException(String message) {
        super(message);
    }

    public RssParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
