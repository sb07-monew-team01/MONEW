package com.codeit.monew.domain.interest.dto.request;

import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public record InterestCursorPageRequest(
        InterestOrderBy orderBy,
        SortDirection direction,
        String cursor,
        LocalDateTime after,
        Integer limit,
        String keyword
){
    public InterestCursorPageRequest {
        // 기본값 처리
        if (orderBy == null) orderBy = InterestOrderBy.NAME;
        if (direction == null) direction = SortDirection.ASC;
        if (limit == null) limit = 6;
    }

    public String keywordValue() {
        return StringUtils.hasText(keyword) ? keyword.trim() : null;
    }

    public String nameCursor() {
        return orderBy == InterestOrderBy.NAME ? cursor : null;
    }

    public Long subscriberCountCursor() {
        return orderBy == InterestOrderBy.SUBSCRIBER_COUNT && cursor != null
                ? Long.parseLong(cursor)
                : null;
    }
}
