package com.sideproject.withpt.application.notification.service;

import com.sideproject.withpt.application.notification.repository.NotificationRepository;
import com.sideproject.withpt.application.notification.service.response.NotificationResponse;
import com.sideproject.withpt.application.notification.strategy.NotificationStrategy;
import com.sideproject.withpt.application.notification.strategy.NotificationStrategyFactory;
import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationStrategyFactory strategyFactory;

    @Transactional
    public <T> Notification createNotification(User sender, User receiver, String text, NotificationType type, LocalDateTime createdAt, T relatedEntity) {
        NotificationStrategy<T> strategy = (NotificationStrategy<T>) strategyFactory.getStrategy(relatedEntity);
        Notification notification = strategy.createNotification(sender, receiver, text, type, createdAt, relatedEntity);
        return notificationRepository.save(notification);
    }

    public NotificationResponse getNotificationList(Trainer trainer, Pageable pageable) {
        throw new UnsupportedOperationException("Unsupported getNotificationList");
    }
}
