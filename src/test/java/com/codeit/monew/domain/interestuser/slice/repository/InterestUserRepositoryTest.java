package com.codeit.monew.domain.interestuser.slice.repository;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.interestuser.repository.InterestUserRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
@ActiveProfiles("test")
public class InterestUserRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestUserRepository interestUserRepository;

    @Nested
    @DisplayName("관심사 구독")
    class SubscribeInterestTest {
        @Test
        @DisplayName("성공: InterestUser 엔티티가 정상적으로 영속화된다")
        void subscribe_success() {
            // given
            User user = userRepository.save(new User("tester@test.com", "tester", "test"));
            Interest interest = interestRepository.save(new Interest("백엔드", List.of("Java", "Spring")));
            InterestUser interestUser = new InterestUser(user, interest);

            // when
            InterestUser saved = interestUserRepository.save(interestUser);
            em.flush();
            em.clear();

            // then
            assertThat(saved.getId()).isNotNull();
        }

        @Test
        @DisplayName("실패: User-Interest가 이미 존재하면 unique 제약으로 저장이 실패한다")
        void subscribe_fail_already_exist(){
            // given
            User user = userRepository.save(new User("tester@test.com", "tester", "test"));
            Interest interest = interestRepository.save(new Interest("백엔드", List.of("Java", "Spring")));
            interestUserRepository.save(new InterestUser(user, interest));
            em.flush();
            em.clear();

            // when / then
            assertThatThrownBy(() -> {
                interestUserRepository.save(new InterestUser(user, interest));
                em.flush();
                em.clear();
            }).isInstanceOf(ConstraintViolationException.class);
        }
    }
}
