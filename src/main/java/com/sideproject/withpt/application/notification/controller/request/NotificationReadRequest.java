package com.sideproject.withpt.application.notification.controller.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationReadRequest {

    private List<Long> notificationIds;

    public NotificationReadRequest(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
