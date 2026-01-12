package com.codeit.monew.article.infrastructure.mapper;

import com.codeit.monew.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverItem;
import com.codeit.monew.domain.article.infrastructure.naver.mapper.NaverArticleMapper;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NaverArticleMapperTest {
    private final NaverArticleMapper mapper = new NaverArticleMapper();
    
    @Test
    @DisplayName("item 값을 올바르게 ArticleRequest로 변환해야 하며, 날짜 형식을 LocalDateTime으로 변환할 수 있어야 한다.")
    void pubDateParse_itemToArticleRequest_success() {
        // given
        // Naver API 응답 포맷 (RFC_1123)
        String pubDate = "Tue, 13 Jan 2026 01:43:20 +0900";
        NaverItem item = new NaverItem(
                "삼성",
                "https://link.naver.com/test",
                "https://origin.naver.com/test",
                "주식이 어쩌고 경제가 어쩌고",
                pubDate
        );

        Interest interest = InterestFixture.create("삼성", List.of("주식", "경제"));

        // when
        ArticleCreateRequest result = mapper.toArticleCreateRequest(item, interest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.source()).isEqualTo(ArticleSource.NAVER);
        assertThat(result.title()).isEqualTo("삼성");
        assertThat(result.sourceUrl()).isEqualTo("https://link.naver.com/test");
        assertThat(result.publishDate()).isEqualTo(LocalDateTime.of(2026, 1, 13, 1, 43, 20));
        assertThat(result.summary())
                .isEqualTo("주식이 어쩌고 경제가 어쩌고<br>#삼성 #주식 #경제");
    }

    @Test
    @DisplayName("공백이 적용된 키워드도 변환할 수 있어야 한다.")
    void blankContainsKeyword_itemToArticleRequest_success() {
        // given
        // Naver API 응답 포맷 (RFC_1123)
        String pubDate = "Tue, 13 Jan 2026 01:43:20 +0900";
        NaverItem item = new NaverItem(
                "축구 축구 축구",
                "https://link.naver.com/test",
                "https://origin.naver.com/test",
                "축구 키워드랑 손 흥 민 토트넘 키워드",
                pubDate
        );

        Interest interest = InterestFixture.create("축구 축구", List.of("손 흥 민 토트넘", "축구"));

        // when
        ArticleCreateRequest result = mapper.toArticleCreateRequest(item, interest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.summary())
                .isEqualTo("축구 키워드랑 손 흥 민 토트넘 키워드<br>#축구 축구 #손 흥 민 토트넘 #축구");
    }

}
