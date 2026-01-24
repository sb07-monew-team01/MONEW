package com.codeit.monew.domain.comment.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class CommentNotFoundException extends CommentException {

    public CommentNotFoundException(ErrorCode errorCode, Map<String, Object> details) { super (errorCode, details); }

    public CommentNotFoundException(ErrorCode errorCode) { super (errorCode); }
}
