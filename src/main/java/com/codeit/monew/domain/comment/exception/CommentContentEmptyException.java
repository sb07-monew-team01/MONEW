package com.codeit.monew.domain.comment.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class CommentContentEmptyException extends CommentException {

    public CommentContentEmptyException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public CommentContentEmptyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
