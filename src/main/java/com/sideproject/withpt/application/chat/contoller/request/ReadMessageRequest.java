package com.sideproject.withpt.application.chat.contoller.request;

import com.sideproject.withpt.application.type.Role;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageRequest {

    private Long loginUserId;
    private Role loginUserRole;
    private Long roomId;
    private String participant;
    private List<Long> lastReadMessageIdRange;

    public Long getStartLastReadMessageId() {
        return lastReadMessageIdRange.get(0);
    }

    public Long getEndLastReadMessageId() {
        return lastReadMessageIdRange.get(1);
    }
}
