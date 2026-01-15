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
        //유저아이디 , 확인안된 알림 ,커서
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
       //일단 널 주면 전체조회
        if (request.cursor() == null || request.after() == null) {
            return null;
        }
        //같은값 인데
        //타입이 달라서 바꿔줘야한다
        LocalDateTime cursorTime =
                LocalDateTime.parse(request.cursor());

        return notification.createdAt.gt(cursorTime);
    }

    //컨텐츠받고 넥스트확인후 11개받았으니 배열상 11번째 삭제 하고 슬라이스객체로
    private <T> Slice<T> Slice(List<T> content, int limit) {

        boolean hasNext = content.size() > limit;

        if (hasNext) {
            content.remove(limit);
        }

        return new SliceImpl<>(content, Pageable.unpaged(), hasNext);
    }
}
