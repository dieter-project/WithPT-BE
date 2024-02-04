package com.sideproject.withpt.application.chat.contoller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.RoomType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class RoomListResponse {

    private List<RoomInfo> roomList;
    private String message;

    @Getter
    @Builder
    @NoArgsConstructor
    public static class RoomInfo {

        private Long roomId;
        private String roomName;
        private RoomType type;
        private String identifier;
        private Long participant;
        private String lastChat;
        private int notReadChat;
        private Long lastReadChatId;
        private LocalDateTime lastModifiedDate;

        @QueryProjection
        public RoomInfo(Long roomId, String roomName, RoomType type, String identifier, Long participant,
            String lastChat,
            int notReadChat, Long lastReadChatId, LocalDateTime lastModifiedDate) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.type = type;
            this.identifier = identifier;
            this.participant = participant;
            this.lastChat = lastChat;
            this.notReadChat = notReadChat;
            this.lastReadChatId = lastReadChatId;
            this.lastModifiedDate = lastModifiedDate;
        }
    }


}
