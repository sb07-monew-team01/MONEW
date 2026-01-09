package com.codeit.monew.article.service;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
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
            ArticleCreateRequest request = ArticleCreateRequestFixture.createDefault();
            Article article = ArticleFixture.createEntity(request);
            UUID interestId =  UUID.randomUUID();

            // when
            articleService.createArticle(request, interestId);

            //then
            then(articleRepository).should(times(1)).save(any(Article.class));
        }

        @Test
        @DisplayName("기사 정보 중 이미 존재하는 SourceUrl이면 저장하지 않는다.")
        void createArticle_duplicatedSourceUrl_thenFail() {
            // given
            ArticleCreateRequest request = ArticleCreateRequestFixture.createDefault();

            // 기존에 존재하는 sourceURl을 가진 Article
            given(articleRepository.existsBySourceUrl(request.sourceUrl())).willReturn(true);
            UUID interestId =  UUID.randomUUID();

            // when
            articleService.createArticle(request, interestId);

            then(articleRepository).should(never()).save(any(Article.class));

        }
    }
}
