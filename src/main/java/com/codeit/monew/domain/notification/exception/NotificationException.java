package com.codeit.monew.domain.notification.exception;

import com.codeit.monew.domain.user.User;
import com.codeit.monew.global.enums.ErrorCode;
import com.codeit.monew.global.exception.MonewException;

import java.util.Map;
import java.util.UUID;

public class NotificationException extends MonewException {

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(NotificationException.class, errorCode, details);
    }

    public NotificationException(UUID id, ErrorCode errorCode) {
        super(NotificationException.class, errorCode);
        details.put("contentId", id);
    }
}
