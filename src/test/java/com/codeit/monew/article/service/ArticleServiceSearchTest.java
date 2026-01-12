package com.codeit.monew.article.service;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.common.dto.PageResponse;
import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    class Search {

        @Test
        @DisplayName("""
        검색어와 선택한 여러 출처가 일치하는 기사를 지정한 날짜 범위 내에서 조회한다.
        근데 페이징을 곁들인
        """)
        void titleOrSummaryContainingKeywordWithCursorPaging() {
            // given

            ArticleCreateRequest request1 = ArticleCreateRequestFixture.createDummy(0, -1);
            ArticleCreateRequest request2 = ArticleCreateRequestFixture.createDummy(0, -2);
            ArticleCreateRequest request3 = ArticleCreateRequestFixture.createDummy(0, 0);

            Article article1 =  ArticleFixture.createEntity(request1);
            Article article2 =  ArticleFixture.createEntity(request2);
            Article article3 =  ArticleFixture.createEntity(request3);

            String keyword = "뉴스";
            List<ArticleSource> sources = List.of(ArticleSource.NAVER);
            LocalDateTime publishDateTo = LocalDate.now().plusDays(1).atStartOfDay();
            LocalDateTime publishDateFrom = LocalDate.now().minusDays(7).atStartOfDay();

            ArticleSearchRequest searchRequest
                    = new ArticleSearchRequest(keyword, sources, publishDateFrom, publishDateTo,
                    null, null, null, null, null);

            ArticleSearchCondition searchCondition
                    = new ArticleSearchCondition(keyword, sources, publishDateFrom, publishDateTo,
                    null, null, null, null, null);


            ArticleDto dto1 = new ArticleDto(article1.getTitle(), article1.getSummary(), article1.getSource().toString(), article1.getPublishDate());
            ArticleDto dto2 = new ArticleDto(article2.getTitle(), article2.getSummary(), article2.getSource().toString(), article2.getPublishDate());
            ArticleDto dto3 = new ArticleDto(article3.getTitle(), article3.getSummary(), article3.getSource().toString(), article3.getPublishDate());

            Page<Article> pages = new PageImpl<>(List.of(article3, article1, article2));

            when(articleRepository.findByKeywordAndSource(any(ArticleSearchCondition.class))).thenReturn(pages);

            when(articleMapper.toDto(article1)).thenReturn(dto1);
            when(articleMapper.toDto(article2)).thenReturn(dto2);
            when(articleMapper.toDto(article3)).thenReturn(dto3);

            // when
            PageResponse<ArticleDto> articlePages = articleService.searchByKeyword(searchRequest);
            List<ArticleDto> articles = articlePages.content();

            // then
            assertThat(articles).hasSize(3);
            assertThat(articles).extracting(ArticleDto::source).containsOnly("NAVER");
            assertThat(articles.get(0).title()).isEqualTo(article3.getTitle());
            assertThat(articles.get(1).publishDate()).isEqualTo(article1.getPublishDate());
        }
    }
}
