package com.codeit.monew.domain.commentuserlike.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class CommentUserLikeNotFoundException extends CommentUserLikeException {
    public CommentUserLikeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentUserLikeNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
