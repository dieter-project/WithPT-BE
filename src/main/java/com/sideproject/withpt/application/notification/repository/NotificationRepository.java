package com.sideproject.withpt.application.notification.repository;

import com.sideproject.withpt.domain.notification.Notification;
import com.sideproject.withpt.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findAllByReceiverOrderByCreatedAtDescIdDesc(User receiver, Pageable pageable);
}
