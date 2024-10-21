package com.sideproject.withpt.domain.chat;

import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.user.trainer.Trainer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@ToString(exclude = {"trainer", "member", "room"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_role")
    private Role role;

    private String roomName;

    private int notReadChat; // 읽지 않은 채팅 수

    private Long lastReadChatId; // 마지막으로 읽은 채팅 ID

    public void incrementNotReadChat() {
        this.notReadChat += 1;
    }

    public void updateLastChatAndNotReadChat(Long savedMessageId) {
        this.notReadChat = 0;
        this.lastReadChatId = savedMessageId;
    }

    public static Participant createParticipant(Trainer trainer, Member member, Role role, Room room, String roomName) {
        return Participant.builder()
            .trainer(trainer)
            .member(member)
            .role(role)
            .room(room)
            .roomName(roomName)
            .notReadChat(-1)
            .lastReadChatId(-1L)
            .build();
    }
}
