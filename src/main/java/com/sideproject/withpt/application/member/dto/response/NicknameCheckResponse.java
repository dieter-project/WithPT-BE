package com.sideproject.withpt.application.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NicknameCheckResponse {

    private boolean duplicateNickname;

    public static NicknameCheckResponse from(boolean isDuplicated){
        return NicknameCheckResponse.builder()
            .duplicateNickname(isDuplicated)
            .build();
    }
}
