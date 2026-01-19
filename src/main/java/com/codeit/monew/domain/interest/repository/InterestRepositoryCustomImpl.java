package com.codeit.monew.domain.interest.repository;

import com.codeit.monew.domain.interest.dto.InterestCursorQuery;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interest.vo.AfterCursorValue;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        AfterCursorValue after = parseAfter(query.after());
        boolean asc = query.direction().isAsc();
        BooleanExpression cursorCondition;

        if (query.orderBy() == null) {
            throw new IllegalArgumentException("정렬 기준이 없습니다.");
        }

        cursorCondition = switch (query.orderBy()) {
            case NAME -> {
                if (query.nameCursor() == null) yield null;
                if (asc) {
                    yield interest.name.gt(query.nameCursor())
                        .or(interest.name.eq(query.nameCursor()).and(interest.createdAt.gt(after.createdAt())
                        .or(interest.createdAt.eq(after.createdAt()).and(interest.id.gt(after.id())))));
                } else {
                    yield interest.name.lt(query.nameCursor())
                        .or(interest.name.eq(query.nameCursor()).and(interest.createdAt.lt(after.createdAt())
                        .or(interest.createdAt.eq(after.createdAt()).and(interest.id.lt(after.id())))));
                }
            }
            case SUBSCRIBER_COUNT -> {
                if (query.subscriberCountCursor() == null) yield null;
                if (asc) {
                    yield interest.subscriberCount.gt(query.subscriberCountCursor())
                            .or(interest.subscriberCount.eq(query.subscriberCountCursor())
                                    .and(interest.createdAt.gt(after.createdAt())
                            .or(interest.createdAt.eq(after.createdAt())
                                    .and(interest.id.gt(after.id())))));
                } else {
                    yield interest.subscriberCount.lt(query.subscriberCountCursor())
                            .or(interest.subscriberCount.eq(query.subscriberCountCursor())
                                    .and(interest.createdAt.lt(after.createdAt())
                            .or(interest.createdAt.eq(after.createdAt())
                                    .and(interest.id.lt(after.id())))));
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
            case SUBSCRIBER_COUNT -> primary = asc ? interest.subscriberCount.asc() : interest.subscriberCount.desc();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        }

        if (asc) {
            return new OrderSpecifier[]{primary, interest.createdAt.asc(), interest.id.asc()};
        } else {
            return new OrderSpecifier[]{primary, interest.createdAt.desc(), interest.id.desc()};
        }
    }

    // after(createdAt_id) 파싱만 담당
    private AfterCursorValue parseAfter(String after) {
        if (after == null || after.isBlank()) return null;
        String[] parts = after.split("_");
        if (parts.length != 2) throw new IllegalArgumentException("잘못된 after 커서 형식입니다.");

        return new AfterCursorValue(LocalDateTime.parse(parts[0]), UUID.fromString(parts[1]));
    }
}