package com.codeit.monew.user.service;

import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.user.service.UserService;
import com.codeit.monew.domain.user.util.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UserService.class, UserMapper.class})
@ActiveProfiles("test")
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("사용자를 물리 삭제할 수 있다.")
    void userDelete() {
        // given
        User user = new User("email@sdsd@com", "nickname", "password");
        user.updateDeletedAt(LocalDateTime.now().minusDays(8));
        userRepository.save(user);
        UUID userId = user.getId();

        // when
        userService.deleteHard(userId);

        //then
        assertThat(userRepository.findById(userId)).isEmpty();
    }



}