package com.codeit.monew.article.repository;

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
        Article article1 = Article.builder()
                .title("네이버 뉴스1")
                .source(ArticleSource.NAVER)
                .sourceUrl("a")
                .summary("a")
                .publishDate(LocalDateTime.now())
                .build();
        Article article2 = Article.builder()
                .title("네이버 뉴스2")
                .source(ArticleSource.NAVER)
                .sourceUrl("b")
                .summary("b")
                .publishDate(LocalDateTime.now().minusDays(14))
                .build();
        Article article3 = Article.builder()
                .title("한경 뉴스")
                .source(ArticleSource.HANKYUNG)
                .sourceUrl("c")
                .summary("c")
                .publishDate(LocalDateTime.now())
                .build();
        Article article4 = Article.builder()
                .title("조선 뉴스")
                .source(ArticleSource.CHOSUN)
                .sourceUrl("d")
                .summary("d")
                .publishDate(LocalDateTime.now().plusDays(2))
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.save(article4);

        String keyword = null;
        List<ArticleSource> sources = null;
        LocalDateTime publishDateFrom = null;
        LocalDateTime publishDateTo = null;

        // when
        List<Article> articles = articleRepository.findByKeywordAndSource(keyword, sources, publishDateFrom, publishDateTo);

        // then
        assertThat(articles).hasSize(1);
        assertThat(articles).extracting(Article::getSource).containsOnly(ArticleSource.NAVER);
        assertThat(articles.get(0).getPublishDate())
                .isAfter(LocalDate.now().minusDays(7).atStartOfDay())
                .isBefore(LocalDate.now().plusDays(1).atStartOfDay());
    }
}