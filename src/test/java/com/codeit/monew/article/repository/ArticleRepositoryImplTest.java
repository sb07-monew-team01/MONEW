package com.codeit.monew.article.repository;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@ActiveProfiles("test")
class ArticleRepositoryImplTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("""
            기본 검색 조건(출처, 날짜)에서 커서 기반 페이징으로 기사를 조회한다.
            출처: NAVER
            날짜: 최근 일주일, 예시) 12일(월) ~ 5일(월)
            """)
    void searchArticleWithCursorPaging() {
        // given
        for (int i = 0; i < 10; i++) {
            articleRepository.save(
                    ArticleFixture.createEntity(
                            ArticleCreateRequestFixture.createDummy(i % 2, -i)
                    )
            );
        }

        ArticleSearchCondition searchCondition
                = ArticleSearchCondition.builder()
                .orderBy("publishDate")
                .direction("DESC")
                .limit(3)
                .build();

        // when 첫 번째 페이지
        Slice<Article> pages1 = articleRepository.findByKeywordAndSource(searchCondition);
        List<Article> articles1 = pages1.getContent();

        // then
        assertThat(articles1).hasSize(3);
        assertThat(articles1.get(0).getSource()).isEqualTo(ArticleSource.NAVER);
        assertThat(articles1.get(1).getPublishDate())
                .isAfter(articles1.get(2).getPublishDate())
                .isBefore(articles1.get(0).getPublishDate());
        assertThat(pages1.hasNext()).isTrue();

        // when 두 번째 페이지
        String nextCursor = articles1.get(2).getPublishDate().toString();
        LocalDateTime nextAfter = articles1.get(2).getCreatedAt();

        ArticleSearchCondition searchCondition2
                = ArticleSearchCondition.builder()
                .cursor(nextCursor)
                .after(nextAfter)
                .orderBy("publishDate")
                .direction("DESC")
                .limit(3)
                .build();

        Slice<Article> pages2 = articleRepository.findByKeywordAndSource(searchCondition2);
        List<Article> articles2 = pages2.getContent();

        // then
        assertThat(articles2).hasSize(1);
        assertThat(articles2.get(0).getPublishDate())
                .isBefore(articles1.get(2).getPublishDate());
        assertThat(pages2.hasNext()).isFalse();
    }

    @Test
    @DisplayName("""
            기본 검색 조건에서 특정값을 기준으로 기사 목록을 정렬한다.
            정렬순: 조회수, 댓글수
            """)
    void searchArticleWithViewAndCommentCount() {

        // given
        for (int i = 0; i < 5; i++) {
            articleRepository.save(
                    ArticleFixture.createWithViewAndComment(
                            ArticleCreateRequestFixture.createDummy(0, 0), 5-i, i
                    )
            );
        }

        ArticleSearchCondition searchByViewCount
                = ArticleSearchCondition.builder()
                .orderBy("viewCount")
                .direction("DESC")
                .limit(10)
                .build();

        ArticleSearchCondition searchByCommentCount
                = ArticleSearchCondition.builder()
                .orderBy("commentCount")
                .direction("DESC")
                .limit(10)
                .build();

        // when
        // 조회수 정렬
        Slice<Article> pagesWithView = articleRepository.findByKeywordAndSource(searchByViewCount);
        List<Article> articlesWithView = pagesWithView.getContent();

        // 댓글수 정렬
        Slice<Article> pagesWithComment = articleRepository.findByKeywordAndSource(searchByCommentCount);
        List<Article> articlesWithComment = pagesWithComment.getContent();

        // then
        assertThat(articlesWithView).hasSize(5);
        assertThat(articlesWithView.get(0)).isEqualTo(articlesWithComment.get(4));
        assertThat(articlesWithView.get(4).getTitle()).isEqualTo(articlesWithComment.get(0).getTitle());
    }
}