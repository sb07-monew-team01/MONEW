package com.codeit.monew.domain.notification.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
public record NotificationPageQuery(

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}_[0-9a-fA-F\\-]{36}$",
                message = "invalid cursor")
        String cursor,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime after,

        @Min(1)
        @Max(50)
        Integer limit
) {
}
