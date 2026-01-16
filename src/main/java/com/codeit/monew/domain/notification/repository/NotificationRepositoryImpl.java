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
                        notification.userId.eq(request.userid()),
                        notification.confirmed.isFalse(),
                        Cursor(request)
                )
                .orderBy(notification.createdAt.asc())
                .limit(request.limit() + 1)
                .fetch();

        return  Slice(content, request.limit());
    }

    private BooleanExpression Cursor(NotificationPageRequest request) {

        if (request.cursor() == null || request.after() == null) {
            return null;
        }

        LocalDateTime cursorTime =
                LocalDateTime.parse(request.cursor());

        return notification.createdAt.gt(cursorTime);
    }


    private <T> Slice<T> Slice(List<T> content, int limit) {

        boolean hasNext = content.size() > limit;

        if (hasNext) {
            content.remove(limit);
        }

        return new SliceImpl<>(content, Pageable.unpaged(), hasNext);
    }
}
