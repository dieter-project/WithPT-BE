package com.sideproject.withpt.application.notification.controller;

import com.sideproject.withpt.application.notification.controller.request.NotificationReadRequest;
import com.sideproject.withpt.application.notification.service.NotificationService;
import com.sideproject.withpt.application.notification.service.response.NotificationResponse;
import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiSuccessResponse<NotificationResponse> getNotificationList(
        @Parameter(hidden = true) @AuthenticationPrincipal Long receiverId, Pageable pageable) {
        return ApiSuccessResponse.from(
            notificationService.getNotificationList(receiverId, pageable)
        );
    }

    @PatchMapping("/notifications/read")
    public ResponseEntity<Void> readNotifications(
        @Parameter(hidden = true) @AuthenticationPrincipal Long receiverId,
        @RequestBody NotificationReadRequest request) {
        notificationService.readNotifications(receiverId, request);
        return ResponseEntity.noContent().build();
    }
}
