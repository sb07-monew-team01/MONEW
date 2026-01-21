package com.codeit.monew.domain.interest.mapper;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.springframework.stereotype.Component;

@Component
public class InterestQueryMapper {
    public InterestCursorQuery toQuery(InterestCursorPageRequest request) {
        InterestOrderBy orderBy = InterestOrderBy.fromString(request.orderBy()).orElse(InterestOrderBy.NAME);
        SortDirection direction = SortDirection.fromString(request.direction()).orElse(SortDirection.ASC);

        String nameCursor = null;
        Long subscriberCountCursor = null;

        switch (orderBy) {
            case NAME -> nameCursor = request.cursor();
            case SUBSCRIBERCOUNT -> subscriberCountCursor = Long.parseLong(request.cursor());
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
