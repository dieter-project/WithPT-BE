package com.sideproject.withpt.application.notification.service.response;

import com.sideproject.withpt.application.user.response.UserResponse;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.Notification;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationInfoResponse<T> {

    private Long id;
    private NotificationType type;
    private T relatedData;
    private UserResponse notificationSender;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;


    @Builder
    private NotificationInfoResponse(Long id, NotificationType type, T relatedData, UserResponse sender, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.relatedData = relatedData;
        this.notificationSender = sender;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static <T> NotificationInfoResponse<T> from(Notification notification, T relatedData) {
        return NotificationInfoResponse.<T>builder()
            .id(notification.getId())
            .type(notification.getType())
            .relatedData(relatedData)
            .sender(UserResponse.of(notification.getSender()))
            .message(notification.getText())
            .isRead(notification.isRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
