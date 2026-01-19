package com.codeit.monew.domain.userActivity.service;

import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.repository.UserActivityRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 활동 내역 조회 서비스")
class UserActivityServiceImplTest {

    @Mock
    UserActivityRepository userActivityRepository;

    @InjectMocks
    UserActivityServiceImpl userActivityService;

    @Test
    @DisplayName("사용자 ID로 활동 내역을 조회할 수 있다")
    void getByUserId() {
        // given
        UUID userId = UUID.randomUUID();
        UserActivityDto dto = Instancio.create(UserActivityDto.class);
        when(userActivityRepository.getUserActivity(userId)).thenReturn(dto);

        // when
        UserActivityDto response = userActivityService.getByUserId(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(dto);
        verify(userActivityRepository).getUserActivity(userId);
    }
}
