package com.codeit.monew.domain.notification.dto.request;

import java.util.List;

public record NotificationCreateRequestList(
        List<NotificationCreateRequest> notificationCreateRequests
) {
    //불변성
    public static NotificationCreateRequestList of(List<NotificationCreateRequest> notificationCreateRequests) {
        return new NotificationCreateRequestList(List.copyOf(notificationCreateRequests));
    }
}
