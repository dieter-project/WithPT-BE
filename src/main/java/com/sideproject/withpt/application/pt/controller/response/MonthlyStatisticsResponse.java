package com.sideproject.withpt.application.pt.controller.response;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthlyStatisticsResponse {

    private LocalDate currentDate;
    private Long existingMemberCount;
    private Long reEnrolledMemberCount;
    private Long newMemberCount;
}
