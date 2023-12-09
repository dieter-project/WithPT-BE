package com.sideproject.withpt.application.pt.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalAndRemainingPtCountResponse {

    private Long memberId;
    private Long gymId;
    private int totalPtCount;
    private int remainingPtCount;

    public static TotalAndRemainingPtCountResponse of(Long memberId, Long gymId, int totalPtCount, int remainingPtCount) {
        return TotalAndRemainingPtCountResponse.builder()
            .memberId(memberId)
            .gymId(gymId)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .build();
    }
}
