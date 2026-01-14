package com.codeit.monew.user.repository;

import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("삭제된지 특정 날짜가 지난 유저를 한번에 삭제할 수 있다.")
    void deleteAllByDeletedAtBefore() {
        // given

        // 논리삭제 이후 7일 지난 유저들
        for (int i = 0; i < 20; i++) {
            User user = new User("demail" + i + "@gmail.com", "nickname" + i, "password" + i);
            user.updateDeletedAt(LocalDateTime.now().minusDays(8));  // 8일 전으로 설정
            userRepository.save(user);  // 수정 후 저장
        }
        // 논리 삭제 이후 7일이 지나지 않은 유저들
        for (int i = 0; i < 35; i++) {
            User user = new User("nemail" + i + "@gmail.com", "nickname" + i, "password" + i);
            user.updateDeletedAt(LocalDateTime.now().minusHours(70));  // 3일 전
            userRepository.save(user);  // 수정 후 저장
        }
        // 논리 삭제가 이루어지지 않은 유저들
        for (int i = 0; i < 13; i++) {
            User user = new User("email" + i + "@gmail.com", "nickname" + i, "password" + i);
            userRepository.save(user);
        }

        // when
        long deleted = userRepository.deleteAllByDeletedAtBefore(LocalDateTime.now().minusDays(7));

        // then
        assertThat(deleted).isEqualTo(20);
        assertThat(userRepository.findAll()).hasSize(48);
    }
}