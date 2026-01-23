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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityServiceImpl {

    private final UserActivityRepository userActivityRepository;
    private final UserActivityDtoMapper userActivityMapper;

    public UserActivityDto createUserActivity(User user) {
        UserActivity userActivity = new UserActivity(user);
        userActivityRepository.save(userActivity);
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto getByUserId(UUID userId) {
        UserActivity activity = userActivityRepository.getByUser_id(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (activity == null)
            throw new UserNotFoundException(userId);
        return userActivityMapper.toDto(activity);
    }

    public UserActivityDto addComment(UUID userId, Comment comment, Article article, Long commentUserLikeCount) {
        UserActivity activity = userActivityRepository.getByUser_id(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (activity.getComments().size() > 10) {
            activity.getComments().remove(0);
        }
        activity.getComments().add(new UserActivityComment(
                comment.getId(),
                article.getId(),
                article.getTitle(),
                activity.getUser_id(),
                activity.getNickname(),
                comment.getContent(),
                commentUserLikeCount,
                comment.getCreatedAt()
        ));
        return userActivityMapper.toDto(activity);
    }

    public UserActivityDto addCommentLike(UUID userId, CommentUserLike commentLike, Long commentLikeCount) {
        UserActivity activity = userActivityRepository.getByUser_id(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (activity.getCommentLikes().size() > 10) {
            activity.getCommentLikes().remove(0);
        }
        Comment comment = commentLike.getComment();
        Article article = comment.getArticle();
        User commentUser = comment.getUser();
        activity.getCommentLikes().add(new UserActivityCommentLike(
                commentLike.getId(),
                commentLike.getCreatedAt(),
                comment.getId(),
                article.getId(),
                article.getTitle(),
                commentUser.getId(),
                commentUser.getNickname(),
                comment.getContent(),
                commentLikeCount,
                comment.getCreatedAt()));
        return userActivityMapper.toDto(activity);
    }

    public UserActivityDto addArticleView(UUID userId, ArticleView articleView) {
        UserActivity userActivity = userActivityRepository.getByUser_id(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        List<UserActivityArticle> articleViews = userActivity.getArticleViews();
        if (articleViews.size() > 10) {
            articleViews.remove(0);
        }

        Article article = articleView.getArticle();
        articleViews.add(new UserActivityArticle(
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
        ));
        return userActivityMapper.toDto(userActivity);
    }

    public UserActivityDto addSubscription(UUID userId, Interest interest, InterestUser interestUser) {
        UserActivity userActivity = userActivityRepository.getByUser_id(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userActivity.getSubscriptions().add(new UserActivityInterestSubscription(
                interestUser.getId(),
                interest.getId(),
                interest.getName(),
                interest.getKeywords().stream()
                        .map(Object::toString)
                        .toList(),
                interest.getSubscriberCount(),
                interestUser.getCreatedAt()
        ));
        return userActivityMapper.toDto(userActivity);
    }

    public void remove(UUID userId) {
        userActivityRepository.deleteByUser_id(userId);
    }
}
