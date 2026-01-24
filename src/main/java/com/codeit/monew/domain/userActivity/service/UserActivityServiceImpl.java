package com.codeit.monew.domain.userActivity.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.user.exception.UserNotFoundException;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;
import com.codeit.monew.domain.userActivity.entity.*;
import com.codeit.monew.domain.userActivity.mapper.UserActivityDtoMapper;
import com.codeit.monew.domain.userActivity.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserActivityDtoMapper userActivityMapper;
    private final MongoTemplate mongoTemplate;

    private Query queryByUserId(UUID userId) {
        return Query.query(Criteria.where("userId").is(userId));
    }

    public UserActivityDto createUserActivity(User user) {
        UserActivity userActivity = new UserActivity(user);
        userActivityRepository.save(userActivity);
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto getByUserId(UUID userId) {
        UserActivity activity = userActivityRepository.getByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (activity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(activity);
    }

    public UserActivityDto addComment(UUID userId, Comment comment, Article article, Long commentUserLikeCount) {
        UserActivityComment newComment = new UserActivityComment(
                comment.getId(),
                article.getId(),
                article.getTitle(),
                userId,
                comment.getUser().getNickname(),
                comment.getContent(),
                commentUserLikeCount,
                comment.getCreatedAt()
        );

        Update update = new Update()
                .push("comments")
                .atPosition(Update.Position.FIRST)
                .slice(10)
                .value(newComment);
        UserActivity userActivity = mongoTemplate.findAndModify(
                queryByUserId(userId),
                update,
                FindAndModifyOptions.options().returnNew(true),
                UserActivity.class);
        if (userActivity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto addCommentLike(UUID userId, CommentUserLike commentLike, Long commentLikeCount) {
        Comment comment = commentLike.getComment();
        Article article = comment.getArticle();
        User commentUser = comment.getUser();
        UserActivityCommentLike newCommentLike = new UserActivityCommentLike(
                commentLike.getId(),
                commentLike.getCreatedAt(),
                comment.getId(),
                article.getId(),
                article.getTitle(),
                commentUser.getId(),
                commentUser.getNickname(),
                comment.getContent(),
                commentLikeCount,
                comment.getCreatedAt());
        Update update = new Update()
                .push("commentLikes")
                .atPosition(Update.Position.FIRST)
                .slice(10)
                .value(newCommentLike);
        UserActivity userActivity = mongoTemplate.findAndModify(
                queryByUserId(userId),
                update,
                FindAndModifyOptions.options().returnNew(true), // 변경 후 Document 반환
                UserActivity.class
        );
        if(userActivity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto addArticleView(UUID userId, ArticleView articleView) {
        Article article = articleView.getArticle();
        UserActivityArticleView newArticleView = new UserActivityArticleView(
                articleView.getId(),
                articleView.getUser().getId(),
                articleView.getCreatedAt(),
                article.getId(),
                article.getSource(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getPublishDate(),
                article.getSummary(),
                article.getCommentCount(),
                article.getViewCount()
        );
        Update update = new Update()
                .push("articleViews")
                .atPosition(Update.Position.FIRST)
                .slice(10)
                .value(newArticleView);

        UserActivity userActivity = mongoTemplate.findAndModify(
                queryByUserId(userId),
                update,
                FindAndModifyOptions.options().returnNew(true),
                UserActivity.class);
        if (userActivity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto addSubscription(UUID userId, Interest interest, InterestUser interestUser) {
        UserActivityInterestSubscription newSubscription = new UserActivityInterestSubscription(
                interestUser.getId(),
                interest.getId(),
                interest.getName(),
                interest.getKeywords().stream()
                        .map(Object::toString)
                        .toList(),
                interest.getSubscriberCount(),
                interestUser.getCreatedAt()
        );
        Update update = new Update()
                .push("subscriptions")
                .atPosition(Update.Position.FIRST)
                .slice(10)
                .value(newSubscription);
        UserActivity userActivity = mongoTemplate.findAndModify(
                queryByUserId(userId),
                update,
                FindAndModifyOptions.options().returnNew(true),
                UserActivity.class);
        if (userActivity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(userActivity);
    }

    public void removeSubscription(UUID userId, UUID interestId) {
        Update update = new Update()
                .pull("subscriptions", Query.query(Criteria.where("interestId").is(interestId)));
        mongoTemplate.updateFirst(queryByUserId(userId), update, UserActivity.class);
    }

    public void remove(UUID userId) {
        userActivityRepository.deleteByUserId(userId);
    }
}
