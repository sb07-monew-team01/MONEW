package com.codeit.monew.domain.notification.service;

import com.codeit.monew.domain.notification.dto.response.NotificationDto;

import java.util.UUID;

public interface NotificationService {

    NotificationDto createByInterest(UUID userId,UUID resourceId,String resourceName,int resourceCont);

    NotificationDto createByCommentLike(UUID userId,UUID resourceId,String name);

}
