package com.codeit.monew.domain.interest.dto.request;

import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public record InterestCursorPageRequest(
        String orderBy,
        String direction,
        String cursor,
        LocalDateTime after,
        Integer limit,
        String keyword
){
    public InterestCursorPageRequest {
        // 기본값 처리 (문자열 그대로)
        if (orderBy == null) orderBy = "name";
        if (direction == null) direction = "desc";
        if (limit == null) limit = 6;
    }

    public String keywordValue() {
        return StringUtils.hasText(keyword) ? keyword.trim() : null;
    }
}
