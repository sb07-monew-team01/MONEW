package com.codeit.monew.domain.article.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.exception.ArticleNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


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

            then(articleRepository).should().findById(articleId);
        }

        @Test
        @DisplayName("존재하지 않는 기사면 예외가 발생한다.")
        void softDelete_nullArticle_fail() {
            // given
            UUID articleId = UUID.randomUUID();
            given(articleRepository.findById(articleId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> articleService.softDelete(articleId))
                    .isInstanceOf(ArticleNotFoundException.class);

            then(articleRepository).should().findById(articleId);
        }

        @Test
        @DisplayName("이미 삭제된 기사면 예외가 발생한다. 예외명은 ArticleNotFoundException")
        void softDelete_alreadySoftDeletedArticle_fail() {
            // given
            UUID articleId = UUID.randomUUID();
            Article article = ArticleFixture.createDefaultEntity();
            article.softDelete();
            given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

            // when & then
            // Swagger 문서에서 이미 논리 삭제된 기사는 "기사를 찾을 수 없습니다"
            assertThatThrownBy(() -> articleService.softDelete(articleId))
                    .isInstanceOf(ArticleNotFoundException.class);

            then(articleRepository).should().findById(articleId);
        }
    }
}
