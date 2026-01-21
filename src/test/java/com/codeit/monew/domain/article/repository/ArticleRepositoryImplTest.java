package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.fixture.ArticleCreateRequestFixture;
import com.codeit.monew.domain.article.fixture.ArticleFixture;
import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.codeit.monew.global.config.TestJpaAuditing;
import com.codeit.monew.global.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQueryDslConfig.class, TestJpaAuditing.class})
@ActiveProfiles("test")
class ArticleRepositoryImplTest {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TestEntityManager entityManager;

    private void saveArticle(int count) {
        for (int i = 0; i < count; i++) {
            articleRepository.save(ArticleFixture.createWithViewAndComment(
                    ArticleCreateRequestFixture.createDummy(0, -i), i+1, i));
        }
        entityManager.flush();
        entityManager.clear();
    }

    private String createCursor(Article article, String orderBy) {
        return switch (orderBy) {
            case "viewCount" -> article.getViewCount() + "_" + article.getId();
            case "commentCount" -> article.getCommentCount() + "_" + article.getId();
            default -> article.getPublishDate().toString() + "_" + article.getId();
        };
    }

    @Nested
    @DisplayName("정렬 순서에 따른 주 정렬, 보조 정렬 검증")
    class CursorAndAfterValidationWithOrderBy {

        @ParameterizedTest
        @CsvSource({
                "publishDate, DESC", "publishDate, ASC",
                "viewCount, DESC", "viewCount, ASC",
                "commentCount, DESC", "commentCount, ASC"
        })
        @DisplayName("""
            모든 정렬조건(게시일, 조회수, 댓글수) 및
            모든 순서(내림차순, 오름차순)로 기사 목록을 정렬한다.
            """)
        void cursorPagingWithAllCase(String orderBy, String direction) {
            // given
            saveArticle(10);

            ArticleSearchCondition cond = ArticleSearchCondition.builder()
                    .orderBy(orderBy)
                    .direction(direction)
                    .limit(5)
                    .build();


            Slice<Article> firstPage = articleRepository.findByKeywordsAndSources(cond);
            List<Article> firstPageContent = firstPage.getContent();

            Article lastArticle = firstPageContent.get(firstPageContent.size()-1);
            String cursor = createCursor(lastArticle, orderBy);
            LocalDateTime after = lastArticle.getCreatedAt();

            ArticleSearchCondition cond2 = ArticleSearchCondition.builder()
                    .cursor(cursor)
                    .after(after)
                    .orderBy(orderBy)
                    .direction(direction)
                    .limit(5)
                    .build();

            Slice<Article> secondPage = articleRepository.findByKeywordsAndSources(cond2);
            List<Article> secondPageContent = secondPage.getContent();

            // then
            assertThat(firstPageContent).hasSize(5);
            assertThat(secondPageContent).hasSize(3);   // 기본 날짜 끝 범위

            if ("publishDate".equals(orderBy)) {
                if ("DESC".equals(direction))
                    assertThat(secondPageContent.get(0).getPublishDate())
                            .isBefore(firstPageContent.get(4).getPublishDate());
                else
                    assertThat(secondPageContent.get(0).getPublishDate())
                            .isAfter(firstPageContent.get(4).getPublishDate());
            }
            else {
                if ("DESC".equals(direction))
                    assertThat(getValue(secondPageContent.get(0), orderBy))
                            .isLessThan(getValue(firstPageContent.get(4), orderBy));
                else
                    assertThat(getValue(secondPageContent.get(0), orderBy))
                            .isGreaterThan(getValue(firstPageContent.get(4), orderBy));
            }

        }
        private long getValue(Article article, String orderBy) {
            if ("viewCount".equals(orderBy)) return article.getViewCount();
            else return article.getCommentCount();
        }
        
        @ParameterizedTest
        @CsvSource({
                "publishDate",
                "viewCount",
                "commentCount"
        })
        @DisplayName("잘못된 형식의 커서가 들어오면 아무것도 조회하지 않는다.")
        void cursorPagingWithWrongFormat_EmptyList(String orderBy) {
            // given
            ArticleSearchCondition cond1 = ArticleSearchCondition.builder()
                    .cursor("12")   // _id의 형태가 아님
                    .after(LocalDateTime.now())
                    .orderBy(orderBy)
                    .limit(5)
                    .build();

            UUID id = UUID.randomUUID();
            ArticleSearchCondition cond2 = ArticleSearchCondition.builder()
                    .cursor("Number_" + id) // 파싱 에러
                    .after(LocalDateTime.now())
                    .orderBy(orderBy)
                    .limit(5)
                    .build();

            // when & then
            assertThat(articleRepository.findByKeywordsAndSources(cond1).getContent()).isEmpty();
            assertThat(articleRepository.findByKeywordsAndSources(cond2).getContent()).isEmpty();
        }
        
