package com.codeit.monew.domain.interestuser.unit.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.web.InterestNotFoundException;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.interestuser.exception.AlreadySubscribedException;
import com.codeit.monew.domain.interestuser.exception.InterestUserNotFoundException;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.domain.interestuser.service.InterestUserServiceImpl;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class InterestUserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    InterestRepository interestRepository;

    @Mock
    InterestUserRepository interestUserRepository;

    @InjectMocks
    InterestUserServiceImpl interestUserService;

    @Nested
    @DisplayName("관심사 구독 - 상태 검증")
    class SubscribeInterestStateTest {
        @Test
        @DisplayName("성공: 사용자는 관심사를 구독할 수 있다")
        void success_subscribe_interest() {
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            User user = new User("tester@test.com", "tester", "test");
            Interest interest = new Interest("백엔드", List.of("java", "spring"));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));
            given(interestUserRepository.save(any(InterestUser.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            InterestUser result = interestUserService.subscribe(userId, interestId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("실패: 유저를 id 로 찾을 수 없을 때 예외가 발생한다")
        void fail_subscribe_interest_not_found_user() {
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestUserService.subscribe(userId, interestId))
                    .isInstanceOf(UserNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
        
        @Test
        @DisplayName("실패: 관심사를 id 로 찾을 수 없을 때 예외가 발생한다")
        void fail_subscribe_interest_not_found_interest() {
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            User user = new User("tester@test.com", "tester", "test");
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(interestRepository.findById(interestId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestUserService.subscribe(userId, interestId))
                    .isInstanceOf(InterestNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 이미 구독한 관심사는 다시 구독할 수 없다")
        void fail_subscribe_interest_already_subscribed() {
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            User user = new User("tester@test.com", "tester", "test");
            Interest interest = new Interest("백엔드", List.of("java", "spring"));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));
            given(interestUserRepository.existsByUserIdAndInterestId(userId, interestId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> interestUserService.subscribe(userId, interestId))
                    .isInstanceOf(AlreadySubscribedException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ALREADY_SUBSCRIBED);
        }
    }

    @Nested
    @DisplayName("관심사 구독 - 행위 검증")
    class SubscribeInterestBehaviorTest {
        @Test
        @DisplayName("성공: 구독 생성 시 저장소의 save가 호출된다")
        void success_subscribe_interest_save() {
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            User user = new User("tester@test.com", "tester", "test");
            Interest interest = new Interest("백엔드", List.of("java", "spring"));

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

            // when
            interestUserService.subscribe(userId, interestId);

            // then
            then(interestUserRepository).should().save(any(InterestUser.class));
            then(interestRepository).should().save(any(Interest.class));
        }
    }

    @Nested
    @DisplayName("관심사 구독 취소")
    class UnsubscribeInterestTest {
        @Test
        @DisplayName("성공: 사용자가 관심사 구독을 취소하면 delete가 호출된다")
        void success_unsubscribe_interest() {
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            User user = new User("tester@test.com", "tester", "test");
            Interest interest = new Interest("백엔드", List.of("java", "spring"));
            InterestUser interestUser = new InterestUser(user, interest);

            given(interestUserRepository
                    .findByUserIdAndInterestId(userId, interestId))
                    .willReturn(Optional.of(interestUser));

            // when
            interestUserService.unSubscribe(userId, interestId);

            // then
            then(interestRepository).should().save(any(Interest.class));
            then(interestUserRepository).should().delete(interestUser);
        }

        @Test
        @DisplayName("실패: 사용자가 구독하지 않은 관심사를 취소하면 예외가 발생한다")
        void fail_unsubscribe_not_subscribed(){
            // given
            UUID userId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();

            given(interestUserRepository.findByUserIdAndInterestId(userId, interestId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestUserService.unSubscribe(userId, interestId))
                    .isInstanceOf(InterestUserNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTERESTUSER_NOT_FOUND);
        }
    }
}
