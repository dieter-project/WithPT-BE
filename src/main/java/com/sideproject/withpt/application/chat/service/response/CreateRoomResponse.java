package com.sideproject.withpt.application.chat.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomResponse {

    private RoomInfoResponse room;
    private String message;

    @Builder
    public CreateRoomResponse(RoomInfoResponse room, String message) {
        this.room = room;
        this.message = message;
    }
}
