package com.codeit.monew.domain.comment.exception;

import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class CommentAlreadyDeleteException extends CommentException {

    public CommentAlreadyDeleteException(ErrorCode errorCode, Map<String, Object> details) { super(errorCode, details); }

    public CommentAlreadyDeleteException(ErrorCode errorCode) { super(errorCode); }
}
