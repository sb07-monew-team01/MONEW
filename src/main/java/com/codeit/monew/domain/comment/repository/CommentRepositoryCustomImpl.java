package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import com.codeit.monew.domain.comment.entity.QComment;
import com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QComment comment = QComment.comment;
    private static final QCommentUserLike like = QCommentUserLike.commentUserLike;

    @Override
    public Slice<CommentWithLikeCount> findByArticleIdOrderBy(
            UUID articleId,
            CommentOrderBy orderBy,
            String cursor,
            int limit
    ) {
        NumberExpression<Long> likeCountExpr = like.id.countDistinct();

        BooleanExpression whereCondition =
                comment.article.id.eq(articleId)
                        .and(comment.deletedAt.isNull());

        BooleanExpression havingCondition = null;

        if (orderBy == CommentOrderBy.likeCount && cursor != null) {
            String[] parts = cursor.split("_");
            if (parts.length == 3) {
                long cursorLikeCount = Long.parseLong(parts[0]);
                LocalDateTime cursorCreatedAt = LocalDateTime.parse(parts[1]);
                UUID cursorId = UUID.fromString(parts[2]);

                havingCondition =
                        likeCountExpr.lt(cursorLikeCount)
                                .or(
                                        likeCountExpr.eq(cursorLikeCount)
                                                .and(
                                                        comment.createdAt.lt(cursorCreatedAt)
                                                                .or(
                                                                        comment.createdAt.eq(cursorCreatedAt)
                                                                                .and(comment.id.lt(cursorId))
                                                                )
                                                )
                                );
            }
        }

        if (orderBy == CommentOrderBy.createdAt && cursor != null) {
            String[] parts = cursor.split("_");
            if (parts.length == 2) {
                LocalDateTime cursorCreatedAt = LocalDateTime.parse(parts[0]);
                UUID cursorId = UUID.fromString(parts[1]);

                whereCondition =
                        whereCondition.and(
                                comment.createdAt.lt(cursorCreatedAt)
                                        .or(
                                                comment.createdAt.eq(cursorCreatedAt)
                                                        .and(comment.id.lt(cursorId))
                                        )
                        );
            }
        }

        List<CommentWithLikeCount> results =
                queryFactory
                        .select(Projections.constructor(
                                CommentWithLikeCount.class,
                                comment,
                                likeCountExpr
                        ))
                        .from(comment)
                        .leftJoin(like).on(like.comment.eq(comment))
                        .where(whereCondition)
                        .groupBy(comment.id)
                        .having(havingCondition)
                        .orderBy(orderSpecifiers(orderBy))
                        .limit(limit + 1)
                        .fetch();

        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results.remove(limit);
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }

    private OrderSpecifier<?>[] orderSpecifiers(CommentOrderBy orderBy) {
        return switch (orderBy) {
            case createdAt -> new OrderSpecifier<?>[]{
                    comment.createdAt.desc(),
                    comment.id.desc()
            };
            case likeCount -> new OrderSpecifier<?>[]{
                    like.id.countDistinct().desc(),
                    comment.createdAt.desc(),
                    comment.id.desc()
            };
        };
    }
}
