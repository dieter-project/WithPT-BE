package com.sideproject.withpt.domain.notification;

import com.sideproject.withpt.common.type.NotificationType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type")
public abstract class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;  // 알림 종류 (식단 피드백, 수업 등록 요청 등)

    @Column(nullable = false)
    private String text;  // 알림 텍스트 (알림 내용)

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // 알림 발신자 (User)

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;  // 알림 수신자 (User)

    @Column(nullable = false)
    private boolean isRead;  // 알림 읽음 여부

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 알림 생성 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;  // 알림 생성 시 기본적으로 읽지 않은 상태로 설정
    }

    protected Notification(NotificationType type, String text, User sender, User receiver) {
        this.type = type;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }
}
