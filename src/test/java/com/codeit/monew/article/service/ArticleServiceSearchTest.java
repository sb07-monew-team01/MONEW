package com.codeit.monew.article.service;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
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
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
            Article article1 = Article.builder()
                    .title("네이버 뉴스")
                    .source(ArticleSource.NAVER)
                    .publishDate(LocalDateTime.now())
                    .build();
            Article article2 = Article.builder()
                    .title("조선 뉴스")
                    .source(ArticleSource.CHOSUN)
                    .publishDate(LocalDateTime.now().minusDays(1))
                    .build();
            Article article3 = Article.builder()
                    .title("한경 뉴스")
                    .source(ArticleSource.HANKYUNG)
                    .publishDate(LocalDateTime.now().minusDays(2))
                    .build();

            String keyword = "뉴스";
            List<ArticleSource> sources = List.of(ArticleSource.NAVER, ArticleSource.HANKYUNG);
            LocalDateTime publishDateTo = LocalDate.now().atTime(LocalTime.MIDNIGHT);
            LocalDateTime publishDateFrom = publishDateTo.minusDays(7);

            when(articleRepository.findByKeywordAndSource(keyword, sources, publishDateFrom, publishDateTo))
                    .thenReturn(List.of(article3));

            // when
            List<ArticleDto> articles = articleService.searchByKeyword(keyword, sources, publishDateFrom, publishDateTo);

            // then
            assertThat(articles).hasSize(1);
            assertThat(articles).extracting(ArticleDto::source).containsExactly("HANKYUNG");
            assertThat(articles.get(0).publishDate())
                    .isAfterOrEqualTo(publishDateFrom).isBeforeOrEqualTo(publishDateTo);
        }
    }
}
