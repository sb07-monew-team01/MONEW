package com.codeit.monew.article.service;

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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceCreateTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    @DisplayName("수집한 기사에 대한 Article 저장 테스트")
    class Create {
        @Test
        @DisplayName("기사 정보와 관심사 Id가 들어오면 Article를 저장한다.")
        void createArticle() {
            //given
            ArticleCreateRequest request = new ArticleCreateRequest(
                    ArticleSource.NAVER,
                    "http://target.com",
                    "test-title",
                    "test-content",
                    LocalDateTime.now(),
                    "test summary"
            );

            Article article = Article.builder()
                    .source(request.source())
                    .sourceUrl(request.sourceUrl())
                    .title(request.title())
                    .content(request.content())
                    .publishDate(request.publishDate())
                    .summary(request.summary())
                    .build();

            UUID instantId =  UUID.randomUUID();
            // when
            articleService.createArticle(request, instantId);

            //then
            then(articleRepository).should(times(1)).save(article);
        }
    }


}
