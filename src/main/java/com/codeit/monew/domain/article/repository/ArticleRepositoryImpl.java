package com.codeit.monew.domain.article.repository;

import com.codeit.monew.domain.article.dto.request.ArticleSearchCondition;
import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.article.entity.ArticleSource;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.codeit.monew.domain.article.entity.QArticle.article;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Article> findByKeywordAndSource(ArticleSearchCondition searchCondition) {

        int pageSize = searchCondition.limit();
        Pageable pageable = PageRequest.of(0, pageSize);

        BooleanExpression cursorCondition = cursorCondition(
                searchCondition.orderBy(),
                searchCondition.direction(),
                searchCondition.cursor(),
                searchCondition.after()
        );


        List<Article> contents = queryFactory
                .selectFrom(article)
                .where(
                        keywordContains(searchCondition.keyword()),
                        sourceIn(searchCondition.sourceIn()),
                        startDate(searchCondition.publishDateFrom()),
                        endDate(searchCondition.publishDateTo()),
                        cursorCondition
                )
                .orderBy(
                        articleSorts(searchCondition.orderBy(), searchCondition.direction())
                )
                .limit(pageSize +  1)
                .fetch();

        boolean hasNext = false;
        if (contents.size() > pageSize) {
            contents.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
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

    // 커서 페이징 (기본: 게시일, 내림차순)
    BooleanExpression cursorCondition(String orderBy,
                                      String direction,
                                      String cursor,
                                      LocalDateTime after) {

        if (cursor == null || after == null) return null;

        boolean isDesc = "DESC".equalsIgnoreCase(direction);

        // 게시일 정렬
        if ("publishDate".equalsIgnoreCase(orderBy)) {
            try {
                LocalDateTime date = LocalDateTime.parse(cursor);
                return isDesc
                        ? article.publishDate.lt(date)
                        .or(article.publishDate.eq(date).and(article.createdAt.lt(after)))
                        : article.publishDate.gt(date)
                        .or(article.publishDate.eq(date).and(article.createdAt.gt(after)));
            } catch (Exception e) {
                log.warn("커서 형식이 잘못됨: {}", cursor);
                return null;
            }
        }
        // 조회수 정렬
        else if ("viewCount".equalsIgnoreCase(orderBy)) {
            long count = Long.parseLong(cursor);
            return isDesc
                    ? article.viewCount.lt(count)
                    .or(article.viewCount.eq(count).and(article.createdAt.lt(after)))
                    : article.viewCount.gt(count)
                    .or(article.viewCount.eq(count).and(article.createdAt.gt(after)));
        }
        // 댓글수 정렬
        else {
            long count = Long.parseLong(cursor);
            return isDesc
                    ? article.commentCount.lt(count)
                    .or(article.commentCount.eq(count).and(article.createdAt.lt(after)))
                    : article.commentCount.gt(count)
                    .or(article.commentCount.eq(count).and(article.createdAt.gt(after)));
        }
    }

    private OrderSpecifier<?>[] articleSorts(String orderBy, String direction) {

        Order order = "DESC".equalsIgnoreCase(direction) ? Order.DESC : Order.ASC;

        OrderSpecifier<?> mainSort = switch (orderBy) {
            case "viewCount" -> new OrderSpecifier<>(order, article.viewCount);
            case "commentCount" -> new OrderSpecifier<>(order, article.commentCount);
            default -> new OrderSpecifier<>(order, article.publishDate);
        };

        OrderSpecifier<?> subSort = new OrderSpecifier<>(order, article.createdAt);

        return new OrderSpecifier[] { mainSort, subSort};
    }

    @Override
    public long countTotalElements(ArticleSearchCondition searchCondition) {

        Long total =  queryFactory
                .select(article.count())
                .from(article)
                .where(
                        keywordContains(searchCondition.keyword()),
                        sourceIn(searchCondition.sourceIn()),
                        startDate(searchCondition.publishDateFrom()),
                        endDate(searchCondition.publishDateTo())
                )
                .fetchOne();

        return total != null ? total : 0L;
    }
}