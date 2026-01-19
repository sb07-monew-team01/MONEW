package com.codeit.monew.domain.notification.scheduler;

import com.codeit.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDeleteScheduler {
    private final NotificationService notificationService;


    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldConfirmedNotifications() {
        log.info("Notification confirmedDelete scheduler started");
        notificationService.deleteAll();
        log.info("Notification confirmedDelete scheduler finished");
    }
}
