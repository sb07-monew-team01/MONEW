package com.codeit.monew.domain.comment.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class CommentContentTooLongException extends CommentException {

    public CommentContentTooLongException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public CommentContentTooLongException(ErrorCode errorCode) {
        super(errorCode);
    }
}
