package com.codeit.monew.article.repository;

import com.codeit.monew.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

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
            게시일자를 기준으로 내림차순 정렬을 한다.
            """)
    void searchAllNaverArticle_EmptyKeyword_EmptySource() {
        // given
        for (int i = 0; i < 10; i++) {
            articleRepository.save(
                    ArticleFixture.createEntity(
                            ArticleCreateRequestFixture.createDummy(0, -i)
                    )
            );
        }

        ArticleSearchCondition searchCondition
                = ArticleSearchCondition.builder()
                .limit(5)
                .build();

        // when
        Page<Article> pages1 = articleRepository.findByKeywordAndSource(searchCondition);
        List<Article> articles1 = pages1.getContent();

        // then
        assertThat(articles1).hasSize(5);
        assertThat(articles1.get(2).getPublishDate())
                .isAfter(articles1.get(3).getPublishDate())
                .isBefore(articles1.get(1).getPublishDate());
        assertThat(pages1.hasNext()).isTrue();

        // when 2
        Object nextCursor = articles1.get(4).getPublishDate();
        LocalDateTime nextAfter = articles1.get(4).getCreatedAt();

        ArticleSearchCondition searchCondition2
                = ArticleSearchCondition.builder()
                .cursor(nextCursor)
                .after(nextAfter)
                .limit(5)
                .build();

        Page<Article> pages2 = articleRepository.findByKeywordAndSource(searchCondition2);
        List<Article> articles2 = pages2.getContent();

        // then 2
        assertThat(articles2).hasSize(3);
        assertThat(articles2.get(1).getPublishDate())
                .isBefore(articles2.get(0).getPublishDate());
        assertThat(pages2.hasNext()).isFalse();
    }
}