        @Test
        @DisplayName("""
                주 정렬이 null이거나, 보조정렬이 null 이라면 
                다른 정렬 기준과 관계없이 전체 조회한다.
                """)
        void searchAllArticles_CursorOrAfterIsNull() {
            // given
            saveArticle(5);

            ArticleSearchCondition cond1 = ArticleSearchCondition.builder()
                    .cursor(null)
                    .after(LocalDateTime.now().minusDays(5))
                    .limit(10)
                    .build();

            ArticleSearchCondition cond2 = ArticleSearchCondition.builder()
                    .cursor(LocalDateTime.now().minusDays(5).toString())
                    .after(null)
                    .limit(10)
                    .build();
            // when
            List<Article> content1 = articleRepository.findByKeywordsAndSources(cond1).getContent();
            List<Article> content2 = articleRepository.findByKeywordsAndSources(cond2).getContent();
            
            // then
            assertThat(content1).hasSize(5);
            assertThat(content2).hasSize(5);
            
        }
    }

    @Nested
    @DisplayName("""
            source, keywords 검증
            """)
    class SourceAndKeywordsValidation {

        @ParameterizedTest
        @CsvSource({
                "NAVER",
                "HANKYUNG"
        })
        @DisplayName("""
                지정된 날짜 범위 내
                여러 출처(NAVER, HANKYUNG)로
                검색어를 통해 조회한다.
                """)
        void cursorPagingWithMultipleSourcesAndDate(String source) {
            // given
            for(int i = 0; i < 10; i++){
                articleRepository.save(ArticleFixture.createEntity(ArticleCreateRequestFixture.createDummy(i%2, -i)));
            }
            entityManager.flush();
            entityManager.clear();

            List<String> keywords = List.of("뉴스");
            List<ArticleSource> sources = List.of("NAVER".equals(source) ? ArticleSource.NAVER : ArticleSource.HANKYUNG);
            LocalDateTime start = LocalDate.now().minusDays(10).atStartOfDay();
            LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

            // when
            ArticleSearchCondition cond = ArticleSearchCondition.builder()
                    .keywords(keywords)
                    .sourceIn(sources)
                    .publishDateFrom(start)
                    .publishDateTo(end)
                    .limit(5)
                    .build();

            Slice<Article> firstPage = articleRepository.findByKeywordsAndSources(cond);
            List<Article> firstPageContent = firstPage.getContent();

            String cursor = createCursor(firstPageContent.get(firstPageContent.size()-1), "publishDate");
            LocalDateTime after = firstPageContent.get(firstPageContent.size()-1).getCreatedAt();

            ArticleSearchCondition cond2 = ArticleSearchCondition.builder()
                    .keywords(keywords)
                    .sourceIn(sources)
                    .cursor(cursor)
                    .after(after)
                    .publishDateFrom(start)
                    .publishDateTo(end)
                    .limit(5)
                    .build();


            // then
            assertThat(firstPageContent).hasSize(5);
            if ("NAVER".equals(source))
                assertThat(firstPageContent.get(0).getSource()).isEqualTo(ArticleSource.NAVER);
            else
                assertThat(firstPageContent.get(0).getSource()).isEqualTo(ArticleSource.HANKYUNG);

            assertThat(firstPageContent.get(1).getPublishDate())
                    .isBefore(firstPageContent.get(2).getPublishDate())
                            .isAfter(firstPageContent.get(0).getPublishDate());
            assertThat(articleRepository.findByKeywordsAndSources(cond2)).isEmpty();
        }
        
        @Test
        @DisplayName("""
                출처가 null이거나 비어있다면 네이버 기사를,
                검색어가 null이거나 비어있으면 전체 조회한다.
                """)
        void searchAllArticles_KeywordsEmptyOrNull() {
            // given
            saveArticle(5);

            ArticleSearchCondition cond1 = ArticleSearchCondition.builder()
                    .keywords(List.of())
                    .sourceIn(List.of())
                    .limit(10)
                    .build();

            ArticleSearchCondition cond2 = ArticleSearchCondition.builder()
                    .keywords(null)
                    .sourceIn(null)
                    .limit(10)
                    .build();

            // when
            List<Article> content1 = articleRepository.findByKeywordsAndSources(cond1).getContent();
            List<Article> content2 = articleRepository.findByKeywordsAndSources(cond2).getContent();

            // then
            assertThat(content1).hasSize(5);
            assertThat(content2).hasSize(5);
        }
    }

    @Nested
    @DisplayName("총 갯수 검증")
    class TotalCountValidation {

        @ParameterizedTest
        @CsvSource({"10", "0"})
        @DisplayName("""
                기사 목록의 총 갯수를 알 수 있다.
                목록이 비어있으면 0을 반환한다.
                """)
        void countArticleList(int count) {
            // given
            if (count > 0) saveArticle(count);

            ArticleSearchCondition cond = ArticleSearchCondition.builder()
                    .limit(5)
                    .build();

            // when
            long total = articleRepository.countTotalElements(cond);

            // then
            if (count == 0) assertThat(total).isEqualTo(0);
            else assertThat(total).isEqualTo(8);
        }
    }
}