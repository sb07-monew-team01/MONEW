package com.codeit.monew.domain.userActivity.entity;

import com.codeit.monew.domain.user.entity.User;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Document(collection = "user_activities")
public class UserActivity {
    @Id
    private String id;

    private UUID user_id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private List<UserActivityInterestSubscription> subscriptions;
    private List<UserActivityComment> comments;
    private List<UserActivityCommentLike> commentLikes;
    private List<UserActivityArticle> articleViews;

    public UserActivity(User user) {
        this.user_id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt();
        this.subscriptions = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.commentLikes = new ArrayList<>();
        this.articleViews = new ArrayList<>();
    }
}
