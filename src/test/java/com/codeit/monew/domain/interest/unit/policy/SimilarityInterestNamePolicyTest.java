package com.codeit.monew.domain.interest.unit.policy;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.InterestNameTooSimilarException;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.policy.SimilarityInterestNamePolicy;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimilarityInterestNamePolicyTest {
    private final InterestNamePolicy policy = new SimilarityInterestNamePolicy();

    @Nested
    @DisplayName("관심사 이름이 기존에 있던 관심사와 80% 유사도를 보일 때의 정책")
    class SimilarityPolicy{
    
        @Test
        @DisplayName("실패: 기존 관심사 이름과 80% 이상 유사하면 예외가 발생한다")
        void fail_similarity_interest_name(){
            //given
            String input = "프로그래밍 언어";
            List<Interest> interests = List.of(
                    new Interest("프로그래밍", List.of("python", "C"))
            );

            //when & then
            assertThatThrownBy(() -> policy.apply(input, interests))
                    .isInstanceOf(InterestNameTooSimilarException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NAME_TOO_SIMILAR);
        }

        @Test
        @DisplayName("80% 미만이면 예외 없이 통과한다")
        void pass_when_similarity_under_threshold() {
            // given
            String input = "동물";
            List<Interest> interests = List.of(
                    new Interest("프로그래밍", List.of("python"))
            );

            // when & then
            assertThatNoException()
                    .isThrownBy(() -> policy.apply(input, interests));
        }
    }
}