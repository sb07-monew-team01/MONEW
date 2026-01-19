package com.codeit.monew.domain.notification.dto.response;

import com.codeit.monew.domain.notification.entity.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @Schema(
                description = "알림 확인 여부 (목록 조회 시 기본 false)",
                example = "false"
        )
        boolean confirmed,
        UUID userId,
        String content,
        ResourceType resourceType,
        UUID resourceId

) {

}
