package com.codeit.monew.article.matcher;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.matcher.ArticleMatcherImpl;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleMatcherImplTest {

    private final ArticleMatcherImpl articleMatcher = new ArticleMatcherImpl();

    @Nested
    @DisplayName("Article 관심사 포함 검사")
    class ArticleMatcher{
        @Test
        @DisplayName("기사 제목에 관심사 키워드가 포함되면 true를 반환한다.")
        void match_articleContainsKeyword_thenTrue() throws  Exception{
            ArticleCreateRequest articleCreateRequest = ArticleCreateRequestFixture.createWithTitleAndSummary("경제", "요약은 아무거나");

            // 관심사 리스트
            List<Interest> interests = InterestFixture.createDefault();

            boolean result = articleMatcher.match(articleCreateRequest, interests);

            assertThat(result).isTrue();
        }
    }

}