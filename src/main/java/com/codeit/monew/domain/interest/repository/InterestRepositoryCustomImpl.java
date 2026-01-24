package com.codeit.monew.domain.interest.repository;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.entity.Interest;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.codeit.monew.domain.interest.entity.QInterest.interest;

@Repository
@RequiredArgsConstructor
public class InterestRepositoryCustomImpl implements InterestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Slice<Interest> findAllByCursor(InterestCursorQuery query) {
        List<Interest> result = queryFactory
                .selectFrom(interest)
                .where(buildCursorCondition(query))
                .orderBy(buildOrderSpecifiers(query))
                .limit(query.limit() + 1)
                .fetch();

        boolean hasNext = result.size() > query.limit();

        if (hasNext) {
            result.remove(result.size() - 1);
        }

        return new SliceImpl<>(result, PageRequest.of(0, query.limit()), hasNext);
    }

    // 현재 커서 이후(또는 이전) 범위 조건: where 절
    private BooleanExpression buildCursorCondition(InterestCursorQuery query) {
        boolean asc = query.direction().isAsc();
        BooleanExpression cursorCondition;

        if (query.orderBy() == null) {
            throw new IllegalArgumentException("정렬 기준이 없습니다.");
        }

        cursorCondition = switch (query.orderBy()) {
            case NAME -> {
                if (query.nameCursor() == null) yield null;
                if (asc) {
                    if (query.after() != null) {
                        yield interest.name.gt(query.nameCursor())
                                .or(interest.name.eq(query.nameCursor())
                                        .and(interest.createdAt.gt(query.after())));
                    } else {
                        // after가 null이면 단순 name 비교
                        yield interest.name.gt(query.nameCursor());
                    }
                } else {
                    if (query.after() != null) {
                        yield interest.name.lt(query.nameCursor())
                                .or(interest.name.eq(query.nameCursor())
                                        .and(interest.createdAt.lt(query.after())));
                    } else {
                        yield interest.name.lt(query.nameCursor());
                    }
                }
            }
            case SUBSCRIBERCOUNT -> {
                if (query.subscriberCountCursor() == null) yield null;
                if (asc) {
                    if (query.after() != null) {
                        yield interest.subscriberCount.gt(query.subscriberCountCursor())
                                .or(interest.subscriberCount.eq(query.subscriberCountCursor())
                                        .and(interest.createdAt.gt(query.after())));
                    } else {
                        yield interest.subscriberCount.gt(query.subscriberCountCursor());
                    }
                } else {
                    if (query.after() != null) {
                        yield interest.subscriberCount.lt(query.subscriberCountCursor())
                                .or(interest.subscriberCount.eq(query.subscriberCountCursor())
                                        .and(interest.createdAt.lt(query.after())));
                    } else {
                        yield interest.subscriberCount.lt(query.subscriberCountCursor());
                    }
                }
            }
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        };

        if (query.keyword() != null && !query.keyword().isBlank()) {
            BooleanExpression keywordCondition = interest.name.containsIgnoreCase(query.keyword());
            cursorCondition = cursorCondition != null ? cursorCondition.and(keywordCondition) : keywordCondition;
        }

        return cursorCondition;
    }

    // order by 절
    private OrderSpecifier<?>[] buildOrderSpecifiers(InterestCursorQuery query) {
        boolean asc = query.direction().isAsc();
        OrderSpecifier<?> primary;

        switch (query.orderBy()) {
            case NAME -> primary = asc ? interest.name.asc() : interest.name.desc();
            case SUBSCRIBERCOUNT -> primary = asc ? interest.subscriberCount.asc() : interest.subscriberCount.desc();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        }

        if (asc) {
            return new OrderSpecifier[]{primary, interest.createdAt.asc(), interest.id.asc()};
        } else {
            return new OrderSpecifier[]{primary, interest.createdAt.desc(), interest.id.desc()};
        }
    }
}