package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import com.codeit.monew.domain.comment.entity.QComment;
import com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import com.querydsl.core.types.dsl.NumberExpression;

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
        // 커서 조건 생성
        BooleanExpression cursorCondition =
                switch (orderBy) {
                    case createdAt -> createdAtCursorCondition(cursor);
                    case likeCount -> null;
                };

        // 조회
        List<CommentWithLikeCount> results =
                queryFactory
                        .select(Projections.constructor(
                                CommentWithLikeCount.class,
                                comment,
                                like.id.countDistinct()
                        ))
                        .from(comment)
                        .leftJoin(like)
                        .on(like.comment.eq(comment))
                        .where(
                                comment.article.id.eq(articleId),
                                comment.deletedAt.isNull(),
                                cursorCondition
                        )
                        .groupBy(comment.id)
                        .orderBy(orderSpecifiers(orderBy))
                        .limit(limit + 1)
                        .fetch();

        boolean hasNext =
                orderBy == CommentOrderBy.createdAt
                && results.size() > limit;
        if (hasNext) {
            results.remove(limit);
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }

    private BooleanExpression createdAtCursorCondition(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        String[] parts = cursor.split("_");
        if (parts.length != 2) {
            return null;
        }

        LocalDateTime cursorCreatedAt = LocalDateTime.parse(parts[0]);
        UUID cursorId = UUID.fromString(parts[1]);

        return comment.createdAt.lt(cursorCreatedAt)
                .or(
                        comment.createdAt.eq(cursorCreatedAt)
                                .and(comment.id.lt(cursorId))
                );
    }


    private OrderSpecifier<?>[] orderSpecifiers(CommentOrderBy orderBy) {
        return switch (orderBy) {
            case createdAt -> new OrderSpecifier<?>[]{
                    comment.createdAt.desc(),
                    comment.id.desc()
            };
            case likeCount -> new OrderSpecifier<?>[]{
                    like.id.countDistinct().desc(),
                    comment.id.desc()
            };
        };
    }
}
