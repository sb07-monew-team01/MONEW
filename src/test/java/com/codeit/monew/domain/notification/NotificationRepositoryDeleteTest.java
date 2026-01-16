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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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



    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    LocalDateTime time = LocalDateTime.now().minusDays(7);
    List<Notification> notifications = new ArrayList<>();


    @BeforeEach
    void setUp() {
        notifications = NotificationDeleteTestFixture.commentNotificationList(
                userId, resourceId, 5
        );
        repository.saveAll(notifications);
        em.flush();
        em.clear();
    }

        @Test
        @DisplayName("논리삭제가 안된 데이터가(confirm값이 false) 7일이 지나도 남아있다")
        void confirm_updateAt_7_delete() {

            // given
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

    @Nested
    @DisplayName("논리삭제가 되었다")
    class HardDelete {

        @BeforeEach
        void confirmAll() {
            NotificationDeleteTestFixture.confirmAll(notifications);
            repository.saveAll(notifications);
            em.flush();
            em.clear();
        }


        @Test
        @DisplayName("논리삭제가 되었어도(confirm값이 ture)7일이 지나야 삭제가 된다")
        void confirm_true_delete() {

            // given
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
            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(8))
                    .setParameter("ids",
                           notifications.stream().map(Notification::getId).toList()
                    )
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
