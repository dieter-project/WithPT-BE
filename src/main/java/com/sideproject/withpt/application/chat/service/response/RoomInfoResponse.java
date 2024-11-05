package com.sideproject.withpt.application.chat.service.response;

import com.sideproject.withpt.common.type.RoomType;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomInfoResponse {

    private Long roomId;
    private String identifier;
    private RoomType roomType;
    private String roomName;
    private int unreadMessageCount;
    private Long lastReadMessageId;
    private String lastChat;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder
    private RoomInfoResponse(Long roomId, String identifier, RoomType roomType, String roomName, int unreadMessageCount, Long lastReadMessageId, String lastChat, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.roomId = roomId;
        this.identifier = identifier;
        this.roomType = roomType;
        this.roomName = roomName;
        this.unreadMessageCount = unreadMessageCount;
        this.lastReadMessageId = lastReadMessageId;
        this.lastChat = lastChat;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static RoomInfoResponse from(Room room, Participant participant) {
        return RoomInfoResponse.builder()
            .roomId(room.getId())
            .identifier(room.getIdentifier())
            .roomType(room.getType())
            .roomName(participant.getRoomName())
            .unreadMessageCount(participant.getUnreadMessageCount())
            .lastReadMessageId(participant.getLastReadMessageId())
            .lastChat(room.getLastChat())
            .createdDate(room.getCreatedDate())
            .lastModifiedDate(room.getLastModifiedDate())
            .build();
    }
}
