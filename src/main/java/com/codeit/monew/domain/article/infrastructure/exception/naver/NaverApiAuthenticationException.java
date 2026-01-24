package com.codeit.monew.domain.article.infrastructure.exception.naver;

public class NaverApiAuthenticationException extends NaverApiException {
    public NaverApiAuthenticationException(String message) {
        super(message);
    }

    public NaverApiAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
