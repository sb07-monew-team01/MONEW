package com.codeit.monew.domain.userActivity.event;

import com.codeit.monew.domain.userActivity.event.dto.*;
import com.codeit.monew.domain.userActivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserActivityEventListener {
    private final UserActivityService userActivityService;

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        userActivityService.createUserActivity(event.user());
    }

    @EventListener
    public void handleUserDeleted(UserDeletedEvent event) {
        userActivityService.remove(event.userId());
    }

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        userActivityService.addComment(
                event.userId(),
                event.comment(),
                event.article(),
                event.likeCount()
        );
    }

    @EventListener
    public void handleCommentLikeCreated(CommentLikeCreatedEvent event) {
        userActivityService.addCommentLike(event.userId(), event.commentLike(), event.likeCount());
    }

    @EventListener
    public void handleArticleViewed(ArticleViewedEvent event) {
        userActivityService.addArticleView(
                event.userId(),
                event.articleView()
        );
    }

    @EventListener
    public void handleInterestSubscribed(InterestSubscribedEvent event) {
        userActivityService.addSubscription(
                event.userId(),
                event.interest(),
                event.interestUser()
        );
    }

    @EventListener
    public void handleInterestUnsubscribed(InterestUnsubscribedEvent event) {
        userActivityService.removeSubscription(
                event.userId(),
                event.interestId()
        );
    }
}
