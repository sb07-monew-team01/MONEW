package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.request.CommentWithLikeCount;
import com.codeit.monew.domain.comment.dto.request.SortDirection;
import com.codeit.monew.domain.comment.entity.QComment;
import com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QComment comment = QComment.comment;
    private static final QCommentUserLike like = QCommentUserLike.commentUserLike;

    @Override
    public Slice<CommentWithLikeCount> findByCommentIdOrderBy(
            UUID articleId,
            CommentOrderBy orderBy,
            SortDirection direction,
            String cursor,
            LocalDateTime createdAt,
            int limit
    ) {
        Order sortOrder = direction == SortDirection.ASC ? Order.ASC : Order.DESC;

        Expression<? extends Comparable<?>> orderExpression =
                orderBy == CommentOrderBy.likeCount
                        ? like.id.count()
                        : comment.createdAt;

        OrderSpecifier<?> orderSpecifier =
                new OrderSpecifier<>(sortOrder, orderExpression);

        List<CommentWithLikeCount> results =
                queryFactory
                        .select(
                                Projections.constructor(
                                        CommentWithLikeCount.class,
                                        comment,
                                        like.id.count()
                                )
                        )
                        .from(comment)
                        .leftJoin(like).on(like.comment.eq(comment))
                        .where(
                                comment.article.id.eq(articleId),
                                comment.deletedAt.isNull(),
                                cursorCondition(cursor, createdAt, sortOrder)
                        )
                        .groupBy(comment.id)
                        .orderBy(orderSpecifier, comment.id.desc())
                        .limit(limit + 1)
                        .fetch();

        boolean hasNext = results.size() > limit;

        if (hasNext) {
            results.remove(limit);
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }

    private com.querydsl.core.types.Predicate cursorCondition(
            String cursor,
            LocalDateTime createdAt,
            Order order
    ) {
        if (cursor == null || createdAt == null) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);

        return order == Order.DESC
                ? comment.createdAt.lt(createdAt)
                .or(comment.createdAt.eq(createdAt).and(comment.id.lt(cursorId)))
                : comment.createdAt.gt(createdAt)
                .or(comment.createdAt.eq(createdAt).and(comment.id.gt(cursorId)));
    }
}
