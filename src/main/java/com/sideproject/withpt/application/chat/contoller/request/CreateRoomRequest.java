package com.sideproject.withpt.application.chat.contoller.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CreateRoomRequest {

    @NotNull(message = "채팅하려는 상대방 ID는 필수입니다.")
    private Long id;

    @Builder
    private CreateRoomRequest(Long id) {
        this.id = id;
    }
}
