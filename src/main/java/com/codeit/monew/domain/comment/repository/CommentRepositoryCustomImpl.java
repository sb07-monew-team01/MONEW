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

        BooleanExpression cursorCondition = buildCursorCondition(cursor);

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
                                cursorCondition
                        )
                        .groupBy(comment.id)
                        .orderBy(
                                orderBy == CommentOrderBy.likeCount
                                        ? like.count().desc()
                                        : comment.createdAt.desc(),
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
     * 커서 기반 페이징 (createdAt + id)
     */
    private BooleanExpression buildCursorCondition(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);
        QComment cursorComment = new QComment("cursorComment");

        var cursorCreatedAt =
                queryFactory
                        .select(cursorComment.createdAt)
                        .from(cursorComment)
                        .where(cursorComment.id.eq(cursorId));

        return QComment.comment.createdAt.lt(cursorCreatedAt)
                .or(
                        QComment.comment.createdAt.eq(cursorCreatedAt)
                                .and(QComment.comment.id.lt(cursorId))
                );
    }
}
