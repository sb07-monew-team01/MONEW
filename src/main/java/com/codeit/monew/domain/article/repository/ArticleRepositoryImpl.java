package com.codeit.monew.domain.article.repository;

import static com.codeit.monew.domain.article.entity.QArticle.article;

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
    public List<Article> findByKeywordAndSource(String keyword, List<ArticleSource> sources,
                                                LocalDateTime publishDateFrom, LocalDateTime publishDateTo) {
        return queryFactory
                .selectFrom(article)
                .where(
                        keywordContains(keyword),
                        sourceIn(sources),
                        startDate(publishDateFrom),
                        endDate(publishDateTo)
                )
                .fetch();
    }

    BooleanExpression keywordContains(String keyword) {
        return keyword != null ? article.title.contains(keyword)
                .or(article.summary.contains(keyword))
                : null;
    }

    BooleanExpression sourceIn(List<ArticleSource> sources) {
        if (sources == null || sources.isEmpty())
            return article.source.eq(ArticleSource.NAVER);
        return article.source.in(sources);
    }

    BooleanExpression startDate(LocalDateTime from) {
        if (from == null)
            from = LocalDate.now().minusDays(7).atStartOfDay();
        return article.publishDate.goe(from);
    }

    BooleanExpression endDate(LocalDateTime to) {
        if (to == null)
            to = LocalDate.now().plusDays(1).atStartOfDay();
        return article.publishDate.lt(to);
    }
}