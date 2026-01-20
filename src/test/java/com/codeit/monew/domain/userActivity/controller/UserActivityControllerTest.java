package com.codeit.monew.domain.userActivity.controller;

import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.service.UserActivityServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserActivityController.class)
class UserActivityControllerTest {

    @MockitoBean
    UserActivityServiceImpl userActivityService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("유저 활동 내역 조회를 처리할 수 있다.")
    void getUserActivity() throws Exception {
        // given
        when(userActivityService.getByUserId(any()))
                .thenReturn(Instancio.create(UserActivityDto.class));

        // when & then
        mockMvc.perform(get("/api/user-activities/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 id에 해당하는 사용자가 없을 시 오류가 발생한다.")
    void fail_userNotFound() throws Exception {
        // given
        when(userActivityService.getByUserId(any()))
                .thenThrow(new UserNotFoundException(UUID.randomUUID()));

        // when & then
        mockMvc.perform(get("/api/user-activities/" + UUID.randomUUID()))
                .andExpect(status().is(404));

    }

}