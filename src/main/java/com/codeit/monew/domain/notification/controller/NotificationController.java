package com.codeit.monew.domain.notification.controller;

import com.codeit.monew.domain.notification.controller.doc.NotificationDocs;
import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationPageQuery;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateAllRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateRequest;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.service.NotificationService;
import com.codeit.monew.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationDocs {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationDto>> getNotification(

             @Valid @ModelAttribute NotificationPageQuery request
            , @RequestHeader(value = "Monew-Request-User-ID") UUID userId){

        int limit = request.limit() == null ? 50 : request.limit();

        NotificationPageRequest  pageRequest = new NotificationPageRequest(request.cursor(),request.after(),limit,userId);

        PageResponse<NotificationDto> unconfirmedCustom = notificationService.findUnconfirmedCustom(pageRequest);

        return ResponseEntity.ok().body(unconfirmedCustom);

    }

    @PatchMapping("/{notificationId}")
        public ResponseEntity<NotificationDto> confirmNotification(
                @PathVariable UUID notificationId,
                @RequestHeader(value = "Monew-Request-User-ID") UUID userId){

        NotificationUpdateRequest request = new NotificationUpdateRequest(userId,notificationId);

        NotificationDto update = notificationService.update(request);

        return ResponseEntity.ok().body(update);
    }

    @PatchMapping
    public ResponseEntity<List<NotificationDto>> confirmAllNotification(

            @RequestHeader(value = "Monew-Request-User-ID") UUID userid){

        NotificationUpdateAllRequest request = new NotificationUpdateAllRequest(userid);

        List<NotificationDto> notifications = notificationService.updateAll(request);

        return ResponseEntity.ok().body(notifications);
    }
}
