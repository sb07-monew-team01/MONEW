package com.codeit.monew.domain.userActivity.service;

import com.codeit.monew.domain.article.entity.Article;
import com.codeit.monew.domain.articleView.entity.ArticleView;
import com.codeit.monew.domain.comment.entity.Comment;
import com.codeit.monew.domain.commentuserlike.entity.CommentUserLike;
import com.codeit.monew.domain.interest.entity.Interest;
import com.codeit.monew.domain.interestuser.entity.InterestUser;
import com.codeit.monew.domain.user.entity.User;
import com.codeit.monew.domain.userActivity.dto.UserActivityDto;

import java.util.UUID;

public interface UserActivityService {
    UserActivityDto createUserActivity(User user);
    UserActivityDto getByUserId(UUID userId);
    UserActivityDto addComment(UUID userId, Comment comment, Article article, Long commentUserLikeCount);
    UserActivityDto addCommentLike(UUID userId, CommentUserLike commentLike, Long commentLikeCount);
    UserActivityDto addArticleView(UUID userId, ArticleView articleView);
    UserActivityDto addSubscription(UUID userId, Interest interest, InterestUser interestUser);
    void removeSubscription(UUID userId, UUID interestId);
    void remove(UUID userId);
}
