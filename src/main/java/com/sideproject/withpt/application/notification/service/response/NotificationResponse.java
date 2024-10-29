package com.sideproject.withpt.application.notification.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.user.response.UserResponse;
import com.sideproject.withpt.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
public class NotificationResponse {

    private UserResponse notificationReceiver;
    private Slice<NotificationInfoResponse<?>> notifications;

    @Builder
    private NotificationResponse(UserResponse receiver, Slice<NotificationInfoResponse<?>> notifications) {
        this.notificationReceiver = receiver;
        this.notifications = notifications;
    }

    public static NotificationResponse of(User receiver, Slice<NotificationInfoResponse<?>> notifications) {
        return NotificationResponse.builder()
            .receiver(UserResponse.of(receiver))
            .notifications(notifications)
            .build();
    }
}
