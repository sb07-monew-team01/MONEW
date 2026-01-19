package com.codeit.monew.domain.user.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotAuthorizedException extends UserException {
    public UserNotAuthorizedException(UUID loginId, UUID requestId) {
        super(ErrorCode.USER_NOT_AUTHORIZED, Map.of("loginId", loginId, "requestId", requestId));
    }
}
