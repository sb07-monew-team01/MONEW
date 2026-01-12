package com.codeit.monew.domain.user.exception;

import com.codeit.monew.domain.user.User;
import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;

public class UserAlreadyDeletedException extends UserException {
    public UserAlreadyDeletedException(User user) {
        super(ErrorCode.USER_ALREADY_DELETED, Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "deletedAt", user.getDeletedAt(),
                "message", "같은 이메일이 삭제된 이후 일주일이 경과해야 동일한 이메일로 계정 생성이 가능합니다."));
    }
}
