package com.sideproject.withpt.application.chat.contoller.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadMessageRequest {

    private Long userId;
    private Long roomId;
    private Long startLastReadMessageId;
    private Long endLastReadMessageId;

    @Builder
    public ReadMessageRequest(Long userId, Long roomId, Long startLastReadMessageId, Long endLastReadMessageId) {
        this.userId = userId;
        this.roomId = roomId;
        this.startLastReadMessageId = startLastReadMessageId;
        this.endLastReadMessageId = endLastReadMessageId;
    }
}
