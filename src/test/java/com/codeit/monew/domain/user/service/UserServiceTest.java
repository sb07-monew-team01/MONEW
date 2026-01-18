package com.codeit.monew.domain.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.*;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.util.UserMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("회원 가입")
    class SignUp {
        @Test
        @DisplayName("이메일, 닉네임, 비밀번호로 회원 가입을 할 수 있다.")
        void signUp() {
            // given
            UserSignUpRequest dto = new UserSignUpRequest("someemail@gmail.com", "닉네임이야", "password1");
            when(userRepository.save(any(User.class)))
                    .thenReturn(new User(dto.email(), dto.nickname(), dto.password()));
            when(userMapper.toDto(any(User.class)))
                    .thenReturn(new UserDto(UUID.randomUUID(), dto.email(), dto.nickname(), LocalDateTime.now()));
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            // when
            UserDto response = userService.signUp(dto);

            //then
            verify(userRepository).save(any(User.class));
            verify(userMapper).toDto(any(User.class));
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("물리 삭제 이전 같은 이메일로 가입이 불가능하다.")
        void cantSignUp() {
            // given
            User user = Instancio.create(User.class);
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> userService.signUp(new UserSignUpRequest(user.getEmail(), "nickname", "password2")))
                    .isInstanceOf(UserAlreadyDeletedException.class);
            verify(userMapper, never()).toDto(any(User.class));
        }


        @Test
        @DisplayName("중복 이메일로는 가입할 수 없다")
        void fail_duplicatedEmail() {
            // given
            User user = Instancio.of(User.class)
                    .set(field("deletedAt"), null)
                    .create();
            when(userRepository.findByEmail(user.getEmail()))
                    .thenReturn(Optional.of(user));

            //then
            assertThatThrownBy(() -> userService.signUp(new UserSignUpRequest(user.getEmail(), "다른닉네임이야", "다른비밀번호야")))
                    .isInstanceOf(UserAlreadyExistsException.class);
            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("이메일과 비밀번호로 로그인할 수 있다.")
        void login() {
            // given
            String email = "email@email.com";
            String password = "password";
            User user = new User(email, "nickname", password);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toDto(any(User.class)))
                    .thenReturn(new UserDto(UUID.randomUUID(), email, "nickname", LocalDateTime.now()));

            // when
            UserDto userDto = userService.login(new UserLoginRequest(email, password));

            //then
            verify(userRepository).findByEmail(email);
            verify(userMapper).toDto(any(User.class));
            assertThat(userDto).isNotNull();
        }

        @Test
        @DisplayName("이메일과 비밀번호가 일치하지 않으면 로그인할 수 없다.")
        void login_failed() {
            // given
            String email = "me@email.com";
            User user = new User(email, "nickname", "password");
            String wrongPassword = "wrongPassword";
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> userService.login(new UserLoginRequest(email, wrongPassword)))
                    .isInstanceOf(UserLoginFailedException.class);
            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("논리 삭제된 유저는 로그인 할 수 없다.")
        void deletedUserCantLogin() {
            // given
            String userEmail = "test@asdf.com";
            String userNickname = "delete";
            String userPassword = "password";
            User user = new User(userEmail, userNickname, userPassword);

            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", userId);
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
            user.updateDeletedAt();

            // when & then
            assertThatThrownBy(() -> userService.login(new UserLoginRequest(userEmail, userPassword)))
                    .isInstanceOf(UserAlreadyDeletedException.class);
            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 요청을 하면 예외가 발생한다.")
        void notExistEmail() {
            // given
            String email = "not@email.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.login(new UserLoginRequest(email, "password")))
                    .isInstanceOf(UserNotFoundException.class);
            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class Delete {
        @Test
        @DisplayName("유저 삭제 요청을 통해 논리 삭제가 가능하다.")
        void userDelete() {
            // given
            UUID userId = UUID.randomUUID();
            User user = Instancio.of(User.class)
                    .set(field("deletedAt"), null)
                    .create();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId, userId);

            // when & then
            assertThat(user.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("유저 물리 삭제가 가능하다.")
        void deleteHard() {
            // given
            User user = Instancio.of(User.class)
                    .set(field("deletedAt"), null)
                    .create();
            when(userRepository.findById(any())).thenReturn(Optional.of(user));

            // when
            userService.deleteHard(user.getId(), user.getId());

            // then
            verify(userRepository).delete(user);
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class Failure {
            @Test
            @DisplayName("존재하지 않는 아이디는 삭제가 불가능하다")
            void fail_userNotExist() {
                // given
                when(userRepository.findById(any())).thenReturn(Optional.empty());

                // when & then
                UUID loginId = UUID.randomUUID();
                assertThatThrownBy(() -> userService.delete(loginId, loginId))
                        .isInstanceOf(UserNotFoundException.class);
            }

            @Test
            @DisplayName("논리 삭제가 된 경우 삭제가 불가능하다.")
            void deletedUserCantDeleteAgain() {
                // given
                User user = Instancio.create(User.class);
                when(userRepository.findById(any())).thenReturn(Optional.of(user));

                // when & then
                assertThatThrownBy(() -> userService.delete(user.getId(), user.getId()))
                        .isInstanceOf(UserAlreadyDeletedException.class);
            }
        }
    }

    @Nested
    @DisplayName("유저 수정")
    class Update {
        @Test
        @DisplayName("유저 닉네임을 수정할 수 있다.")
        void changeNickname() {
            // given
            String email = "email@email.com";
            String nickname = "nickname";
            User user = new User(email, nickname, "password");
            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toDto(any(User.class)))
                    .thenReturn(new UserDto(UUID.randomUUID(), email, nickname, LocalDateTime.now()));
            String newNickname = "itsMe";
            UserUpdateRequest dto = new UserUpdateRequest(newNickname);

            // when
            userService.update(userId, userId, dto);

            //then
            assertThat(user.getNickname()).isEqualTo(newNickname);
        }

        @Nested
        @DisplayName("실패 - 비즈니스 로직")
        class BusinessLogicFailure {
            @Test
            @DisplayName("수정 권한이 없는 경우 오류가 발생한다.")
            void fail_unAuthorized() {
                // given
                UserUpdateRequest request = new UserUpdateRequest("nickname");

                // when & then
                assertThatThrownBy(() -> userService.update(UUID.randomUUID(), UUID.randomUUID(), request))
                        .isInstanceOf(UserNotAuthorizedException.class);
            }

            @Test
            @DisplayName("요청 uuid가 존재하지 않으면 예외가 발생한다.")
            void notValidUserUuid() {
                // given
                UUID wrongUserId = UUID.randomUUID();
                when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> userService.update(wrongUserId, wrongUserId, new UserUpdateRequest("nickname")))
                        .isInstanceOf(UserNotFoundException.class);
            }

            @Test
            @DisplayName("논리 삭제된 유저 수정시 예외가 발생한다.")
            void deletedUserUpdate() {
                // given
                User user = Instancio.create(User.class);
                UserUpdateRequest dto = new UserUpdateRequest("nickname");
                UUID userId = user.getId();
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // when & then
                assertThatThrownBy(() -> userService.update(userId, userId, dto))
                        .isInstanceOf(UserAlreadyDeletedException.class);
            }

        }
    }
}

