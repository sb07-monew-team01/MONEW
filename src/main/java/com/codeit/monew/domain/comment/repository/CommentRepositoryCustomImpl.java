package com.codeit.monew.domain.comment.repository;

import com.codeit.monew.domain.comment.dto.request.CommentOrderBy;
import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import com.codeit.monew.domain.comment.entity.QComment;
import com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    @Override
    public Slice<CommentWithLikeCount> findByArticleIdOrderBy(
            UUID articleId,
            CommentOrderBy orderBy,
            String cursor,
            int limit
    ) {
        QComment comment = QComment.comment;
        QCommentUserLike like = QCommentUserLike.commentUserLike;

        BooleanExpression cursorCondition = buildCreatedAtCursorCondition(cursor);

        List<CommentWithLikeCount> results =
                queryFactory
                        .select(Projections.constructor(
                                CommentWithLikeCount.class,
                                comment,
                                like.count()
                        ))
                        .from(comment)
                        .leftJoin(like).on(like.comment.eq(comment))
                        .where(
                                comment.article.id.eq(articleId),
                                comment.deletedAt.isNull(),
                                cursorCondition
                        )
                        .groupBy(comment.id)
                        .orderBy(
                                // 1차 정렬
                                orderBy == CommentOrderBy.likeCount
                                        ? like.count().desc()
                                        : comment.createdAt.desc(),
                                // 2차 정렬: 등록순(최신순) 고정
                                comment.createdAt.desc(),
                                // 3차 정렬: 결정성 보장
                                comment.id.desc()
                        )
                        .limit(limit + 1)
                        .fetch();

        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results.remove(limit);
        }

        return new SliceImpl<>(
                results,
                PageRequest.of(0, limit),
                hasNext
        );
    }

    /**
     * 커서 페이징 조건
     * createdAt DESC, id DESC 기준
     */
    private BooleanExpression buildCreatedAtCursorCondition(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);
        QComment c = QComment.comment;

        LocalDateTime cursorCreatedAt =
                queryFactory
                        .select(c.createdAt)
                        .from(c)
                        .where(c.id.eq(cursorId))
                        .fetchOne();

        if (cursorCreatedAt == null) {
            return null;
        }

        return c.createdAt.lt(cursorCreatedAt)
                .or(
                        c.createdAt.eq(cursorCreatedAt)
                                .and(c.id.lt(cursorId))
                );
    }
}
