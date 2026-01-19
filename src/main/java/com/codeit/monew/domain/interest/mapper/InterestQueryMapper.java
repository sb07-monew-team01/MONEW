package com.codeit.monew.domain.interest.mapper;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.springframework.stereotype.Component;

@Component
public class InterestQueryMapper {
    public InterestCursorQuery toQuery(InterestCursorPageRequest request) {
        InterestOrderBy orderBy = request.orderBy();
        SortDirection direction = request.direction();

        String nameCursor = null;
        Long subscriberCountCursor = null;

        switch (orderBy) {
            case NAME -> nameCursor = request.nameCursor();
            case SUBSCRIBER_COUNT -> subscriberCountCursor = request.subscriberCountCursor();
        }
        return new InterestCursorQuery(
                orderBy,
                direction,
                nameCursor,
                subscriberCountCursor,
                request.after(),
                request.limit(),
                request.keywordValue()
        );
    }
}
