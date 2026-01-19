package com.codeit.monew.domain.notification.repository;

import com.codeit.monew.domain.notification.dto.request.NotificationPageRequest;
import com.codeit.monew.domain.notification.entity.Notification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.codeit.monew.domain.notification.entity.QNotification.notification;


public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NotificationRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<Notification> search(NotificationPageRequest request) {

        List<Notification> content = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(request.userId()),
                        notification.confirmed.isFalse(),
                        Cursor(request)
                )
                .orderBy(notification.createdAt.asc(),notification.id.asc())
                .limit(request.limit() + 1)
                .fetch();

        return  Slice(content, request.limit());
    }

    private BooleanExpression Cursor(NotificationPageRequest request) {

        // 첫 페이지: 커서가 없으면 조건 없음
        if (request.cursor() == null) {
            return null;
        }

        int idx = request.cursor().lastIndexOf('_');

        LocalDateTime cursorTime = LocalDateTime.parse(request.cursor().substring(0, idx));
        UUID cursorId = UUID.fromString(request.cursor().substring(idx + 1));

        return notification.createdAt.gt(cursorTime).or(
                notification.createdAt.eq(cursorTime)
                        .and(notification.id.gt(cursorId))
        );
    }


    private <T> Slice<T> Slice(List<T> content, int limit) {

        boolean hasNext = content.size() > limit;

        if (hasNext) {
            content.remove(limit);
        }

        return new SliceImpl<>(content, Pageable.unpaged(), hasNext);
    }
}
