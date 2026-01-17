package com.codeit.monew.domain.notification;

import com.codeit.monew.domain.notification.entity.Notification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationDeleteTestFixture {

    public static List<Notification> commentNotificationList(
            UUID userId,
            UUID resourceId,
            int count
    ) {
        List<Notification> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(Notification.forCommentLike(userId, resourceId, "사람이름" + i));
        }
        return list;
    }

    public static List<Notification> interestNotificationList(
            UUID userId,
            UUID resourceId,
            int count
    ) {
        List<Notification> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(Notification.forInterest(userId, resourceId, "관심사이름" + i,i));
        }
        return list;
    }

    public static void confirmAll(List<Notification> notifications) {
        notifications.forEach(Notification::confirm);
    }


}
