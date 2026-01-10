package com.codeit.monew.article.service;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.InterestFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import com.codeit.monew.domain.article.service.ArticleServiceImpl;
import com.codeit.monew.domain.interest.entity.Interest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceCreateTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMatcher articleMatcher;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    @DisplayName("수집한 기사에 대한 Article 저장 테스트")
    class Create {
        @Test
        @DisplayName("기사 정보와 관심사 리스트가 주어지면 Article를 저장한다.")
        void createArticle() {
            // given
            ArticleCreateRequest request = ArticleCreateRequestFixture.createDefault();
            List<Interest> interests = InterestFixture.createDefault(); // 매칭되도록 준비

            given(articleRepository.existsBySourceUrl(request.sourceUrl())).willReturn(false);

            // when
            articleService.createArticle(request, interests);

            // then
            then(articleRepository).should().save(argThat(article ->
                    article.getTitle().equals(request.title()) &&
                            article.getSourceUrl().equals(request.sourceUrl())
            ));
        }

        @Test
        @DisplayName("기사 정보 중 이미 존재하는 SourceUrl이면 저장하지 않는다.")
        void createArticle_duplicatedSourceUrl_thenFail() {
            // given
            ArticleCreateRequest request = ArticleCreateRequestFixture.createDefault();
            List<Interest> interests = InterestFixture.createDefault();

            // 기존에 존재하는 sourceURl을 가진 Article
            given(articleRepository.existsBySourceUrl(request.sourceUrl())).willReturn(true);

            // when
            articleService.createArticle(request, interests);
            //then
            then(articleRepository).should(never()).save(any(Article.class));
        }

        @Test
        @DisplayName("관심사 키워드를 포함하는 기사가 아니면 저장하지 않는다.")
        void createArticle_withoutInterestKeyword_thenNotSaved() {
            ArticleCreateRequest request = ArticleCreateRequestFixture.createDefault();
            List<Interest> interests = InterestFixture.createDefault();
            given(articleRepository.existsBySourceUrl(request.sourceUrl())).willReturn(false);
            given(articleMatcher.match(request, interests)).willReturn(false);

            articleService.createArticle(request, interests);
            //then
            then(articleMatcher).should().match(request, interests);
            then(articleRepository).should(never()).save(any(Article.class));
        }
    }
}
