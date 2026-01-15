package com.codeit.monew.user.service;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserLoginRequest;
import com.codeit.monew.domain.user.dto.request.UserSignUpRequest;
import com.codeit.monew.domain.user.dto.request.UserUpdateRequest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserAlreadyDeletedException;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.service.UserServiceImpl;
import com.codeit.monew.domain.user.util.UserMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
            String userEmail = "test@asdf.com";
            String userNickname = "delete";
            String userPassword = "password";
            User user = new User(userEmail, userNickname, userPassword);

            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", userId);
            user.updateDeletedAt();
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> userService.signUp(new UserSignUpRequest(userEmail, "newNickname", "password2")))
                    .isInstanceOf(UserAlreadyDeletedException.class);
        }


        @Test
        @DisplayName("중복 이메일로는 가입할 수 없다")
        void fail_duplicatedEmail() {
            // given
            String email = "dsfm@email.com";
            String nickname = "나야";
            String password = "비밀번호야";
            User user = new User(email, nickname, password);
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(user));

            //then
            assertThatThrownBy(() -> userService.signUp(new UserSignUpRequest(email, "다른닉네임이야", "다른비밀번호야")))
                    .isInstanceOf(UserAlreadyExistsException.class);

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
            verify(userRepository).findByEmail(userEmail);
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


        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class Delete {
        @Test
        @DisplayName("유저 삭제 요청을 통해 논리 삭제가 가능하다.")
        void userDelete() {
            // given
            String userEmail = "test@asdf.com";
            String userNickname = "delete";
            String userPassword = "password";
            User user = new User(userEmail, userNickname, userPassword);

            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId);

            // when & then
            verify(userRepository).findById(userId);
            assertThat(user.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("논리 삭제가 된 경우 삭제가 불가능하다.")
        void deletedUserCantDeleteAgain() {
            // given
            String userEmail = "test@asdf.com";
            String userNickname = "delete";
            String userPassword = "password";
            User user = new User(userEmail, userNickname, userPassword);

            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            user.updateDeletedAt();

            // when & then
            assertThatThrownBy(() -> userService.delete(userId))
                    .isInstanceOf(UserAlreadyDeletedException.class);
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
            UserUpdateRequest dto = new UserUpdateRequest(userId, newNickname);

            // when
            userService.updateUser(dto);

            //then
            verify(userRepository).findById(userId);
            assertThat(user.getNickname()).isEqualTo(newNickname);
        }

        @Test
        @DisplayName("수정 요청 uuid가 존재하지 않으면 예외가 발생한다.")
        void notValidUserUuid() {
            // given
            User user = new User("email@ma.com", "nickname", "password");
            UUID validId = UUID.randomUUID();
            UUID wrongUserId = UUID.randomUUID();
            ReflectionTestUtils.setField(user, "id", validId);
            when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(new UserUpdateRequest(wrongUserId, "newNickname")))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("논리 삭제된 유저 수정시 예외가 발생한다.")
        void deletedUserUpdate() {
            // given
            User user = new User("email@sdsd@com", "nickname", "password");
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            user.updateDeletedAt();
            UserUpdateRequest dto = new UserUpdateRequest(user.getId(), "newNickname");
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> userService.updateUser(dto))
                    .isInstanceOf(UserAlreadyDeletedException.class);
        }
    }
}

