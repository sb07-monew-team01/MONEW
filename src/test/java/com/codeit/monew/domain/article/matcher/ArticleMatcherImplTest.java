package com.codeit.monew.domain.article.matcher;

import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleMatcherImplTest {

    private final ArticleMatcherImpl articleMatcher = new ArticleMatcherImpl();

    @Nested
    @DisplayName("Article 관심사 포함 검사")
    class ArticleMatcher{

        @ParameterizedTest
        @ValueSource(strings = {
                "네이버 입니다",
                "javaProgramming",
                "Coin코인",
                "경경경제제제"
        })
        @DisplayName("기사 제목에 관심사들의 키워드가 포함되면 true를 반환한다.")
        void match_articleTitleContainsKeyword_thenTrue(String title) {
            // given
            ArticleCreateRequest articleCreateRequest
                    = ArticleCreateRequestFixture.createWithTitleAndSummary(title, "요약은 아무거나");

            // 관심사 리스트
            List<Interest> interests = InterestFixture.createMultiple(
                    InterestFixture.create("기사", List.of("네이버", "조선")),
                    InterestFixture.create("프로그래밍", List.of("Java", "Spring")),
                    InterestFixture.create("코인", List.of("경제", "비트코인"))
            );

            //when
            boolean result = articleMatcher.match(articleCreateRequest, interests);

            //then
            assertThat(result).isTrue();
        }
        @ParameterizedTest
        @ValueSource(strings = {
                "네이버 입니다",
                "javaProgramming",
                "Coin코인",
                "경경경제제제"
        })
        @DisplayName("기사 내용에 관심사들의 키워드가 포함되면 true를 반환한다.")
        void match_Summary_ContainsKeyword_success(String summary) {
            // given
            ArticleCreateRequest articleCreateRequest
                    = ArticleCreateRequestFixture.createWithTitleAndSummary("제목은 아무거나", summary);

            // 관심사 리스트
            List<Interest> interests = InterestFixture.createMultiple(
                    InterestFixture.create("기사", List.of("네이버", "조선")),
                    InterestFixture.create("프로그래밍", List.of("Java", "Spring")),
                    InterestFixture.create("코인", List.of("경제", "비트코인"))
            );

            // when
            boolean result = articleMatcher.match(articleCreateRequest, interests);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "네이버제목, 네이버 요약",
                "javaProgramming , JavaPrograming 요약",
                "Coin코인, aaaaaaaaaaa",
                "경경경제제제, bbbbbbbbbbbbb"
        })
        @DisplayName("기사 요약과 제목에 관심사들의 키워드가 포함되지 않으면 false를 반환한다.")
        void match_articleTitleAndSummary_notContainsKeyword_fail(String title, String summary) {
            // given
            ArticleCreateRequest articleCreateRequest
                    = ArticleCreateRequestFixture.createWithTitleAndSummary(title, summary);

            // 관심사 리스트
            List<Interest> interests = InterestFixture.createMultiple(
                    InterestFixture.create("xxx", List.of("111", "222")),
                    InterestFixture.create("Coin코인보다 긴 키워드 네임", List.of("JavaPrograming 요약보다 긴 키워드")),
                    InterestFixture.create("제b", List.of("bbbbbbbbbbbbbbbbbbbbbbbb", "g요"))
            );

            // when
            boolean result = articleMatcher.match(articleCreateRequest, interests);

            // then
            assertThat(result).isFalse();
        }
    }



}