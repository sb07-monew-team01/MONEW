package com.codeit.monew.domain.comment.exception;

import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class CommentException extends MonewException {

    public CommentException(ErrorCode errorCode, Map<String, Object> details) {
        super(Comment.class, errorCode, details);
    }

    public CommentException(ErrorCode errorCode) { super(Comment.class, errorCode); }
}
