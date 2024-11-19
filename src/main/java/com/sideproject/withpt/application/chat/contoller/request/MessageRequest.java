package com.sideproject.withpt.application.chat.contoller.request;

import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.lesson.Lesson;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDateTime;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MessageRequest {

    @NotNull(message = "채팅 방 ID는 필수 입니다.")
    private Long roomId; // 방 번호

    @ValidEnum(enumClass = MessageType.class)
    private MessageType messageType; // 메시지 타입

    @NotNull(message = "채팅을 보낸 사람의 ID는 필수입니다.")
    private Long sender; // 채팅을 보낸 사람

    @NotNull(message = "채팅하려는 상대방 ID는 필수입니다.")
    private Long receiver;

    private String message; // 메시지

    private Long relatedEntityId; // DIET, LESSON 등 관련 엔터티 ID

    private int notRead;

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    @Builder
    public MessageRequest(Long roomId, MessageType messageType, Long sender, Long receiver, String message, Long relatedEntityId, int notRead, String s3DataUrl, String fileName, String fileDir) {
        this.roomId = roomId;
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.notRead = notRead;
        this.s3DataUrl = s3DataUrl;
        this.fileName = fileName;
        this.fileDir = fileDir;
    }

    @AssertTrue(message = "메시지 타입에 따라 적절한 데이터를 입력하세요.")
    public boolean isValidMessageRequest() {
        switch (messageType) {
            case TALK:
                return message != null && relatedEntityId == null;
            case DIET:
            case LESSON:
                return relatedEntityId != null && message == null;
            case IMAGE:
                return s3DataUrl != null && fileName != null && fileDir != null;
            default:
                return false;
        }
    }

    public Message toEntity(Room room, User sender, User receiver, LocalDateTime sentAt, Diets diets, Lesson lesson) {
        return Message.builder()
            .room(room)
            .sender(sender)
            .receiver(receiver)
            .type(messageType)
            .message(message)
            .notRead(notRead)
            .sentAt(sentAt)
            .lesson(lesson)
            .diets(diets)
            .s3DataUrl(s3DataUrl)
            .fileName(fileName)
            .fileDir(fileDir)
            .build();
    }
}
