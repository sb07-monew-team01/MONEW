package com.codeit.monew.domain.interest.unit.entity;

import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.exception.KeywordValidException;
import com.codeit.monew.domain.interestkeyword.entity.InterestKeyword;
import com.codeit.monew.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InterestTest {

    @Nested
    @DisplayName("관심사 생성")
    class CreateInterestTest {
        @Test
        @DisplayName("성공: 유효한 이름과 키워드가 주어지면 관심사가 생성된다")
        void success_create_interest(){
            // given
            String name = "백엔드";
            List<String> keywords = List.of("java", "spring");

            // when
            Interest interest = new Interest(name, keywords);

            // then
            assertThat(interest).isNotNull();
            assertThat(interest.getName()).isEqualTo(name);
            assertThat(interest.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactlyElementsOf(keywords);
        }



        @Test
        @DisplayName("실패: 관심사 내에서 키워드가 중복되면 예외가 발생한다")
        void fail_create_interest_duplicate_keyword(){
            // given
            String name = "프로그래밍";
            List<String> keywords = List.of("java", "spring", "java");

            // when & then
            assertThatThrownBy(() -> new Interest(name, keywords))
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
            assertThatThrownBy(() -> new Interest(name, keywords))
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
            assertThatThrownBy(() -> new Interest(name, keywords))
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
            assertThatThrownBy(() -> new Interest(name, keywords))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NULL_KEYWORD);
        }
    }

    @Nested
    @DisplayName("관심사 수정")
    class UpdateInterestTest {
        @Test
        @DisplayName("성공: 관심사 수정 시 키워드가 변경된다")
        void success_update_interest_keywords(){
            // given
            Interest interest = new Interest("백엔드", List.of("java", "spring"));

            // when
            interest.update(List.of("DB", "Spring boot"));

            // then
            assertThat(interest.getKeywords())
                    .extracting(InterestKeyword::getKeyword)
                    .containsExactly("DB", "Spring boot");
        }

        @Test
        @DisplayName("실패: 관심사 내에서 같은 키워드가 중복되면 예외가 발생한다")
        void fail_update_interest_duplicate_keyword(){
            // given
            Interest interest = new Interest("백엔드", List.of("java"));

            // when & then
            assertThatThrownBy(() -> interest.update(List.of("java", "spring", "java")))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_KEYWORD_DUPLICATE);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 없으면 예외가 발생한다")
        void fail_update_interest_empty_keyword(){
            // given
            Interest interest = new Interest("백엔드", List.of("java"));

            // when & then
            assertThatThrownBy(() -> interest.update(List.of()))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_EMPTY_KEYWORD);
        }

        @Test
        @DisplayName("실패: 관심사의 키워드가 10개를 초과하면 예외가 발생한다")
        void fail_create_interest_over_keyword_limit() {
            // given
            Interest interest = new Interest("백엔드", List.of("java"));

            // when & then
            assertThatThrownBy(() -> interest.update(List.of(
                    "java", "spring", "python", "C", "C++", "javascript", "typescript", "html", "css",
                    "sql", "mongoDB")))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.TOO_MANY_KEYWORD);
        }

        @Test
        @DisplayName("실패 : 관심사의 키워드가 null이면 예외가 발생한다")
        void fail_create_interest_null_keyword(){
            // given
            Interest interest = new Interest("백엔드", List.of("java"));

            // when & then
            assertThatThrownBy(() -> interest.update(null))
                    .isInstanceOf(KeywordValidException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INTEREST_NULL_KEYWORD);
        }
    }
}
