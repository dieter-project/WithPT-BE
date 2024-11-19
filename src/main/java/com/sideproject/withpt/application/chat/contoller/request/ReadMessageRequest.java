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
    private List<Long> lastReadMessageIdRange;

    @Builder
    public ReadMessageRequest(Long userId, Long roomId, List<Long> lastReadMessageIdRange) {
        this.userId = userId;
        this.roomId = roomId;
        this.lastReadMessageIdRange = lastReadMessageIdRange;
    }

    public Long getStartLastReadMessageId() {
        return lastReadMessageIdRange.get(0);
    }

    public Long getEndLastReadMessageId() {
        return lastReadMessageIdRange.get(1);
    }
}
