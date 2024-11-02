package com.sideproject.withpt.domain.chat;

import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.user.User;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User user; // 참가자 (User)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private String roomName;

    private int unreadMessageCount; // 읽지 않은 채팅 수

    private Long lastReadMessageId; // 마지막으로 읽은 채팅 ID

    @Builder
    private Participant(User user, Room room, String roomName, int unreadMessageCount, Long lastReadMessageId) {
        this.user = user;
        this.room = room;
        this.roomName = roomName;
        this.unreadMessageCount = unreadMessageCount;
        this.lastReadMessageId = lastReadMessageId;
    }

    // 읽지 않은 메시지 수 증가
    public void incrementUnreadMessages() {
        this.unreadMessageCount += 1;
    }

    // 마지막 읽은 메시지 및 읽지 않은 메시지 수 업데이트
    public void updateLastChatAndNotReadChat(Long savedMessageId) {
        this.unreadMessageCount = 0;
        this.lastReadMessageId = savedMessageId;
    }

    public static Participant create(User user, Room room, String roomName) {
        return Participant.builder()
            .user(user)
            .room(room)
            .roomName(roomName)
            .unreadMessageCount(0)
            .lastReadMessageId(null)
            .build();
    }
}
