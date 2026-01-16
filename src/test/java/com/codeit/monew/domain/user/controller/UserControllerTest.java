package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
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

import static org.mockito.Mockito.when;
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

        @Test
        @DisplayName("올바르지 않은 이메일 형식으로 가입할 수 없다.")
        void fail_notValidEmail() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest("sadlifhn.lcm","nickname", "password");

            // when & then
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("이메일에 공백이 올 수 없다.")
        void fail_emailNotBlank() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(" ","nickname", "password");

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400));
        }

        @Test
        @DisplayName("이미 존재하는 이메일로는 가입할 수 없다.")
        void fail_emailAlreadyExist() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest("","nickname", "password");
            when(userService.signUp(request)).thenThrow(new UserAlreadyExistsException(request.email()));

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(409));
        }

        // TODO : 이미 존재하는 이메일로는 가입할 수 없다 (409)
    }
}