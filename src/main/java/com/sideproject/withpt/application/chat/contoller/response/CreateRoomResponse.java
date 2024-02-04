package com.sideproject.withpt.application.chat.contoller.response;

import com.sideproject.withpt.application.type.RoomType;
import com.sideproject.withpt.domain.chat.Participant;
import com.sideproject.withpt.domain.chat.Room;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CreateRoomResponse {

    private RoomInfo room;
    private String message;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomInfo {
        private Long roomId;
        private String identifier;
        private RoomType roomType;
        private String roomName;
        private int notReadChat;
        private Long lastReadChatId;
        private String lastChat;
        private LocalDateTime lastModifiedDate;

        public static RoomInfo createRoomInfo(Room room, Participant participant) {
            return RoomInfo.builder()
                .roomId(room.getId())
                .identifier(room.getIdentifier())
                .roomType(room.getType())
                .roomName(participant.getRoomName())
                .notReadChat(participant.getNotReadChat())
                .lastReadChatId(participant.getLastReadChatId())
                .lastChat(room.getLastChat())
                .lastModifiedDate(room.getLastModifiedDate())
                .build();
        }
    }
}
