package com.codeit.monew.domain.notification.exception;

import com.codeit.monew.global.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(UUID notificationID) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("notificationID", notificationID));
    }
}
