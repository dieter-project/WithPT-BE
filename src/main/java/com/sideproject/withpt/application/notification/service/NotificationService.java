package com.sideproject.withpt.application.notification.service;

import com.sideproject.withpt.application.notification.controller.request.NotificationReadRequest;
import com.sideproject.withpt.application.notification.repository.NotificationRepository;
import com.sideproject.withpt.application.notification.service.response.NotificationInfoResponse;
import com.sideproject.withpt.application.notification.service.response.NotificationResponse;
import com.sideproject.withpt.application.notification.service.response.mapper.NotificationMapper;
import com.sideproject.withpt.application.notification.service.response.mapper.NotificationMapperFactory;
import com.sideproject.withpt.application.notification.strategy.NotificationStrategy;
import com.sideproject.withpt.application.notification.strategy.NotificationStrategyFactory;
import com.sideproject.withpt.application.user.UserRepository;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private final NotificationStrategyFactory strategyFactory;
    private final NotificationMapperFactory notificationMapperFactory;

    @Transactional
    public <T> Notification createNotification(User sender, User receiver, String text, NotificationType type, LocalDateTime createdAt, T relatedEntity) {
        NotificationStrategy<T> strategy = (NotificationStrategy<T>) strategyFactory.getStrategy(relatedEntity);
        Notification notification = strategy.createNotification(sender, receiver, text, type, createdAt, relatedEntity);
        return notificationRepository.save(notification);
    }

    public NotificationResponse getNotificationList(Long receiverId, Pageable pageable) {
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);
        Slice<Notification> notifications = notificationRepository.findAllByReceiverOrderByCreatedAtDescIdDesc(receiver, pageable);

        List<NotificationInfoResponse<?>> notificationInfoResponse = mapNotificationsToResponses(notifications);

        SliceImpl<NotificationInfoResponse<?>> notificationInfoResponses = new SliceImpl<>(notificationInfoResponse, pageable, notifications.hasNext());
        return NotificationResponse.of(receiver, notificationInfoResponses);
    }

    @Transactional
    public void readNotifications(Long receiverId, NotificationReadRequest request) {
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> GlobalException.USER_NOT_FOUND);

        notificationRepository.markNotificationsAsRead(receiver, request.getNotificationIds());
    }

    private List<NotificationInfoResponse<?>> mapNotificationsToResponses(Slice<Notification> notifications) {
        return notifications.stream()
            .map(this::mapNotificationToResponse)
            .collect(Collectors.toList());
    }

    private NotificationInfoResponse<?> mapNotificationToResponse(Notification notification) {
        NotificationMapper<Notification> mapper = (NotificationMapper<Notification>) notificationMapperFactory.getMapper(notification.getClass());
        return mapper.toResponse(notification);
    }
}
