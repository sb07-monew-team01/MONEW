package com.codeit.monew.global.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    ALREADY_EXISTS("이미 존재하는 값입니다.", HttpStatus.BAD_REQUEST),
    USER_LOGIN_FAILED("아이디와 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // Interest
    INTEREST_NOT_FOUND("해당 관심사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTEREST_NAME_TOO_SIMILAR("이미 유사한 이름의 관심사가 존재합니다.", HttpStatus.CONFLICT),
    INTEREST_KEYWORD_DUPLICATE("같은 관심사 내에 중복 키워드가 존재합니다.", HttpStatus.CONFLICT),
    INTEREST_EMPTY_KEYWORD("관심사에 등록된 키워드가 없습니다.", HttpStatus.BAD_REQUEST),
    INTEREST_NULL_KEYWORD("키워드가 null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    TOO_MANY_KEYWORD("키워드가 10개를 초과합니다.", HttpStatus.BAD_REQUEST),

    COMMENT_EMPTY_CONTENT("댓글 내용이 null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_TOO_LONG("댓글 내용은 500자 이하여야 합니다.", HttpStatus.BAD_REQUEST),

    //Notification
    NOTIFICATION_NOT_FOUND("해당 알림을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

    public final String description;
    public final HttpStatus httpStatus;

    ErrorCode(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
