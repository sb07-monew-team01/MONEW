package com.codeit.monew.domain.notification.repository;

import com.codeit.monew.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    void deleteByUpdatedAtBefore(LocalDateTime oneWeek);

    List<Notification> findAllByUserId(UUID uuid);
}
