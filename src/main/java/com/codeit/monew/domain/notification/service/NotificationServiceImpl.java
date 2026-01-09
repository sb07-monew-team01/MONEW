package com.codeit.monew.domain.notification.service;

import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.entity.ResourceType;
import com.codeit.monew.domain.notification.mapper.NotificationMapper;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;


    @Transactional
    @Override
    public NotificationDto createByInterest(UUID userId, UUID resourceId, String resourceName, int resourceCont) {

        Notification saved = notificationRepository.save(
                Notification.forInterest(userId, resourceId, resourceName,resourceCont)
        );

        return NotificationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name) {

        Notification saved = notificationRepository.save(
                Notification.forCommentLike(userId, resourceId, name)
        );

        return NotificationMapper.toDto(saved);
    }
}
