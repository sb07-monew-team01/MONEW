package com.codeit.monew.domain.article.infrastructure.exception.naver;

public class NaverApiServerException extends NaverApiException{

    public NaverApiServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NaverApiServerException(String message) {
        super(message);
    }
}
