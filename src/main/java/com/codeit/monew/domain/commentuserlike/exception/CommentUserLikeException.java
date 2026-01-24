package com.codeit.monew.domain.commentuserlike.exception;

import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class CommentUserLikeException extends MonewException {

    public CommentUserLikeException(ErrorCode errorCode) {
        super(CommentUserLikeException.class, errorCode);
    }

    public CommentUserLikeException(ErrorCode errorCode, Map<String, Object> details) {
        super(CommentUserLikeException.class, errorCode, details);
    }
}
