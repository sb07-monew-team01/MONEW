package com.codeit.monew.domain.commentuserlike.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class CommentAlreadyLikedException extends CommentUserLikeException {
    public CommentAlreadyLikedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentAlreadyLikedException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

}