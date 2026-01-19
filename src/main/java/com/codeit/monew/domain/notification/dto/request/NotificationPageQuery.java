package com.codeit.monew.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
public record NotificationPageQuery(

        @Schema(
                description = "커서 (createdAt_UUID)",
                example = "2026-01-18T10:15:30_7a187ac2-87f7-44d3-a8fb-b1936b1f0000"
        )
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}_[0-9a-fA-F\\-]{36}$",
                message = "형식에 어긋납니다")
        String cursor,

        @Schema(
                description = "보조 커서(createdAt)",
                example = "2026-01-18T10:15:30"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime after,

        @Schema(
                description = "페이지 크기 (1~50)",
                example = "20"
        )
        @Min(value = 1, message = "limit은 1 이상이어야 합니다")
        @Max(value = 50, message = "limit은 50 이하여야 합니다")
        Integer limit
) {
}
