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
        @DisplayName("검색어와 선택한 여러 출처가 일치하는 기사를 조회한다.")
        void titleOrSummaryContainingKeyword() {
            // given
            Article article1 = Article.builder()
                    .title("네이버 뉴스")
                    .source(ArticleSource.NAVER)
                    .build();
            Article article2 = Article.builder()
                    .title("조선 뉴스")
                    .source(ArticleSource.CHOSUN)
                    .build();
            Article article3 = Article.builder()
                    .title("한경 뉴스")
                    .source(ArticleSource.HANKYUNG)
                    .build();

            String keyword = "뉴스";
            List<ArticleSource> sources = List.of(ArticleSource.NAVER, ArticleSource.HANKYUNG);

            when(articleRepository.findByKeywordAndSource(keyword, sources))
                    .thenReturn(List.of(article1, article3));

            // when
            List<ArticleDto> articles = articleService.searchByKeyword(keyword, sources);

            // then
            verify(articleRepository, times(1)).findByKeywordAndSource(any(), any());
            assertThat(articles).hasSize(2);
            assertThat(articles).extracting(ArticleDto::source).containsExactly("NAVER", "HANKYUNG");
        }
    }
}
