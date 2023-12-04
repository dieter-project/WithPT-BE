package com.sideproject.withpt.application.pt.controller.response;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TotalPtsCountResponse {

    private Long totalMemberCount;
    private LocalDate day;

    public static TotalPtsCountResponse from(Long totalMemberCount) {
        return TotalPtsCountResponse.builder()
            .totalMemberCount(totalMemberCount)
            .day(LocalDate.now())
            .build();
    }
}
