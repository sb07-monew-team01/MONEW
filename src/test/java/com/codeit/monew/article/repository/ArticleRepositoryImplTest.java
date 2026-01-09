package com.codeit.monew.article.repository;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.request.ArticleCreateRequest;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryImplTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("""
    검색어와 출처가 비어있으면 출처가 NAVER인 기사 전체가 조회된다.
    날짜 범위가 비어있으면 오늘~7일전 사이의 기사가 조회된다.
    """)
    void searchAllNaverArticle_EmptyKeyword_EmptySource() {
        // given
        ArticleCreateRequest request1 = ArticleCreateRequestFixture.createDummy(0, 0);
        ArticleCreateRequest request2 = ArticleCreateRequestFixture.createDummy(0, -14);
        ArticleCreateRequest request3 = ArticleCreateRequestFixture.createDummy(1, 0);
        ArticleCreateRequest request4 = ArticleCreateRequestFixture.createDummy(2, 2);
        articleRepository.save(ArticleFixture.createEntity(request1));
        articleRepository.save(ArticleFixture.createEntity(request2));
        articleRepository.save(ArticleFixture.createEntity(request3));
        articleRepository.save(ArticleFixture.createEntity(request4));

        ArticleSearchRequest searchRequest
                = new ArticleSearchRequest(null, null, null, null);

        // when
        List<Article> articles = articleRepository.findByKeywordAndSource(searchRequest);

        // then
        assertThat(articles).hasSize(1);
        assertThat(articles).extracting(Article::getSource).containsOnly(ArticleSource.NAVER);
        assertThat(articles.get(0).getPublishDate())
                .isAfter(LocalDate.now().minusDays(7).atStartOfDay())
                .isBefore(LocalDate.now().plusDays(1).atStartOfDay());
    }
}