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
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;


    @Override
    public NotificationDto createByInterest(UUID userId, UUID resourceId, String resourceName, int resourceCont) {

        String content = String.format("%s와 관련된 기사가 %d건 등록되었습니다.", resourceName, resourceCont);

        Notification notification = new Notification(userId,resourceId, ResourceType.INTEREST,content);



        NotificationDto notificationDto = new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getConfirmed(),
                notification.getUserId(),
                notification.getContent(),
                notification.getResourceType(),
                notification.getResourceId()
        );

        notificationRepository.save(notification);

        return notificationDto;
    }

    @Override
    public NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name) {

        String content = String.format("%s님이 나의 댓글을 좋아합니다.", name);

        Notification notification = new Notification(userId,resourceId, ResourceType.COMMENT,content);


        NotificationDto notificationDto = new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getConfirmed(),
                notification.getUserId(),
                notification.getContent(),
                notification.getResourceType(),
                notification.getResourceId()
        );

        notificationRepository.save(notification);

        return notificationDto;
    }
}
