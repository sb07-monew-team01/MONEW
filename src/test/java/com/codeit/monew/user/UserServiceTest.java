package com.codeit.monew.user;

import com.codeit.monew.domain.user.dto.UserDto;
import com.codeit.monew.domain.user.dto.request.UserSignInRequest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserAlreadyDeletedException;
import com.codeit.monew.domain.user.exception.UserAlreadyExistsException;
import com.codeit.monew.domain.user.exception.UserLoginFailedException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.service.UserService;
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
    private UserService userService;

    @Nested
    @DisplayName("회원 가입")
    class SignUp{
        @Test
        @DisplayName("이메일, 닉네임, 비밀번호로 회원 가입을 할 수 있다.")
        void signUp() {
            // given
            UserSignInRequest dto = new UserSignInRequest("someemail@gmail.com", "닉네임이야", "password1");
            when(userRepository.save(any(User.class)))
                    .thenReturn(new User(dto.email(), dto.nickname(), dto.password()));
            when(userMapper.toDto(any(User.class)))
                    .thenReturn(new UserDto(UUID.randomUUID(), dto.email(), dto.nickname(), LocalDateTime.now()));

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
            ReflectionTestUtils.setField(user,"id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId);

            // then
            verify(userRepository).findById(userId);
            assertThat(user.getDeletedAt()).isNotNull();
            assertThatThrownBy(() -> userService.signUp(new UserSignInRequest(userEmail, "newNickname", "password2")))
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
            ReflectionTestUtils.setField(user,"id", UUID.randomUUID());

            // when
            userService.signUp(new UserSignInRequest(email, nickname, password));
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(user));

            //then
            assertThatThrownBy(() -> userService.signUp(new UserSignInRequest(email, "다른닉네임이야", "다른비밀번호야")))
                    .isInstanceOf(UserAlreadyExistsException.class);

        }
    }

    @Nested
    @DisplayName("로그인")
    class Login{
        @Test
        @DisplayName("이메일과 비밀번호로 로그인할 수 있다.")
        void login(){
            // given
            String email = "email@email.com";
            String password = "password";
            User user = new User(email, "nickname", password);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toDto(any(User.class)))
                    .thenReturn(new UserDto(UUID.randomUUID(), email, "nickname", LocalDateTime.now()));

            // when
            UserDto userDto = userService.login(email, password);

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
            assertThatThrownBy(() -> userService.login(email, wrongPassword))
                    .isInstanceOf(UserLoginFailedException.class);
        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class Delete{
        @Test
        @DisplayName("""
            유저 삭제 요청을 통해 논리 삭제가 가능하다.
            논리 삭제가 된 경우 조회가 불가능하다.""")
        void userDelete() {
            // given
            String userEmail = "test@asdf.com";
            String userNickname = "delete";
            String userPassword = "password";
            User user = new User(userEmail, userNickname, userPassword);

            UUID userId = UUID.randomUUID();
            ReflectionTestUtils.setField(user,"id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId);

            // when & then
            verify(userRepository).findById(userId);
            assertThat(user.getDeletedAt()).isNotNull();
            assertThatThrownBy(() -> userService.delete(userId))
                    .isInstanceOf(UserAlreadyDeletedException.class);
            assertThatThrownBy(() -> userService.signUp(new UserSignInRequest(userEmail, "newNickname", "password2")))
                    .isInstanceOf(UserAlreadyDeletedException.class);
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
            ReflectionTestUtils.setField(user,"id", userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId);

            // when & then
            verify(userRepository).findById(userId);
            assertThat(user.getDeletedAt()).isNotNull();
            assertThatThrownBy(() -> userService.login("test@asdf.com", "password"))
                    .isInstanceOf(UserAlreadyDeletedException.class);
        }
    }
}
