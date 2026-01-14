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

        //쿼리 펙토리 형님
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

        //처음에 +1 값 으로 다음꺼 판단하고
        boolean hasNext = content.size() > request.limit();

        //용무끝났으니 마지막 1개 제거
        if (hasNext) {
            content.remove(request.limit());
        }
        return new SliceImpl<>(content, Pageable.unpaged(), hasNext);
    }

    private BooleanExpression Cursor(NotificationPageRequest request) {

        if (request.cursor() == null || request.after() == null) {
            return null;
        }
        //같은값 인데
        //타입이 달라서 바꿔줘야한다
        LocalDateTime cursorTime =
                LocalDateTime.parse(request.cursor());

        return notification.createdAt.gt(cursorTime);
    }

}
