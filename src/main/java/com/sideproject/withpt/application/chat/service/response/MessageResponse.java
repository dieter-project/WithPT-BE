package com.sideproject.withpt.application.chat.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sideproject.withpt.application.user.response.UserResponse;
import com.sideproject.withpt.common.type.MessageType;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MessageResponse {

    private Long roomId;
    private Long messageId;
    private UserResponse sender;
    private UserResponse receiver;
    private String message;
    private MessageType messageType; // 메시지 타입
    private int notRead;
    private Long lessonId;
    private Long dietId;
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로
    private LocalDateTime sentAt;

    public static MessageResponse from(Message message, Room room) {
        return MessageResponse.builder()
            .roomId(room.getId())
            .messageId(message.getId())
            .sender(UserResponse.of(message.getSender()))
            .receiver(UserResponse.of(message.getReceiver()))
            .message(message.getMessage())
            .messageType(message.getType())
            .notRead(message.getNotRead())
            .lessonId(message.getLesson().getId())
            .dietId(message.getDiets().getId())
            .s3DataUrl(message.getS3DataUrl())
            .fileName(message.getFileName())
            .fileDir(message.getFileDir())
            .sentAt(message.getSentAt())
            .build();
    }
}
