package com.codeit.monew.interest.unit.service;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.InterestNotFoundException;
import com.codeit.monew.domain.interest.exception.KeywordValidException;
import com.codeit.monew.domain.interest.policy.InterestNamePolicy;
import com.codeit.monew.domain.interest.repository.InterestRepository;
import com.codeit.monew.domain.interest.service.InterestServiceImpl;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class InterestServiceImplTest {
    @Mock
    InterestRepository interestRepository;

    @Mock
    InterestNamePolicy interestNamePolicy;

    @InjectMocks
    InterestServiceImpl interestService;

    @Nested
    @DisplayName("관심사 생성 - 상태 검증")
    class CreateInterestState {

        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest() {
            //given
            String name = "백엔드";
            List<String> keywords = Arrays.asList("java", "spring");
            given(interestRepository.save(any(Interest.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Interest result = interestService.create(name, keywords);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getKeywords()).containsExactlyElementsOf(keywords);
        }

        @Test
        @DisplayName("실패: 관심사 내에서 같은 키워드가 중복되면 예외가 발생한다")
        void fail_create_interest_duplicate_keyword() {
            // given
            String name = "프로그래밍";
            List<String> keywords = List.of("java", "spring", "java");

            // when & then
            assertThatThrownBy(() -> interestService.create(name, keywords))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_KEYWORD_DUPLICATE);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 없으면 예외가 발생한다")
        void fail_create_interest_empty_keyword(){
            // given
            String name = "프로그래밍";
            List<String> keywords = List.of();

            // when & then
            assertThatThrownBy(() -> interestService.create(name, keywords))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_EMPTY_KEYWORD);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 10개를 초과하면 예외가 발생한다")
        void fail_create_interest_over_keyword_limit() {
            // given
            String name = "프로그래밍";
            List<String> keywords = List.of("java", "spring", "python", "C", "C++", "javascript", "typescript", "html", "css",
                    "sql", "mongoDB");

            // when & then
            assertThatThrownBy(() -> interestService.create(name, keywords))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.TOO_MANY_KEYWORD);
        }

        @Test
        @DisplayName("실패 : 관심사의 키워드가 null이면 예외가 발생한다")
        void fail_create_interest_null_keyword(){
            // given
            String name = "프로그래밍";
            List<String> keywords = null;

            // when & then
            assertThatThrownBy(() -> interestService.create(name, keywords))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NULL_KEYWORD);
        }
    }

    @Nested
    @DisplayName("관심사 생성 - 행위 검증")
    class CreateInterestBehavior {
        @Test
        @DisplayName("성공: 관심사 생성 시 저장소의 save가 호출된다")
        void success_create_interest_save(){
            //given
            given(interestRepository.findAll()).willReturn(List.of());
            given(interestRepository.save(any(Interest.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            //when
            interestService.create("백엔드", List.of("java", "spring"));

            //then
            then(interestRepository).should().save(any(Interest.class));
        }
    }

    @Nested
    @DisplayName("관심사 수정 - 상태 검증")
    class UpdateInterestState {
        @Test
        @DisplayName("성공: 관심사 수정 시 키워드가 변경된다")
        void success_update_interest_keywords(){
            // given
            Interest interest = new Interest("백엔드", List.of("java", "spring"));
            UUID interestId = UUID.randomUUID();
            given(interestRepository.findById(interestId))
                    .willReturn(Optional.of(interest));

            // when
            interestService.editKeywords(interestId, List.of("DB", "Spring boot"));

            // then
            assertThat(interest.getKeywords()).containsExactly("DB", "Spring boot");
        }

        @Test
        @DisplayName("실패: 관심사를 id로 찾을 수 없을 때 예외가 발생한다")
        void fail_update_interest_not_found(){
            // given
            UUID interestId = UUID.randomUUID();
            given(interestRepository.findById(interestId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(interestId, List.of("DB", "Spring boot")))
                    .isInstanceOf(InterestNotFoundException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 관심사 내에서 같은 키워드가 중복되면 예외가 발생한다")
        void fail_update_interest_duplicate_keyword(){
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("백엔드", List.of("java"));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(interestId, List.of("java", "Spring Boot", "java")))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_KEYWORD_DUPLICATE);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 없으면 예외가 발생한다")
        void fail_update_interest_empty_keyword(){
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("백엔드", List.of("java"));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(interestId, List.of("java", "Spring Boot", "java")))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_KEYWORD_DUPLICATE);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 10개를 초과하면 예외가 발생한다")
        void fail_create_interest_over_keyword_limit() {
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("백엔드", List.of("java"));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));


            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(interestId, List.of("java", "spring", "python", "C", "C++", "javascript", "typescript", "html", "css",
                    "sql", "mongoDB")))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.TOO_MANY_KEYWORD);
        }

        @Test
        @DisplayName("실패 : 관심사의 키워드가 null이면 예외가 발생한다")
        void fail_create_interest_null_keyword(){
            // given
            UUID interestId = UUID.randomUUID();
            Interest interest = new Interest("백엔드", List.of("java"));
            given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

            // when & then
            assertThatThrownBy(() -> interestService.editKeywords(interestId, null))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NULL_KEYWORD);
        }
    }
}