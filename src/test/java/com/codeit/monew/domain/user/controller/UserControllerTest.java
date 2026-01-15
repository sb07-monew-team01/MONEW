package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.controller.UserController;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockitoBean
    UserServiceImpl userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("생성")
    class Create {
        @Test
        @DisplayName("사용자가 회원가입 할 수 있다.")
        void signUp() throws Exception {
            // given
            String email = "some@email.com";
            String nickname = "nickname";
            String password = "password";
            UserSignUpRequest request = new UserSignUpRequest(email, nickname, password);

            // when & then
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isCreated());
            }
        }

        // TODO : 올바르지 않은 이메일 형식으로 가입할 수 없다. (400)
        // TODO : 이미 존재하는 이메일로는 가입할 수 없다 (409)
        // TODO : 알 수 없는 서버 오류가 발생한 경우 500이 돌아온다.
    }