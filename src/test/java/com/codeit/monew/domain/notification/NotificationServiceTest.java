package com.codeit.monew.domain.notification;




import com.codeit.monew.domain.notification.dto.request.NotificationCreateRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateRequest;
import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.ResourceType;
import com.codeit.monew.domain.notification.exception.NotificationNotFoundException;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import com.codeit.monew.domain.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID resourceId;
    private UUID notificationId;


    @Nested
    @DisplayName("알림 생성")
    class CreateNotification {

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID();
            resourceId = UUID.randomUUID();
        }

        @Test
        @DisplayName("관심사에 의한 알람 생성시 만들어진 dto의 필드값(resourceType,content)확인,Notification객체가 save되는지 확인")
        void create_interest_Notification() {
            // given
            int resourceCont = 4;
            String resourceName = "축구";

            // save 호출 시 null 반환 방지
            when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            NotificationCreateRequest request = NotificationCreateRequest.of(userId, resourceId, resourceName, resourceCont);
            // when
            NotificationDto result =
                    notificationService.createByInterest(request);

            // then
            assertThat(result.resourceType()).isEqualTo(ResourceType.INTEREST);
            assertThat(result.content()).isEqualTo("축구와 관련된 기사가 4건 등록되었습니다.");
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("댓글이 좋아요에 알림 생성 시 만들어진 dto의 필드값(resourceType,content)확인,Notification객체가 save되는지 확인")
        void create_like_Notification() {

            // given
            String name = "좋아요누른사람";

            // save 호출 시 null 반환 방지
            when(notificationRepository.save(any(Notification.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // when
            NotificationDto result = notificationService.createByCommentLike(userId, resourceId, name);

            // then
            assertThat(result.resourceType()).isEqualTo(ResourceType.COMMENT);
            assertThat(result.content()).isEqualTo("좋아요누른사람님이 나의 댓글을 좋아합니다.");
            verify(notificationRepository).save(any(Notification.class));
        }

    }

    @Nested
    @DisplayName("알림 수정(논리삭제)")
    class  UpdateNotification_Logical {

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID();
            resourceId = UUID.randomUUID();
            notificationId = UUID.randomUUID();
        }


        @Test
        @DisplayName("파라미터에 받은 알림id가 존재하지 않으면 예외")
        void update_notification_NotificationNotFoundException() {

            // given
            when(notificationRepository.findById(any()))
                     .thenReturn(Optional.empty());
            NotificationUpdateRequest request = new NotificationUpdateRequest(userId, notificationId);
            // when & then
            assertThatThrownBy(() -> notificationService.update(request))
                    .isInstanceOf(NotificationNotFoundException.class);

        }

        @Test
        @DisplayName("파라미터에 받은 알림id가 존재하면 그 해당객체를 true로 만드냐")
        void update_notification_change_confirmed() {

            // given
            Notification notification =
                    Notification.forInterest(userId, resourceId, "축구", 5);
            NotificationUpdateRequest request = new NotificationUpdateRequest(userId, notificationId);
            //알림 업데이트로직 안에  알림 찾으면  notification 뱉어버리게
            when(notificationRepository.findById(any()))
                    .thenReturn(Optional.of(notification));

            // when
            NotificationDto update = notificationService.update(request);

            //then
            assertThat(update.confirmed()).isTrue();
        }
    }

    @Nested
    @DisplayName("알림 삭제(물리삭제)")
    class  UpdateNotification_Physical {

        @Test
        @DisplayName("delete하면 deleteConfirmedBefore 메서드부른다 ")
        void delete_confirmed_notification_older_than_7d() {

            // when
            notificationService.deleteAll();

            // then
            verify(notificationRepository, atLeastOnce()).deleteConfirmedBefore(any());

        }

    }

}
