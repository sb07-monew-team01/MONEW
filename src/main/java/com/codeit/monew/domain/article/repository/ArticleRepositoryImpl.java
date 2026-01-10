package com.codeit.monew.domain.article.repository;

import static com.codeit.monew.domain.article.entity.QArticle.article;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.dto.request.ArticleSearchRequest;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Article> findByKeywordAndSource(ArticleSearchCondition searchCondition) {

        return queryFactory
                .selectFrom(article)
                .where(
                        keywordContains(searchCondition.keyword()),
                        sourceIn(searchCondition.sourceIn()),
                        startDate(searchCondition.publishDateFrom()),
                        endDate(searchCondition.publishDateTo())
                )
                .fetch();
    }

    // 키워드를 포함하는 기사 제목, 요약 조회
    BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return article.title.contains(keyword)
                .or(article.summary.contains(keyword));
    }

    // 해당하는 출처의 기사 조회
    BooleanExpression sourceIn(List<ArticleSource> sources) {
        if (sources == null || sources.isEmpty())
            return article.source.eq(ArticleSource.NAVER);
        return article.source.in(sources);
    }

    // 날짜 시작 범위 (기본: 7일전)
    BooleanExpression startDate(LocalDateTime date) {
        LocalDateTime from = date != null ? date : LocalDate.now().minusDays(7).atStartOfDay();
        return article.publishDate.goe(from);
    }

    // 날짜 끝 범위 (기본: 오늘)
    BooleanExpression endDate(LocalDateTime date) {
        LocalDateTime to = date != null ? date : LocalDate.now().plusDays(1).atStartOfDay();
        return article.publishDate.lt(to);
    }
}