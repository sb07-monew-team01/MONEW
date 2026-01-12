package com.codeit.monew.global.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    ALREADY_EXISTS("이미 존재하는 값입니다.", HttpStatus.BAD_REQUEST),
    USER_LOGIN_FAILED("아이디와 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    //Notification
    NOTIFICATION_NOT_FOUND("해당 알림을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

    public final String description;
    public final HttpStatus httpStatus;

    ErrorCode(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
