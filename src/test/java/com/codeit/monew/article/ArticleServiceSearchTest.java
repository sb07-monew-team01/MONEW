package com.codeit.monew.article;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleService;
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceSearchTest {

    @InjectMocks
    ArticleServiceImpl articleService;

    @Mock
    ArticleRepository articleRepository;

    @Nested
    class Search {

        @Test
        @DisplayName("검색어가 기사 제목에 포함되면 조회할 수 있다.")
        void titleContainingKeyword() {
            // given
            Article article = Article.builder()
                    .title("축구리그")
                    .build();
            String keyword = "축구";

            when(articleRepository.findByTitleContaining(keyword))
                    .thenReturn(List.of(article));

            // when
            List<Article> articles = articleService.searchByKeyword(keyword);

            // then
            assertThat(articles).hasSize(1);
            assertThat(articles.get(0).getTitle()).isEqualTo("축구리그");
        }
    }
}
