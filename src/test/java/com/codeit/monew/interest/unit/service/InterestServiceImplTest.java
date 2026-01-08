package com.codeit.monew.interest.unit.service;

import com.codeit.monew.domain.interest.Interest;
import com.codeit.monew.domain.interest.InterestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InterestServiceImplTest {
    @Mock
    InterestRepository interestRepository;

    @InjectMocks
    InterestServiceImpl interestService;

    @Nested
    @DisplayName("관심사 생성")
    class CreateInterest {

        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest() {
            //given
            String name = "백엔드";
            List<String> keywords = Arrays.asList("java", "spring");

            // when
            Interest result = interestService.create(name, keywords);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getKeywords()).containsExactlyElementsOf(keywords);
        }

        @Test
        @DisplayName("실패: 관심사 이름이 기존 이름과 80% 이상 유사하면 예외가 발생한다")
        void fail_create_interest_duplicate_name() {
            // given
            String newName = "프로그래밍언어";
            List<String> keywords = Arrays.asList("java", "spring");

            given(interestRepository.findAll())
                    .willReturn(List.of(new Interest("프로그래밍", List.of("python", "C"))));

            // when & then
            InterestNameTooSimilarException exception = catchThrowableOfType(
                    () -> interestService.create(newName, keywords),
                    InterestNameTooSimilarException.class
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.INTEREST_NAME_TOO_SIMILAR);
        }
    }
}