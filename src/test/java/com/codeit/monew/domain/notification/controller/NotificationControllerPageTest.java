package com.codeit.monew.domain.notification.controller;

import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.service.NotificationService;
import com.codeit.monew.global.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerPageTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NotificationService notificationService;

    @Nested
    @DisplayName("알림조회 파라미터")
    class NotificationSearch{


        @Test
        @DisplayName("cursor가_형식이_틀리면_400")
        void bad_cursor_400() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("cursor", "bad_cursor")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("cursor가_형식이_맞으면_200")
        void good_cursor_200() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("cursor", "2026-01-18T10:00:00_7a187ac2-87f7-44d3-a8fb-b1936b1f0000")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("after가_LocalDateTime이 아니면_400")
        void bad_after_400() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("after", "bad_after")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("after가_LocalDateTime이 맞으면_200")
        void good_after_200() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("after", "2026-01-18T10:00:00")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("limit가 50보다 크면_400")
        void bad_limit_400() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("limit", "60")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("limit가 1~50이면_200")
        void good_limit_200() throws Exception {
            mockMvc.perform(get("/api/notifications")
                            .param("limit", "50")
                            .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Query 파라미터와 헤더가 NotificationPageRequest로 올바르게 분배된다")
        void query_to_pageRequest_mapping_good() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            String cursor = "2026-01-18T10:00:00_7a187ac2-87f7-44d3-a8fb-b1936b1f0000";
            String after = "2026-01-18T09:59:00";
            String limit = "10";

            given(notificationService.findUnconfirmedCustom(any()))
                    .willReturn(new PageResponse<>(
                            List.of(), null, null, 0, 0L, false
                    ));

            // when
            mockMvc.perform(get("/api/notifications")
                            .param("cursor", cursor)
                            .param("after", after)
                            .param("limit", limit)
                            .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk());

            // then
            ArgumentCaptor<NotificationPageRequest> captor =
                    ArgumentCaptor.forClass(NotificationPageRequest.class);

            verify(notificationService).findUnconfirmedCustom(captor.capture());

            NotificationPageRequest passed = captor.getValue();
            assertThat(passed.userId()).isEqualTo(userId);
            assertThat(passed.cursor()).isEqualTo(cursor);
            assertThat(passed.after()).isEqualTo(LocalDateTime.parse(after));
            assertThat(passed.limit()).isEqualTo(10);
        }

        @Test
        @DisplayName("컨트롤러가 limit가 null이면 50으로 바꿔서 서비스에 넘긴다")
        void limit_default_50() throws Exception {
            // given
            UUID userId = UUID.randomUUID();

            given(notificationService.findUnconfirmedCustom(any()))
                    .willReturn(new PageResponse<>(
                            List.of(),
                            null,
                            null,
                            0,
                            0L,
                            false
                    ));

            // when
            mockMvc.perform(get("/api/notifications")
                            // limit 파라미터 없음
                            .header("Monew-Request-User-ID", userId.toString()))
                    .andExpect(status().isOk());

            // then
            //findUnconfirmedCustom에 들어갈 매개값 감지
            ArgumentCaptor<NotificationPageRequest> captor =
                    ArgumentCaptor.forClass(NotificationPageRequest.class);

            verify(notificationService).findUnconfirmedCustom(captor.capture());

            NotificationPageRequest request = captor.getValue();
            assertThat(request.limit()).isEqualTo(50);

        }

    }

}
