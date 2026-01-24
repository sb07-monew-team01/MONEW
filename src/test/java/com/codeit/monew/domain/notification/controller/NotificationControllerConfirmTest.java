package com.codeit.monew.domain.notification.controller;

import com.codeit.monew.domain.notification.dto.request.NotificationUpdateAllRequest;
import com.codeit.monew.domain.notification.dto.request.NotificationUpdateRequest;
import com.codeit.monew.domain.notification.dto.response.NotificationDto;
import com.codeit.monew.domain.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerConfirmTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NotificationService notificationService;

    @Nested
    @DisplayName("알림조회 파라미터")
    class NotificationSearch{

        @Test
        @DisplayName("단일조회 notificationId,userId가 NotificationUpdateRequest에 조립된다")
        void confirmNotification_maps_values_and_calls_service() throws Exception {
            UUID notificationId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            NotificationDto dummy = mock(NotificationDto.class);
            given(notificationService.update(any())).willReturn(dummy);

            mockMvc.perform(patch("/api/notifications/{notificationId}", notificationId)
                            .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk());

            ArgumentCaptor<NotificationUpdateRequest> captor =
                    ArgumentCaptor.forClass(NotificationUpdateRequest.class);

            verify(notificationService).update(captor.capture());

            NotificationUpdateRequest passed = captor.getValue();
            assertThat(passed.userId()).isEqualTo(userId);
            assertThat(passed.notificationId()).isEqualTo(notificationId);
        }
        @Test
        @DisplayName("전체조회 userId가 NotificationUpdateAllRequest 조립된다")
        void confirmAll_maps_userId_and_calls_service() throws Exception {
            UUID userId = UUID.randomUUID();

            given(notificationService.updateAll(any()))
                    .willReturn(List.of());

            mockMvc.perform(patch("/api/notifications")
                            .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk());

            ArgumentCaptor<NotificationUpdateAllRequest> captor =
                    ArgumentCaptor.forClass(NotificationUpdateAllRequest.class);

            verify(notificationService).updateAll(captor.capture());
            NotificationUpdateAllRequest passed = captor.getValue();

            assertThat(passed.userId()).isEqualTo(userId);
        }


    }
}
