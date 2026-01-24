package com.codeit.monew.domain.notification.repository;

import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.entity.Notification;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {

    Slice<Notification> search(NotificationPageRequest request);
}
