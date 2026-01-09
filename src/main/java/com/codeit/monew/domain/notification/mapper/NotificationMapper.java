package com.codeit.monew.domain.notification.mapper;

import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.Notification;


public class NotificationMapper {
    //순수 메퍼목적 인스턴스 방지
    private NotificationMapper(){}

    public static NotificationDto toDto(Notification notification) {

        if (notification == null) {
            return null;
        }

        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getConfirmed(),
                notification.getUserId(),
                notification.getContent(),
                notification.getResourceType(),
                notification.getResourceId()
        );
    }
}
