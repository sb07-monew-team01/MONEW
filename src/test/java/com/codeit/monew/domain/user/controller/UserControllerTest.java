package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("사용자가 회원가입 할 수 있다.")
        void signUp() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(
                    "test@test.com",
                    "nickname",
                    "password123"
            );

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }


        @Nested
        @DisplayName("실패 - 유효성 검증")
        class ValidationFailure {

            @DisplayName("올바른 이메일이 들어와야한다.")
            @ParameterizedTest(name = "[잘못된 이메일 {index}] ''{0}''")
            @NullAndEmptySource
            @ValueSource(strings = {
                    "asdf.com"})
            void invalidEmail(String email) throws Exception {
                // given
                UserSignUpRequest request = Instancio.of(UserSignUpRequest.class)
                        .set(field(UserSignUpRequest::email), email)
                        .create();

                // when & then
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(400));
            }

        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("이미 존재하는 이메일로는 가입할 수 없다.")
            void emailAlreadyExist() throws Exception {
                // given
                UserSignUpRequest request = new UserSignUpRequest("rmail@sadf.com", "nickname", "password");
                when(userService.signUp(any())).thenThrow(new UserAlreadyExistsException(request.email()));

                // when & then
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(409));
            }
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("올바른 이메일과 비밀번호로 로그인 할 수 있다.")
        void login() throws Exception {
            // given
            String email = "email@asdf.com";
            UserLoginRequest request = new UserLoginRequest(email, "request");
            when(userService.login(request)).thenReturn(new UserDto(UUID.randomUUID(), email, "nickname", LocalDateTime.now().minusDays(1)));

            // when & then
            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class Failure {
            @Test
            @DisplayName("이메일와 비밀번호가 불일치하면 로그인 할 수 없다")
            void wrongPassword() throws Exception {
                // given
                String email = "email@asdf.com";
                UserLoginRequest request = new UserLoginRequest(email, "request");
                when(userService.login(request))
                        .thenThrow(new UserLoginFailedException(email));

                // when & then
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(401));
            }
        }

        @DisplayName("실패 - 유효성 검증")
        @Nested
        class ValidationFailure {
            @DisplayName("비밀번호가 비어있으면 안된다.")
            @ParameterizedTest(name = "[{index}] ''{0}''")
            @NullAndEmptySource
            void blankPassword(String password) throws Exception {
                // given
                UserLoginRequest request = new UserLoginRequest("email@asdf.com", password);
                when(userService.login(request))
                        .thenThrow(new UserLoginFailedException(request.email()));

                // when & then
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(400));
            }

            @DisplayName("올바른 이메일이 들어와야한다.")
            @ParameterizedTest(name = "[잘못된 이메일 {index}] ''{0}''")
            @NullAndEmptySource
            @ValueSource(strings = {"asdf.com"})
            void notValidEmail(String email) throws Exception {
                // given
                UserLoginRequest request = new UserLoginRequest(email, "password");

                // when & then
                mockMvc.perform(post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(400));
            }

        }
    }
}

