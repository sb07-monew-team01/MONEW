package com.codeit.monew.notification;




import com.codeit.monew.domain.notification.entity.Notification;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.ResourceType;
import com.codeit.monew.domain.notification.repository.NotificationRepository;
import com.codeit.monew.domain.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

       // when
       NotificationDto result =
               notificationService.createByInterest(userId, resourceId, resourceName, resourceCont);

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
        NotificationDto result = notificationService.createByCommentLike(userId,resourceId,name);

        // then
        assertThat(result.resourceType()).isEqualTo(ResourceType.COMMENT);
        assertThat(result.content()).isEqualTo("좋아요누른사람님이 나의 댓글을 좋아합니다.");
        verify(notificationRepository).save(any(Notification.class));
    }

}
