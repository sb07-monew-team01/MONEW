package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class ArticleDeleteTest {
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    @DisplayName("논리 삭제 테스트")
    class SoftDelete {

        @Test
        @DisplayName("Article을 논리삭제 할 수 있다.")
        void softDelete_success() {
            // given
            UUID articleId = UUID.randomUUID();
            Article article = ArticleFixture.createDefaultEntity();
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when
            articleService.softDelete(articleId);

            //then
            assertThat(article.isDeleted()).isTrue();
        }
    }
}
