package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiAuthenticationException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiRateLimitException;
import com.codeit.monew.domain.article.infrastructure.exception.naver.NaverApiServerException;
import com.codeit.monew.domain.article.infrastructure.naver.client.NaverArticleClient;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverApiResponse;
import com.codeit.monew.domain.article.infrastructure.naver.dto.NaverItem;
import com.codeit.monew.domain.article.infrastructure.naver.mapper.NaverArticleMapper;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class NaverArticleCollectorTest {
    @Mock
    private NaverArticleMapper naverArticleMapper;

    @Mock
    private NaverArticleClient naverArticleClient;

    @InjectMocks
    private NaverArticleCollector naverArticleCollector;

    @Test
    @DisplayName("관심사 기반으로 검색된 네이버 뉴스 아이템들을 기사 생성 요청 객체로 변환하여 수집한다")
    void collect_success () {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Java", "Spring"))
        );

        NaverApiResponse naverApiResponse = new NaverApiResponse(
                "2024-01-01", 100, 1, 10,
                List.of(
                        new NaverItem(
                                "제목",
                                "https://originalLink.com",
                                "https://link.com",
                                "설명",
                                "Mon, 12 Jan 2026 11:23:55 +0900"
                        ),
                        new NaverItem(
                                "제목2",
                                "https://originalLink.com3",
                                "https://link.com3",
                                "설명",
                                "Mon, 12 Jan 2026 11:23:55 +0900"
                        ),
                        new NaverItem(
                                "제목3",
                                "https://originalLink3.com",
                                "https://link.com3",
                                "설명",
                                "Mon, 12 Jan 2026 11:23:55 +0900"
                        )

                )
        );

        given(naverArticleClient.searchArticle(eq("프로그래밍 Java Spring"), anyInt(), anyInt()))
                .willReturn(naverApiResponse);

        given(naverArticleMapper.toArticleCreateRequest(any(), any(Interest.class)))
                .willAnswer(invocation -> {
                    NaverItem item = invocation.getArgument(0);
                    return ArticleCreateRequestFixture.createWithTitleAndSummary(
                            item.title(),
                            "요약"
                    );
                });

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).hasSize(3);

        then(naverArticleClient).should(times(1)).searchArticle(eq("프로그래밍 Java Spring"), anyInt(), anyInt());
        then(naverArticleMapper).should(times(3)).toArticleCreateRequest(any(), any(Interest.class));
    }

    @Test
    @DisplayName("네이버 API 호출 중 예외가 발생하면 빈 리스트를 반환한다")
    void collect_fail_returnsEmpty() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Java"))
        );

        given(naverArticleClient.searchArticle(anyString(), anyInt(), anyInt()))
                .willThrow(new RuntimeException());

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("NaverApiAuthenticationException 발생 시 빈 리스트 반환")
    void collect_authException_returnsEmpty() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Java"))
        );

        given(naverArticleClient.searchArticle(anyString(), anyInt(), anyInt()))
                .willThrow(new NaverApiAuthenticationException("인증 실패"));

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("NaverApiRateLimitException 발생 시 빈 리스트 반환")
    void collect_rateLimitException_returnsEmpty() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Spring"))
        );

        given(naverArticleClient.searchArticle(anyString(), anyInt(), anyInt()))
                .willThrow(new NaverApiRateLimitException("Rate Limit 초과"));

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("NaverApiServerException 발생 시 빈 리스트 반환")
    void collect_serverException_returnsEmpty() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Kotlin"))
        );

        given(naverArticleClient.searchArticle(anyString(), anyInt(), anyInt()))
                .willThrow(new NaverApiServerException("서버 오류"));

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("기타 Exception 발생 시 빈 리스트 반환")
    void collect_generalException_returnsEmpty() {
        // given
        List<Interest> interests = InterestFixture.createMultiple(
                new Interest("프로그래밍", List.of("Java"))
        );

        given(naverArticleClient.searchArticle(anyString(), anyInt(), anyInt()))
                .willThrow(new RuntimeException("기타 오류"));

        // when
        List<ArticleCreateRequest> result = naverArticleCollector.collect(interests);

        // then
        assertThat(result).isEmpty();
    }
}
