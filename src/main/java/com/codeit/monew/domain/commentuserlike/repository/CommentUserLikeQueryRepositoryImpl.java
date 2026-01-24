package com.codeit.monew.domain.commentuserlike.repository;

import com.codeit.monew.domain.comment.dto.response.CommentWithLikeCount;
import com.codeit.monew.domain.comment.entity.QComment;
import com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CommentUserLikeQueryRepositoryImpl implements CommentUserLikeQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentWithLikeCount> findCommentsOrderByLikeCount(UUID articleId, int limit) {
        QComment comment = QComment.comment;
        QCommentUserLike like = QCommentUserLike.commentUserLike;

        return queryFactory
                .select(Projections.constructor(
                        CommentWithLikeCount.class,
                        comment,
                        like.id.countDistinct()
                ))
                .from(like)
                .join(like.comment, comment)
                .where(
                        comment.article.id.eq(articleId),
                        comment.deletedAt.isNull()
                )
                .groupBy(comment.id)
                .orderBy(
                        like.id.countDistinct().desc(),
                        comment.createdAt.desc(),
                        comment.id.desc()
                )
                .limit(limit)
                .fetch();
    }
}