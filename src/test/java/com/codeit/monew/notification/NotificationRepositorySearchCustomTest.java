package com.codeit.monew.notification;


import com.codeit.monew.article.repository.TestQueryDslConfig;
import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDslConfig.class)
public class NotificationRepositorySearchCustomTest {

    @Autowired
    NotificationRepository repository;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("물리삭제")
    class HardDelete {
        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        List<Notification> notifications = new ArrayList<>();;

        @BeforeEach
        void setUp() {
            for (int i = 0; i < 60; i++) {
                notifications.add(
                        Notification.forCommentLike(
                                userId,
                                resourceId,
                                "사람이름" + i
                        )
                );
            }
            repository.saveAll(notifications);

            em.flush();

            for (int i = 0; i < 50; i++) {
                UUID id = notifications.get(i).getId();
                LocalDateTime t = LocalDateTime.now().minusDays(8 + i ).minusMinutes(i * 10);

                em.createQuery("update Notification n set n.createdAt = :t where n.id = :id")
                        .setParameter("t", t)
                        .setParameter("id", id)
                        .executeUpdate();

                ReflectionTestUtils.setField(notifications.get(i), "createdAt", t);
            }
        }

        @Test
        @DisplayName("처음요청 시 기본 50개의 요청다음 남은요청이 있으면 값이적절히 들어가며 hasnext가true다")
        void request_null_isHasNext_lastValue_isCorrect() {

            // given
            NotificationPageRequest request = new NotificationPageRequest(null , null, 50, userId);

            // when
            Slice<Notification> search = repository.search(request);
            LocalDateTime firstCreatedAt = notifications.get(0).getCreatedAt();
            LocalDateTime lastCreatedAt = notifications.get(49).getCreatedAt();

            System.out.println(firstCreatedAt);
            System.out.println(lastCreatedAt);
            System.out.println(search.getContent().get(0).getCreatedAt());
            System.out.println(search.getContent().get(49).getCreatedAt());

            // then
            assertThat(search.getContent().get(0).getCreatedAt()).isEqualTo(lastCreatedAt);
            assertThat(search.getContent().get(49).getCreatedAt()).isEqualTo(firstCreatedAt);
            assertThat(search.getSize()).isEqualTo(50);
            assertThat(search.hasNext()).isTrue();


        }


    }
}
