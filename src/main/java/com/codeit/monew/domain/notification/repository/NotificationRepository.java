package com.codeit.monew.domain.notification.repository;

import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> , NotificationRepositoryCustom {

    //영속성 확인 후 -> delete 하고 -> 영속성컨텍스트 비워
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from Notification n
        where n.confirmed = true
          and n.updatedAt < :date
    """)
    void deleteConfirmedBefore(@Param("date") LocalDateTime date);

    List<Notification> findAllByUserId(UUID uuid);

    Long countByUserIdAndConfirmedFalse(UUID userId);


    @Query("""
    SELECT n
    FROM Notification n
    WHERE n.confirmed = false
      AND n.userId = :userId
    ORDER BY n.createdAt ASC, n.id DESC
""")
    Page<Notification> findUnconfirmedByUserId(
            @Param("userId") UUID userId,
            Pageable pageable
    );
}
