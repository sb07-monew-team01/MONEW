package com.codeit.monew.domain.user.controller;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.exception.UserNotAuthorizedException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Nested
    @DisplayName("유저 수정")
    class Update {
        @Test
        @DisplayName("유저의 닉네임을 수정할 수 있다.")
        void update() throws Exception {
            // given
            UserDto response = Instancio.create(UserDto.class);
            UserUpdateRequest request = new UserUpdateRequest(response.id(), "니는 짱이다");
            when(userService.update(any(), any())).thenReturn(response);

            // when & then
            mockMvc.perform(patch("/api/users/" + response.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("MoNew-Request-User-ID", request.userId())
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Nested
        @DisplayName("실패 - 유효성 검증")
        class ValidationFailure {

            // TODO : 닉네임 유효성 검사 (400) (최대 몇 자?)
            @ParameterizedTest
            @NullAndEmptySource
//            @ValueSource(strings = "aslidjflwekejfsdfjliwe") TODO 정책 추가
            @DisplayName("유효한 닉네임을 사용해야 한다.")
            void fail_notValidNickname(String nickname) throws Exception {
                // given
                UserUpdateRequest request = new UserUpdateRequest(UUID.randomUUID(), nickname);
                // when & then
                mockMvc.perform(patch("/api/users/" + request.userId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("MoNew-Request-User-ID", request.userId().toString())
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(400));
            }

            @Test
            @DisplayName("ID에 해당하는 사용자가 존재하지 않는 경우 오류가 발생한다.")
            void fail_UserNotFoundById() throws Exception {
                // given
                UUID userId = UUID.randomUUID();
                UserUpdateRequest request = new UserUpdateRequest(userId, "솔쳤습니까 휴먼");
                when(userService.update(userId, request)).thenThrow(new UserNotFoundException(userId));
                // when & then
                mockMvc.perform(patch("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("MoNew-Request-User-ID", request.userId())
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(404));
            }
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("사용자 정보 수정 권한이 없는 경우 오류가 발생한다.")
            void fail_NotAuthorized() throws Exception {
                // given
                UUID userId = UUID.randomUUID();
                UserUpdateRequest request = new UserUpdateRequest(userId, "오류에요");
                when(userService.update(any(), any())).thenThrow(new UserNotAuthorizedException(userId, UUID.randomUUID()));

                // when & then
                mockMvc.perform(patch("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("MoNew-Request-User-ID", userId)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().is(403));
            }
        }
    }

    @Nested
    @DisplayName("유저 논리 삭제")
    class SoftDelete {
        @Test
        @DisplayName("유저 논리 삭제 요청을 처리할 수 있다.")
        void delete_soft() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/users/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("MoNew-Request-User-ID", UUID.randomUUID().toString())
            ).andExpect(status().is(204));

        }

        @Nested
        @DisplayName("실패 - 유효성 검증")
        class ValidationFailure {
            @Test
            @DisplayName("올바른 아이디 형식이 들어와야한다.")
            void fail_notValidId() throws Exception {
                // when & then
                mockMvc.perform(delete("/api/users/lsaidmf")
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is4xxClientError());
            }
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("사용자 삭제 권한 없음")
            void fail_notAuthorized() throws Exception {
                // given
                doThrow(new UserNotAuthorizedException(UUID.randomUUID(), UUID.randomUUID())).when(userService).delete(any(), any());

                // when & then
                mockMvc.perform(delete("/api/users/" + UUID.randomUUID())
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(403));
            }

            @Test
            @DisplayName("사용자 정보 없음")
            void fail_userNotFound() throws Exception {
                // given
                doThrow(new UserNotFoundException(UUID.randomUUID())).when(userService).delete(any(), any());

                // when & then
                mockMvc.perform(delete("/api/users/" + UUID.randomUUID())
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(404));
            }
        }

    }

    @Nested
    @DisplayName("유저 물리 삭제")
    class HardDelete {
        @Test
        @DisplayName("유저 물리 삭제 요청을 처리할 수 있다.")
        void deleteHard() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/users/" + UUID.randomUUID() + "/hard")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("MoNew-Request-User-ID", UUID.randomUUID().toString())
            ).andExpect(status().is(204));

        }

        @Nested
        @DisplayName("실패 - 유효성 검증")
        class ValidationFailure {
            @Test
            @DisplayName("올바른 아이디 형식이 들어와야한다.")
            void fail_notValidId() throws Exception {
                // when & then
                mockMvc.perform(delete("/api/users/lsaidmf/hard")
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is4xxClientError());
            }
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("사용자 삭제 권한 없음")
            void fail_notAuthorized() throws Exception {
                // given
                doThrow(new UserNotAuthorizedException(UUID.randomUUID(), UUID.randomUUID())).when(userService).deleteHard(any(), any());

                // when & then
                mockMvc.perform(delete("/api/users/" + UUID.randomUUID() + "/hard")
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(403));
            }

            @Test
            @DisplayName("사용자 정보 없음")
            void fail_userNotFound() throws Exception {
                // given
                doThrow(new UserNotFoundException(UUID.randomUUID())).when(userService).deleteHard(any(), any());

                // when & then
                mockMvc.perform(delete("/api/users/" + UUID.randomUUID() + "/hard")
                                .header("MoNew-Request-User-ID", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(404));
            }
        }
    }
}



