package com.codeit.monew.domain.notification;

import com.codeit.monew.global.dto.PageResponse;
import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.entity.Notification;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static org.mockito.ArgumentMatchers.eq;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServicePaginationTest {

    @Mock
    NotificationRepository notificationRepository;

    @InjectMocks
    NotificationServiceImpl notificationService;

    @Nested
    @DisplayName("알림조회 : 서비스에서 처리하는 nextcursor와nextafter가 적절히 들어가냐")
    class Search {

        private UUID userId;
        private int limit;
        private List<Notification> content = new ArrayList<>();
        private NotificationPageRequest request;

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID();
            limit = 10;
            request = new NotificationPageRequest(null, null, limit, userId);

            for (int i = 0; i < limit; i++) {
                Notification n = mock(Notification.class);

                UUID notificationId = UUID.randomUUID();
                LocalDateTime createdAt =
                        LocalDateTime.of(2026, 1, 13, 10, 0).minusDays(i);

                when(n.getId()).thenReturn(notificationId);
                when(n.getCreatedAt()).thenReturn(createdAt);

                content.add(n);
            }
        }
        @Test
        @DisplayName(" hasNext = true 면 , nextCursor,nextAfter 값이 들어간다")
        void hasNextIsTrue_nextCursorAndNextAfter_exist() {
            // given
            //다음은 21개정도있다
            Slice<Notification> slice =
                    new SliceImpl<>(content, Pageable.unpaged(), true);
            //가짜로 레포가 이렇게 답한다고 하고
            when(notificationRepository.search(eq(request))).thenReturn(slice);
            when(notificationRepository.countByUserId(userId)).thenReturn(21L);

             // when
             PageResponse<NotificationDto> res =
                     notificationService.findUnconfirmed(request);

            // then
            //넥스트커서값이 적절하게 들어가냐
            Notification last = content.get(limit - 1);
            assertThat(res.nextCursor()).isEqualTo(last.getCreatedAt().toString());
            assertThat(res.nextAfter()).isEqualTo(last.getCreatedAt());
            }

        @Test
        @DisplayName(" hasNext = flase 면 , nextCursor,nextAfter 값이 null이다")
        void hasNextIsFalse_nextCursorAndNextAfter_null() {
            // given
            // 10개 리턴 다음껀없어
            Slice<Notification> slice =
                    new SliceImpl<>(content, Pageable.unpaged(), false);
            //가짜로 레포가 이렇게 답한다고 하고
            when(notificationRepository.search(eq(request))).thenReturn(slice);
            when(notificationRepository.countByUserId(userId)).thenReturn(10L);

            // when
            PageResponse<NotificationDto> res =
                    notificationService.findUnconfirmed(request);

            // then
            //넥스트커서값이 다 null이냐
            Notification last = content.get(limit - 1);
            assertThat(res.nextCursor()).isNull();
            assertThat(res.nextAfter()).isNull();
        }

    }
}
