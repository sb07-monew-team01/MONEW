package com.codeit.monew.domain.article.infrastructure;

import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssFetchException;
import com.codeit.monew.domain.article.infrastructure.exception.rss.RssParseException;
import com.codeit.monew.domain.article.infrastructure.rss.client.RssArticleClient;
import com.codeit.monew.domain.article.infrastructure.rss.mapper.RssArticleMapper;
import com.codeit.monew.domain.article.matcher.ArticleMatcher;
import com.codeit.monew.domain.interest.entity.Interest;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RssArticleCollectorTest {

    @Mock
    private RssArticleMapper rssArticleMapper;

    @Mock
    private RssArticleClient rssArticleClient;

    @Mock
    private ArticleMatcher articleMatcher;

    @InjectMocks
    private RssArticleCollector rssArticleCollector;

    @Test
    @DisplayName("수집한 RSS 기사들 중 관심사와 매칭되는 Article을 수집한다.")
    void collect_success() {
        // given
        Interest interest = new Interest("프로그래밍", List.of("Spring"));
        List<Interest> interests = List.of(interest);

        // Mock RSS Feed
        SyndFeed mockFeed = mock(SyndFeed.class);
        SyndEntry mockEntry = mock(SyndEntry.class);
        given(mockFeed.getEntries()).willReturn(List.of(mockEntry));

        // 3개 소스 모두 stubbing
        given(rssArticleClient.fetch(anyString())).willReturn(mockFeed);

        // Mock ArticleRequest
        ArticleCreateRequest articleRequest = ArticleCreateRequestFixture
                .createWithTitleAndSummaryAndSource(
                        "프로그래밍이란",
                        "Spring은 Java로한다..",
                        ArticleSource.HANKYUNG
                );

        // Mapper
        given(rssArticleMapper.toArticleCreateRequest(any(SyndEntry.class), any(ArticleSource.class)))
                .willReturn(articleRequest);

        // Matcher: anyList()로 모든 List 매칭!
        given(articleMatcher.match(eq(articleRequest), anyList()))
                .willReturn(true);

        // Mock addTags
        ArticleCreateRequest addTagArticleRequest = ArticleCreateRequestFixture
                .createWithTitleAndSummaryAndSource(
                        "프로그래밍이란",
                        "Spring은 Java로한다.. #프로그래밍 #Spring",
                        ArticleSource.HANKYUNG
                );
        given(rssArticleMapper.addTags(eq(articleRequest), any(Interest.class)))
                .willReturn(addTagArticleRequest);

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(3); // 3개 소스

        assertThat(results).allMatch(result ->
                result.summary().contains("#프로그래밍") &&
                        result.summary().contains("#Spring")
        );
    }

    @Test
    @DisplayName("매칭되지 않는 기사는 필터 처리")
    void collect_filterUnmatched() {
        // given
        Interest interest = new Interest("경제", List.of("환율"));
        List<Interest> interests = List.of(interest);

        SyndFeed mockFeed = mock(SyndFeed.class);
        SyndEntry mockEntry = mock(SyndEntry.class);
        given(mockFeed.getEntries()).willReturn(List.of(mockEntry));
        given(rssArticleClient.fetch(anyString())).willReturn(mockFeed);

        ArticleCreateRequest articleRequest = ArticleCreateRequestFixture
                .createWithTitleAndSummaryAndSource(
                        "스포츠 뉴스",
                        "축구가 시작되었다.",
                        ArticleSource.HANKYUNG
                );
        given(rssArticleMapper.toArticleCreateRequest(any(), any()))
                .willReturn(articleRequest);

        given(articleMatcher.match(eq(articleRequest), anyList()))
                .willReturn(false);

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Mapper가 null 반환하면 empty List 반환")
    void collect_mapperReturnsNull_filtered() {
        // given
        Interest interest = new Interest("경제", List.of("환율"));
        List<Interest> interests = List.of(interest);

        SyndFeed mockFeed = mock(SyndFeed.class);
        SyndEntry mockEntry = mock(SyndEntry.class);
        given(mockFeed.getEntries()).willReturn(List.of(mockEntry));
        given(rssArticleClient.fetch(anyString())).willReturn(mockFeed);

        // Mapper null 반환
        given(rssArticleMapper.toArticleCreateRequest(any(), any()))
                .willReturn(null);

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("RssFetchException 발생 시 해당 뉴스만 스킵하고 계속 수집 진행")
    void collect_rssFetchException_skipAndContinue() {
        // given
        Interest interest = new Interest("경제", List.of("환율"));
        List<Interest> interests = List.of(interest);

        // 한국경제만 실패
        given(rssArticleClient.fetch(contains("hankyung")))
                .willThrow(new RssFetchException("네트워크 오류"));

        // 나머지는 성공
        SyndFeed mockFeed = mock(SyndFeed.class);
        SyndEntry mockEntry = mock(SyndEntry.class);
        given(mockFeed.getEntries()).willReturn(List.of(mockEntry));

        given(rssArticleClient.fetch(contains("chosun"))).willReturn(mockFeed);
        given(rssArticleClient.fetch(contains("yonhap"))).willReturn(mockFeed);

        ArticleCreateRequest articleRequest = ArticleCreateRequestFixture
                .createWithTitleAndSummaryAndSource(
                        "경제 뉴스 입니다.",
                        "환율은 올라가고 있어",
                        ArticleSource.CHOSUN
                );

        given(rssArticleMapper.toArticleCreateRequest(any(), any()))
                .willReturn(articleRequest);
        given(articleMatcher.match(any(), anyList())).willReturn(true);
        given(rssArticleMapper.addTags(any(), any())).willReturn(articleRequest);

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).hasSize(2); // 한국경제 제외, 2개만 성공

        // 3개 소스 모두 시도는 함
        then(rssArticleClient).should(times(3)).fetch(anyString());
    }

    @Test
    @DisplayName("RssParseException 발생 시 해당 소스만 skip")
    void collect_rssParseException_skipAndContinue() {
        // given
        Interest interest = new Interest("경제", List.of("환율"));
        List<Interest> interests = List.of(interest);

        given(rssArticleClient.fetch(contains("chosun")))
                .willThrow(new RssParseException("XML 파싱 오류"));

        // 나머지는 성공
        SyndFeed mockFeed = mock(SyndFeed.class);
        SyndEntry mockEntry = mock(SyndEntry.class);
        given(mockFeed.getEntries()).willReturn(List.of(mockEntry));

        given(rssArticleClient.fetch(contains("hankyung"))).willReturn(mockFeed);
        given(rssArticleClient.fetch(contains("yonhap"))).willReturn(mockFeed);

        ArticleCreateRequest articleRequest = ArticleCreateRequestFixture
                .createWithTitleAndSummaryAndSource(
                        "경제 뉴스 입니다.",
                        "환율은 올라가고 있어",
                        ArticleSource.CHOSUN
                );

        given(rssArticleMapper.toArticleCreateRequest(any(), any()))
                .willReturn(articleRequest);
        given(articleMatcher.match(any(), anyList())).willReturn(true);
        given(rssArticleMapper.addTags(any(), any())).willReturn(articleRequest);

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).hasSize(2); // 조선일보 제외
    }

    @Test
    @DisplayName("모든 뉴스 RSS 수집이 실패하면 빈 리스트 반환")
    void collect_allSourcesFail_returnsEmpty() {
        // given
        Interest interest = new Interest("경제", List.of("환율"));
        List<Interest> interests = List.of(interest);

        // 모든 소스 실패
        given(rssArticleClient.fetch(anyString()))
                .willThrow(new RssFetchException("네트워크 오류"));

        // when
        List<ArticleCreateRequest> results = rssArticleCollector.collect(interests);

        // then
        assertThat(results).isEmpty();

        then(rssArticleClient).should(times(3)).fetch(anyString());
        then(rssArticleMapper).should(never()).toArticleCreateRequest(any(), any());
    }
}