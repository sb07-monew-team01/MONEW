package com.codeit.monew.article;

import com.codeit.monew.domain.article.dto.mapper.ArticleMapper;
import com.codeit.monew.domain.article.dto.response.ArticleDto;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleService;
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
        @DisplayName("검색어와 출처가 일치하는 기사를 조회한다.")
        void titleOrSummaryContainingKeyword() {
            // given
            Article article1 = Article.builder()
                    .title("축구 리그")
                    .source(ArticleSource.NAVER)
                    .build();
            Article article2 = Article.builder()
                    .summary("축구는 재밌다")
                    .source(ArticleSource.CHOSUN)
                    .build();
            String keyword = "축구";
            ArticleSource source = ArticleSource.NAVER;

            when(articleRepository.findByKeywordAndSource(keyword, source))
                    .thenReturn(List.of(article1));

            // when
            List<ArticleDto> articles = articleService.searchByKeyword(keyword, source);

            // then
            verify(articleRepository, times(1)).findByKeyword(any());
            assertThat(articles).hasSize(1);
            assertThat(articles).extracting(ArticleDto::title).contains("축구 리그");
        }
    }
}
