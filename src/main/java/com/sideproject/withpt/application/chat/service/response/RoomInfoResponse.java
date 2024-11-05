package com.sideproject.withpt.application.chat.service.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.user.response.UserResponse;
import com.sideproject.withpt.common.type.RoomType;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import com.sideproject.withpt.domain.user.User;
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
    private UserResponse partner;
    private int unreadMessageCount;
    private Long lastReadMessageId;
    private String lastChat;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder
    @QueryProjection
    public RoomInfoResponse(Long roomId, String identifier, RoomType roomType, String roomName, UserResponse partner, int unreadMessageCount, Long lastReadMessageId, String lastChat, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.roomId = roomId;
        this.identifier = identifier;
        this.roomType = roomType;
        this.roomName = roomName;
        this.partner = partner;
        this.unreadMessageCount = unreadMessageCount;
        this.lastReadMessageId = lastReadMessageId;
        this.lastChat = lastChat;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static RoomInfoResponse from(Room room, Participant participant, User partnerUser) {
        return RoomInfoResponse.builder()
            .roomId(room.getId())
            .identifier(room.getIdentifier())
            .roomType(room.getType())
            .roomName(participant.getRoomName())
            .partner(UserResponse.of(partnerUser))
            .unreadMessageCount(participant.getUnreadMessageCount())
            .lastReadMessageId(participant.getLastReadMessageId())
            .lastChat(room.getLastChat())
            .createdDate(room.getCreatedDate())
            .lastModifiedDate(room.getLastModifiedDate())
            .build();
    }
}
