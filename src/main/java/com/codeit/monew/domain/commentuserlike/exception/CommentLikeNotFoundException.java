package com.codeit.monew.domain.commentuserlike.exception;

import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;

public class CommentLikeNotFoundException extends CommentUserLikeException {
    public CommentLikeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentLikeNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
