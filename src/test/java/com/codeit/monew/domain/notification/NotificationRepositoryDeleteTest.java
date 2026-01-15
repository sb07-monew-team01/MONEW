package com.codeit.monew.domain.notification;
import com.codeit.monew.global.config.QueryDslConfig;
import com.codeit.monew.domain.notification.entity.Notification;

import com.codeit.monew.domain.notification.repository.NotificationRepository;


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
@Import(QueryDslConfig.class)
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
            //강제로 업데이트 7일 이전놈들 주입 3번과 4번친구
            //인서트된  친구들 강제로값 변경해서 넣어주기  1번,2번친구들 업데이트 날짜 8일전 날짜로
            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(8))
                    .setParameter("ids",List.of(notifications.get(0).getId(), notifications.get(1).getId()))
                    .executeUpdate();
            em.clear();

            // when
            //설정한 7일이전 으로 기준하면
            repository.deleteConfirmedBefore(time);

            // then
            //5개의 정보가 그대로 있어야한다
            List<Notification> remains = repository.findAll();
            //확인을 안한것들 모든것이 false
            assertThat(remains)
                    .extracting(Notification::getConfirmed)
                    .containsOnly(false);
            //날짜만 7일이상이라 모두 생존
            assertThat(remains).hasSize(5);
        }

        @Test
        @DisplayName("논리삭제가 되었어도(confirm값이 ture)7일이 지나야 삭제가 된다")
        void confirm_true_delete() {

            // given
            //모두 논리삭제가 되었다는가정하에
            ReflectionTestUtils.setField(notifications.get(0), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(1), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(2), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(3), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(4), "confirmed", true);

            repository.saveAll(notifications);
            //인서트된  친구들 강제로값 변경해서 넣어주기  1번,2번친구들 업데이트 날짜 4일전 날짜로
            em.flush();
            em.createQuery("update Notification n set n.updatedAt = :t where n.id in :ids")
                    .setParameter("t", LocalDateTime.now().minusDays(4))
                    .setParameter("ids",List.of(notifications.get(0).getId(), notifications.get(1).getId()))
                    .executeUpdate();
            em.clear();

            // when
            //설정한 7일이전 으로 기준하면
            repository.deleteConfirmedBefore(time);

            // then
            //설정값 7일 이후 날짜만 남아있냐
            List<Notification> remains = repository.findAll();
            assertThat(remains)
                    .extracting(Notification::getUpdatedAt)
                    .allMatch(dt -> dt.isAfter(time) || dt.isEqual(time));
        }

        @Test
        @DisplayName("논리삭제가 되고(confirm값이 ture)7일이 지나야 삭제가 된다")
        void confirm_true_delete2() {

            // given
            //모두 논리삭제가 되었다는가정하에
            ReflectionTestUtils.setField(notifications.get(0), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(1), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(2), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(3), "confirmed", true);
            ReflectionTestUtils.setField(notifications.get(4), "confirmed", true);

            repository.saveAll(notifications);
            //인서트된  친구들 강제로값 변경해서 넣어주기  모든친구들 업데이트 날짜 8일전 날짜로
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
            //설정한 7일이전 으로 기준하면
            repository.deleteConfirmedBefore(time);

            // then
            //true와 ,시간이 7일 지난 친구들 모두 삭제
            List<Notification> remains = repository.findAll();
            assertThat(remains).isEmpty();
        }



    }



}
