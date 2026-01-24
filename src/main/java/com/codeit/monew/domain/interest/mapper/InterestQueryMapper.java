package com.codeit.monew.domain.interest.mapper;

import com.codeit.monew.domain.interest.dto.query.InterestCursorQuery;
import com.codeit.monew.domain.interest.dto.request.InterestCursorPageRequest;
import com.codeit.monew.domain.interest.vo.InterestOrderBy;
import com.codeit.monew.domain.interest.vo.SortDirection;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InterestQueryMapper {
    public InterestCursorQuery toQuery(InterestCursorPageRequest request) {
        InterestOrderBy orderBy = InterestOrderBy.fromString(request.orderBy()).orElse(InterestOrderBy.NAME);
        SortDirection direction = SortDirection.fromString(request.direction()).orElse(SortDirection.ASC);

        String nameCursor = null;
        Long subscriberCountCursor = null;
        String cursorParsing;
        LocalDateTime afterParsing = request.cursor() == null ?
                null : LocalDateTime.parse(request.cursor().split("_")[1]);

        if(request.cursor() != null){
            cursorParsing = request.cursor().split("_")[0];
            switch (orderBy) {
                case NAME -> nameCursor = cursorParsing;
                case SUBSCRIBERCOUNT -> subscriberCountCursor = Long.parseLong(cursorParsing);
            };
        }

        return new InterestCursorQuery(
                orderBy,
                direction,
                nameCursor,
                subscriberCountCursor,
                afterParsing,
                request.limit(),
                request.keywordValue()
        );
    }
}
