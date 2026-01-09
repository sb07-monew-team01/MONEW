package com.codeit.monew.domain.notification.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notifications")
public class Notification extends BaseUpdatableEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID resourceId;

    @Column(nullable = false,length = 30)
    private ResourceType resourceType;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean confirmed;

    private Notification(UUID userId, UUID resourceId, ResourceType resourceType, String content) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.content = content;
        this.confirmed = false;
    }

    public static Notification forCommentLike(UUID userId, UUID commentId, String likerName) {

        String content = ResourceType.COMMENT.format(likerName);

        return new Notification(userId, commentId, ResourceType.COMMENT, content);
    }

    public static Notification forInterest(UUID userId, UUID interestId, String interestName, int count) {

        String content = ResourceType.INTEREST.format(interestName, count);

        return new Notification(userId, interestId, ResourceType.INTEREST, content);
    }


    public void confirm() {
        this.confirmed = true;
    }
}
