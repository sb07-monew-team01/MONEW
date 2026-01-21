package com.codeit.monew.domain.interest.vo;

import com.codeit.monew.domain.interest.entity.Interest;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

@Getter
public class NextCursor {
    private final String cursor;
    private final LocalDateTime after;

    private NextCursor(String cursor, LocalDateTime after) {
        this.cursor = cursor;
        this.after = after;
    }

    public static NextCursor from(Slice<Interest> slice, InterestOrderBy orderBy) {
        if (!slice.hasNext() || slice.getContent().isEmpty()) {
            return new NextCursor(null, null);
        }

        Interest last = slice.getContent().get(slice.getContent().size() - 1);

        return new NextCursor(
                resolveCursor(last, orderBy),
                last.getCreatedAt()
        );
    }

    private static String resolveCursor(Interest interest, InterestOrderBy orderBy) {
        return switch (orderBy) {
            case NAME -> interest.getName();
            case SUBSCRIBERCOUNT -> String.valueOf(interest.getSubscriberCount());
        };
    }
}
