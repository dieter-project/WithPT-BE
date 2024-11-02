package com.sideproject.withpt.domain.chat;

import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 수신자

    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private int notRead;

    private LocalDateTime sentAt; // 전송 시간

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    @Builder
    private Message(Room room, User sender, User receiver, String content, MessageType type, int notRead, LocalDateTime sentAt, String s3DataUrl, String fileName, String fileDir) {
        this.room = room;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.notRead = notRead;
        this.sentAt = sentAt;
        this.s3DataUrl = s3DataUrl;
        this.fileName = fileName;
        this.fileDir = fileDir;
    }
}
