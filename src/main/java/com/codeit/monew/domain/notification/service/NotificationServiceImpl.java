package com.codeit.monew.domain.notification.service;

import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.domain.notification.dto.request.*;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.exception.NotificationNotFoundException;
import com.codeit.monew.domain.notification.mapper.NotificationMapper;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    @Override
    public NotificationDto createByInterest(NotificationCreateRequest request) {

        Notification saved = notificationRepository.save(
                Notification.forInterest(
                        request.userId(),
                        request.resourceId(),
                        request.resourceName(),
                        request.resourceCount())
        );

        return NotificationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public List<NotificationDto> createAllByInterest(NotificationCreateRequestList requestList) {

        List<Notification> entities = requestList.notificationCreateRequests().stream()
                .map(request -> Notification.forInterest(
                        request.userId(),
                        request.resourceId(),
                        request.resourceName(),
                        request.resourceCount()
                ))
                .toList();

        List<Notification> saved = notificationRepository.saveAll(entities);

        return saved.stream()
                .map(NotificationMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public NotificationDto createByCommentLike(UUID userId, UUID resourceId, String name) {

        Notification saved = notificationRepository.save(
                Notification.forCommentLike(userId, resourceId, name)
        );

        return NotificationMapper.toDto(saved);
    }

    @Transactional
    @Override
    public NotificationDto update(NotificationUpdateRequest request) {

        Notification notification = notificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new NotificationNotFoundException(request.notificationId()));

        notification.confirm();

        //영속화라 알아서 저장하지만 명시적으로 일단 넣었으
        notificationRepository.save(notification);

        return NotificationMapper.toDto(notification);
    }

    @Transactional
    @Override
    public List<NotificationDto> updateAll(NotificationUpdateAllRequest request) {

        List<Notification> notifications = notificationRepository.findAllByUserId(request.userId());

        notifications.forEach(Notification::confirm);

        notificationRepository.saveAll(notifications);

        return notifications.stream()
                .map(NotificationMapper::toDto)
                .toList();
    }

    //물리삭제
    @Transactional
    @Override
    public void deleteAll() {
        //7일지나면삭제니 작동시간 7일이전이면 삭제
        LocalDateTime date = LocalDateTime.now().minusDays(7);

        notificationRepository.deleteConfirmedBefore(date);
    }


    //그저 형식 에맞게 최대50개 배출
    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationDto> findUnconfirmed(NotificationPageRequest request) {

        Pageable pageable = Pageable.ofSize(request.limit());

        Page<Notification> search = notificationRepository.findUnconfirmedByUserId(request.userid(),pageable);

        List<NotificationDto> pageDtoList = search.getContent()
                .stream()
                .map(NotificationMapper::toDto)
                .toList();

        return new PageResponse<>(pageDtoList, null, null, search.getSize(), search.getTotalElements(), false);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<NotificationDto> findUnconfirmedCustom(NotificationPageRequest request) {

        Slice<Notification> search = notificationRepository.search(request);

        long totalElements = notificationRepository.countByUserIdAndConfirmedFalse(request.userid());

         List<NotificationDto> pageDtoList = search.getContent()
                 .stream()
                 .map(NotificationMapper::toDto)
                 .toList();

         String nextCursor = search.hasNext() ?
                 search.getContent().get(search.getContent().size()-1).getCreatedAt().toString() : null;

         LocalDateTime nextAfter =  nextCursor != null ?
                 LocalDateTime.parse(nextCursor) : null;

        return new PageResponse<>(pageDtoList, nextCursor, nextAfter, search.getSize(), totalElements, search.hasNext());
    }


}
