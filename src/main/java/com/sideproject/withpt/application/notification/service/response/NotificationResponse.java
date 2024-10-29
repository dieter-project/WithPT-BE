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

    private UserResponse sender;
    private UserResponse receiver;
    private Slice<NotificationInfoResponse<?>> notifications;

    @Builder
    private NotificationResponse(UserResponse sender, UserResponse receiver, Slice<NotificationInfoResponse<?>> notifications) {
        this.sender = sender;
        this.receiver = receiver;
        this.notifications = notifications;
    }

    public static NotificationResponse of(User sender, User receiver, Slice<NotificationInfoResponse<?>> notifications) {
        return NotificationResponse.builder()
            .sender(sender != null ? UserResponse.of(sender) : null)
            .receiver(UserResponse.of(receiver))
            .notifications(notifications)
            .build();
    }
}
