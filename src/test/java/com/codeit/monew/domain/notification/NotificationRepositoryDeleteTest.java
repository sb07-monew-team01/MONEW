package com.codeit.monew.domain.notification;

import com.codeit.monew.domain.notification.entity.Notification;

import com.codeit.monew.domain.notification.repository.NotificationRepository;


import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;



import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestJpaAuditing.class ,TestQueryDslConfig.class})
public class NotificationRepositoryDeleteTest {

    @Autowired
    NotificationRepository repository;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("물리삭제")
    class HardDelete {

        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now().minusDays(7);
        List<Notification> notifications;

        @BeforeEach
        void setUp() {
            notifications = List.of(
                    Notification.forCommentLike(userId, resourceId, "사람이름"),
                    Notification.forCommentLike(userId, resourceId, "사람이름3"),
                    Notification.forCommentLike(userId, resourceId, "사람이름4"),
                    Notification.forCommentLike(userId, resourceId, "사람이름5"),
                    Notification.forCommentLike(userId, resourceId, "사람이름6")
            );
        }
        @Test
        @DisplayName("논리삭제가 안된 데이터가(confirm값이 false) 7일이 지나도 남아있다")
        void confirm_updateAt_7_delete() {

            // given
            repository.saveAll(notifications);

            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(8))
                    .setParameter("ids",List.of(notifications.get(0).getId(), notifications.get(1).getId()))
                    .executeUpdate();
            em.clear();

            // when
            repository.deleteConfirmedBefore(time);

            // then
            List<Notification> remains = repository.findAll();

            assertThat(remains)
                    .extracting(Notification::getConfirmed)
                    .containsOnly(false);

            assertThat(remains).hasSize(5);
        }

        @Test
        @DisplayName("논리삭제가 되었어도(confirm값이 ture)7일이 지나야 삭제가 된다")
        void confirm_true_delete() {

            // given
            ReflectionTestUtils.setField(notifications.get(0), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(1), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(2), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(3), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(4), "confirmed", true);

            repository.saveAll(notifications);

            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(4))
                    .setParameter("ids",List.of(notifications.get(0).getId(), notifications.get(1).getId()))
                    .executeUpdate();
            em.clear();

            // when
            repository.deleteConfirmedBefore(time);

            // then
            List<Notification> remains = repository.findAll();
            assertThat(remains)
                    .extracting(Notification::getUpdatedAt)
                    .allMatch(dt -> dt.isAfter(time) || dt.isEqual(time));
        }

        @Test
        @DisplayName("논리삭제가 되고(confirm값이 ture)7일이 지나야 삭제가 된다")
        void confirm_true_delete2() {

            // given
            ReflectionTestUtils.setField(notifications.get(0), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(1), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(2), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(3), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(4), "confirmed", true);

            repository.saveAll(notifications);

            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(8))
                    .setParameter("ids",List.of(
                            notifications.get(0).getId(),
                            notifications.get(1).getId(),
                            notifications.get(2).getId(),
                            notifications.get(3).getId(),
                            notifications.get(4).getId()))
                    .executeUpdate();
            em.clear();

            // when
            repository.deleteConfirmedBefore(time);

            // then
            List<Notification> remains = repository.findAll();
            assertThat(remains).isEmpty();
        }



    }



}
