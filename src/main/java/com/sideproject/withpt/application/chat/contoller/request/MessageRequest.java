package com.sideproject.withpt.application.chat.contoller.request;

import com.sideproject.withpt.application.type.MessageType;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.chat.Message;
import com.sideproject.withpt.domain.chat.Room;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotNull(message = "채팅 방 ID는 필수 입니다.")
    private Long roomId; // 방 번호

    @ValidEnum(enumClass = MessageType.class)
    private MessageType messageType; // 메시지 타입

    @Pattern(regexp = "TRAINER_\\d+|MEMBER_\\d+", message = "식별자가 옳바르지 않습니다.")
    private String sender; // 채팅을 보낸 사람

    @Pattern(regexp = "TRAINER_\\d+|MEMBER_\\d+", message = "식별자가 옳바르지 않습니다.")
    private String receiver;
    private String message; // 메시지

    private int notRead;

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

    public Role getSenderRole() {
        return Role.valueOf(sender.split("_")[0]);
    }

    public Role getReceiverRole() {
        return Role.valueOf(receiver.split("_")[0]);
    }

    public Message toEntity(Room room) {
        return Message.builder()
            .room(room)
            .sender(sender)
            .receiver(receiver)
            .message(message)
            .type(messageType)
            .notRead(notRead)
            .s3DataUrl(s3DataUrl)
            .fileName(fileName)
            .fileDir(fileDir)
            .build();
    }
}
