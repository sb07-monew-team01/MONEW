package com.codeit.monew.domain.article.infrastructure.exception.naver;

public class NaverApiRateLimitException extends NaverApiException {
    public NaverApiRateLimitException(String message) {
        super(message);
    }

    public NaverApiRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
