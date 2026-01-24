package com.codeit.monew.domain.user.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {
        public UserNotFoundException(String email) {
            super(ErrorCode.USER_NOT_FOUND, Map.of("email", email));
        }

    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }
}
