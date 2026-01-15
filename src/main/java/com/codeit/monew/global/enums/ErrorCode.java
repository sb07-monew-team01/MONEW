package com.codeit.monew.global.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 Bad Request
    // User
    USER_LOGIN_FAILED("아이디와 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_RECENTLY_DELETED("해당 이메일은 최근 삭제된 계정으로 재가입이 제한됩니다.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_DELETED("이미 삭제된 유저입니다.", HttpStatus.BAD_REQUEST),

    // Interest
    INTEREST_NOT_FOUND("해당 관심사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTEREST_NAME_TOO_SIMILAR("이미 유사한 이름의 관심사가 존재합니다.", HttpStatus.CONFLICT),
    INTEREST_KEYWORD_DUPLICATE("같은 관심사 내에 중복 키워드가 존재합니다.", HttpStatus.CONFLICT),
    INTEREST_EMPTY_KEYWORD("관심사에 등록된 키워드가 없습니다.", HttpStatus.BAD_REQUEST),
    INTEREST_NULL_KEYWORD("키워드가 null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    TOO_MANY_KEYWORD("키워드가 10개를 초과합니다.", HttpStatus.BAD_REQUEST),

    // Comment
    COMMENT_EMPTY_CONTENT("댓글 내용이 null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_TOO_LONG("댓글 내용은 500자 이하여야 합니다.", HttpStatus.BAD_REQUEST),
    COMMENT_ALREADY_DELETE("이미 삭제된 댓글입니다.", HttpStatus.CONFLICT),
    COMMENT_NOT_FOUND("해당 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    //Notification
    NOTIFICATION_NOT_FOUND("해당 알림을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

    public final String description;
    public final HttpStatus httpStatus;

    ErrorCode(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
