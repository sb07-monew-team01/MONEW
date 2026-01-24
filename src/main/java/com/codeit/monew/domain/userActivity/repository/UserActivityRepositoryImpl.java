package com.codeit.monew.domain.userActivity.repository;

import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.mapper.UserActivityMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.codeit.monew.domain.article.entity.QArticle.article;
import static com.codeit.monew.domain.articleView.entity.QArticleView.articleView;
import static com.codeit.monew.domain.comment.entity.QComment.comment;
import static com.codeit.monew.domain.commentuserlike.entity.QCommentUserLike.commentUserLike;
import static com.codeit.monew.domain.interest.entity.QInterest.interest;
import static com.codeit.monew.domain.interestkeyword.entity.QInterestKeyword.interestKeyword;
import static com.codeit.monew.domain.interestuser.entity.QInterestUser.interestUser;
import static com.codeit.monew.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserActivityRepositoryImpl implements UserActivityRepository {

    private final JPAQueryFactory queryFactory;
    private final UserActivityMapper userActivityMapper;

    @Override
    public UserActivityDto getUserActivity(UUID userId) {
        User userEntity = queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (userEntity == null)
            return null;

        // 구독 관심사
        List<Interest> interests = queryFactory
                .select(interest)
                .from(interestUser)
                .join(interestUser.interest, interest)
                .leftJoin(interest.keywords, interestKeyword).fetchJoin()
                .where(interestUser.user.id.eq(userId))
                .fetch();

        // 최근 작성 댓글 10개
        List<Comment> recentComments = queryFactory
                .selectFrom(comment)
                .join(comment.article, article).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(
                        comment.user.id.eq(userId),
                        comment.deletedAt.isNull()
                ).orderBy(comment.createdAt.desc())
                .limit(10)
                .fetch();

        // 최근 좋아요한 댓글 10개
        List<CommentUserLike> recentLikedComments = queryFactory
                .selectFrom(commentUserLike)
                .join(commentUserLike.comment, comment).fetchJoin()
                .join(comment.article, article).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(
                        commentUserLike.user.id.eq(userId),
                        comment.deletedAt.isNull()
                ).orderBy(commentUserLike.createdAt.desc())
                .limit(10)
                .fetch();

        // 최근 본 기사 10개
        List<ArticleView> recentViewedArticles = queryFactory
                .selectFrom(articleView)
                .join(articleView.article, article).fetchJoin()
                .where(
                        articleView.user.id.eq(userId),
                        article.deletedAt.isNull()
                ).orderBy(articleView.createdAt.desc())
                .limit(10)
                .fetch();

        return userActivityMapper.toDto(
                userEntity,
                interests,
                recentComments,
                recentLikedComments,
                recentViewedArticles
        );
    }
}
