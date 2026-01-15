package com.codeit.monew.domain.article.infrastructure.mapper;

import com.codeit.monew.domain.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.infrastructure.rss.mapper.RssArticleMapper;
import com.codeit.monew.domain.interest.entity.Interest;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RssArticleMapperTest {

    private final RssArticleMapper mapper = new RssArticleMapper();

    @Test
    @DisplayName("정상적인 RSS entry는 ArticleCreateRequest로 변환된다")
    void toArticleCreateRequest_success() {
        // given
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("테스트 제목");
        entry.setLink("https://test.com");

        SyndContentImpl content = new SyndContentImpl();
        content.setValue("<p>요약입니다</p>");
        entry.setDescription(content);

        entry.setPublishedDate(new Date());

        // when
        ArticleCreateRequest result =
                mapper.toArticleCreateRequest(entry, ArticleSource.HANKYUNG);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("테스트 제목");
        assertThat(result.sourceUrl()).isEqualTo("https://test.com");
        assertThat(result.summary()).isEqualTo("요약입니다");
        assertThat(result.source()).isEqualTo(ArticleSource.HANKYUNG);
    }

    @Test
    @DisplayName("summary가 없으면 제목이 summary로 대체된다")
    void toArticleCreateRequest_summaryFallbackToLink() {
        // given
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("제목");
        entry.setLink("https://test.com");
        entry.setPublishedDate(new Date());

        // when
        ArticleCreateRequest result =
                mapper.toArticleCreateRequest(entry, ArticleSource.CHOSUN);

        // then
        assertThat(result.summary()).isEqualTo("제목");
    }

    @Test
    @DisplayName("태그가 정상적으로 summary 뒤에 추가된다")
    void addTags_success() {
        // given
        ArticleCreateRequest req = new ArticleCreateRequest(
                ArticleSource.YEONHAP,
                "url",
                "title",
                LocalDateTime.now(),
                "요약"
        );

        Interest interest = InterestFixture.create(
                "경제",
                List.of("주식", "환율")
        );

        // when
        ArticleCreateRequest result = mapper.addTags(req, interest);

        // then
        assertThat(result.summary())
                .isEqualTo("요약<br>#경제 #주식 #환율");
    }

    @Test
    @DisplayName("matched interest가 null이면 기존 객체를 그대로 반환한다")
    void addTags_nullInterest() {
        // given
        ArticleCreateRequest req = new ArticleCreateRequest(
                ArticleSource.YEONHAP,
                "url",
                "title",
                LocalDateTime.now(),
                "요약"
        );

        // when
        ArticleCreateRequest result = mapper.addTags(req, null);

        // then
        assertThat(result.summary()).isEqualTo("요약");
    }
}
