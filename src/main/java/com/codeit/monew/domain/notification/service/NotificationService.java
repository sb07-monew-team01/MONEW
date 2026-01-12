package com.codeit.monew.domain.notification.service;

import com.codeit.monew.domain.notification.dto.request.NotificationCreateRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationCreateRequestList;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateAllRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateRequest;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDto createByInterest(NotificationCreateRequest request);

    List<NotificationDto> createAllByInterest(NotificationCreateRequestList requestList);

    NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name);

    NotificationDto update(NotificationUpdateRequest request);

    List<NotificationDto> updateAll(NotificationUpdateAllRequest request);

    void deleteAll();

}
