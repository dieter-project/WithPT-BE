package com.sideproject.withpt.application.chat.contoller.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadMessageResponse {

    private Long roomId;
    private List<Long> lastReadMessageIdRange;
}
