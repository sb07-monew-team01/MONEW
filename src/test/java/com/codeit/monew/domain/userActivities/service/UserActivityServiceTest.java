package com.codeit.monew.domain.userActivities.service;

import com.codeit.monew.domain.articleView.repository.ArticleViewRepository;
import com.codeit.monew.domain.comment.repository.CommentRepository;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.repository.UserRepository;
import com.codeit.monew.domain.userActivities.dto.UserActivityDto;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 활동 내역 조회")
class UserActivityServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    InterestRepository interestRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ArticleViewRepository articleViewRepository;

    @InjectMocks
    UserActivityService userActivityService;


    @Test
    @DisplayName("사용자 활동 내역을 조회할 수 있다.")
    void findUserActivities() {
        // given
        User user = Instancio.of(User.class)
                .ignore(field("deletedAt"))
                .create();

        // when
        UserActivityDto dto = userActivityService.findByUser(user.getId());

        // then
        Assertions.assertThat(dto).isNotNull();
    }

}