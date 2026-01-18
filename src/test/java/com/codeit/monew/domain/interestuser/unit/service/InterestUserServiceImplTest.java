package com.codeit.monew.domain.interestuser.unit.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.domain.interestuser.service.InterestUserServiceImpl;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
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
        @DisplayName("성공: 유효한 사용자와 관심사가 주어지면 구독이 생성된다")
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
            InterestUser result = interestUserService.subscribe(userId, interestId);

            // then
            then(interestUserRepository).should().save(any(InterestUser.class));
        }
    }
}
