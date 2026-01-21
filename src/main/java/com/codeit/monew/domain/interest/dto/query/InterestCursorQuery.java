package com.codeit.monew.domain.interest.dto.query;

import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;

import java.time.LocalDateTime;

public record InterestCursorQuery(
        InterestOrderBy orderBy,        // 정렬 기준
        SortDirection direction,        // 정렬 방향
        String nameCursor,              // 주 커서 값
        Long subscriberCountCursor,
        LocalDateTime after,       // 보조 커서 값
        Integer limit,       // 한 페이지 당 보이는 갯수
        String keyword      // 검색 키워드
) {
    public InterestCursorQuery {
        if (orderBy == InterestOrderBy.NAME && subscriberCountCursor != null) {
            throw new IllegalArgumentException(
                    "NAME 정렬에는 subscriberCountCursor를 사용할 수 없습니다."
            );
        }

        if (orderBy == InterestOrderBy.SUBSCRIBERCOUNT && nameCursor != null) {
            throw new IllegalArgumentException(
                    "SUBSCRIBERCOUNT 정렬에는 nameCursor를 사용할 수 없습니다."
            );
        }
    }
}

