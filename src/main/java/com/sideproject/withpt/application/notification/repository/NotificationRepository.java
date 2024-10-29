package com.sideproject.withpt.application.notification.repository;

import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findAllByReceiverOrderByCreatedAtDescIdDesc(User receiver, Pageable pageable);

    List<Notification> findAllByReceiver(User receiver);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver = :receiver AND n.id IN :notificationIds")
    int markNotificationsAsRead(@Param("receiver") User receiver, @Param("notificationIds") List<Long> notificationIds);
}
