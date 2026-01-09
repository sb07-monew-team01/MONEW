package com.codeit.monew.article.service;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;

    @Spy
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    class Search {

        @Test
        @DisplayName("검색어와 선택한 여러 출처가 일치하는 기사를 지정한 날짜 범위 내에서 조회한다.")
        void titleOrSummaryContainingKeyword() {
            // given
            ArticleCreateRequest request1 = ArticleCreateRequestFixture.createDummy(0, 0);
            ArticleCreateRequest request2 = ArticleCreateRequestFixture.createDummy(1, 2);

            Article article1 =  ArticleFixture.createEntity(request1);
            Article article2 =  ArticleFixture.createEntity(request2);


            String keyword = "뉴스";
            List<ArticleSource> sources = List.of(ArticleSource.NAVER, ArticleSource.HANKYUNG);
            LocalDateTime publishDateTo = LocalDate.now().plusDays(1).atStartOfDay();
            LocalDateTime publishDateFrom = LocalDate.now().minusDays(7).atStartOfDay();

            ArticleSearchRequest searchRequest
                    = new ArticleSearchRequest(keyword, sources, publishDateFrom, publishDateTo);

            when(articleRepository.findByKeywordAndSource(searchRequest)).thenReturn(List.of(article1));

            // when
            List<ArticleDto> articles = articleService.searchByKeyword(searchRequest);

            // then
            assertThat(articles).hasSize(1);
            assertThat(articles).extracting(ArticleDto::source).containsExactly("NAVER");
            assertThat(articles.get(0).publishDate())
                    .isAfterOrEqualTo(publishDateFrom).isBeforeOrEqualTo(publishDateTo);
        }
    }
}
