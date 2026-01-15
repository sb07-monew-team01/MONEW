package com.codeit.monew.article.matcher;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.matcher.ArticleMatcherImpl;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleMatcherImplTest {

    private final ArticleMatcherImpl articleMatcher = new ArticleMatcherImpl();

    @Nested
    @DisplayName("Article 관심사 포함 검사 - 모든 키워드 포함")
    class ArticleMatcherAllRequired {

        @ParameterizedTest
        @DisplayName("관심사 이름과 키워드가 모두 제목/요약에 포함되어야 true")
        @CsvSource({
                "네이버 기사, 네이버 요약",
                "Java 프로그래밍, Java 요약",
                "Coin 경제, Coin 요약"
        })
        void match_allKeywordsInTitleAndSummary_thenTrue(String title, String summary) {
            // given
            ArticleCreateRequest articleCreateRequest
                    = ArticleCreateRequestFixture.createWithTitleAndSummary(title, summary);

            List<Interest> interests = List.of(
                    InterestFixture.create("네이버", List.of("기사")),
                    InterestFixture.create("Java", List.of("프로그래밍", "요")),
                    InterestFixture.create("Coin", List.of("경제"))
            );

            // when
            boolean result = articleMatcher.match(articleCreateRequest, interests);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "네이버, 아무내용",
                "아무제목, 기사",
                "네이버, 기사",
                "기사 관련, 네이버",
                "네이버, 다른내용",
                "JAAAAVA, CO"
        })
        @DisplayName("관심사 이름 또는 키워드 중 하나라도 누락되면 false")
        void match_extremeCases(String title, String summary) {
            // given
            ArticleCreateRequest articleCreateRequest
                    = ArticleCreateRequestFixture.createWithTitleAndSummary(title, summary);

            List<Interest> interests = List.of(
                    InterestFixture.create("네이버", List.of("아무내용임")),
                    InterestFixture.create("Java", List.of("프로그래밍")),
                    InterestFixture.create("Coin", List.of("경제"))
            );

            // when
            boolean result = articleMatcher.match(articleCreateRequest, interests);

            assertThat(result).isFalse();
        }
    }
}
