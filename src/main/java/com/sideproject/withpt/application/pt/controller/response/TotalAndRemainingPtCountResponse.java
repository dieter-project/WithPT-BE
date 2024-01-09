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

    private Long ptId;
    private int totalPtCount;
    private int remainingPtCount;

    public static TotalAndRemainingPtCountResponse of(Long ptId, int totalPtCount, int remainingPtCount) {
        return TotalAndRemainingPtCountResponse.builder()
            .ptId(ptId)
            .totalPtCount(totalPtCount)
            .remainingPtCount(remainingPtCount)
            .build();
    }
}
