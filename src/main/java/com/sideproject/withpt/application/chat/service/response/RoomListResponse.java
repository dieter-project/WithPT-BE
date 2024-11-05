package com.sideproject.withpt.application.chat.service.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomListResponse {

    private List<RoomInfoResponse> roomList;
    private String message;

}
