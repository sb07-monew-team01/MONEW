package com.codeit.monew.domain.notification.service;

import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.entity.ResourceType;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;


    @Override
    public NotificationDto createByInterest(UUID userId, UUID resourceId, String resourceName, int resourceCont) {

        String content = String.format("%s와 관련된 기사가 %d건 등록되었습니다.", resourceName, resourceCont);

        Notification notification = new Notification(userId,resourceId, ResourceType.INTEREST,content);

        Notification saved = notificationRepository.save(notification);

        NotificationDto notificationDto = new NotificationDto(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getConfirmed(),
                saved.getUserId(),
                saved.getContent(),
                saved.getResourceType(),
                saved.getResourceId()
        );

        return notificationDto;
    }

    @Override
    public NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name) {

        String content = String.format("%s님이 나의 댓글을 좋아합니다.", name);

        Notification notification = new Notification(userId,resourceId, ResourceType.COMMENT,content);

        Notification saved = notificationRepository.save(notification);

        NotificationDto notificationDto = new NotificationDto(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getConfirmed(),
                saved.getUserId(),
                saved.getContent(),
                saved.getResourceType(),
                saved.getResourceId()
        );

        return notificationDto;
    }
}
