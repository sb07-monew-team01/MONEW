package com.codeit.monew.domain.notification.entity;

import com.codeit.monew.domain.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseUpdatableEntity {
    private UUID userId;
    private UUID resourceId;
    private ResourceType resourceType;
    private String content;
    private Boolean confirmed;


    public Notification(UUID userId, UUID resourceId, ResourceType resourceType,String content) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.content = content;
        this.confirmed = false;
    }



    public void confirm() {
        this.confirmed = true;
    }
}
