package com.sideproject.withpt.application.pt.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PtStatisticResponse {

    private MonthStatistics monthStatistic;
    private List<MonthlyMemberCount> statistics;


    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MonthlyMemberCount {

        private String date;
        private Long count;

        @QueryProjection
        public MonthlyMemberCount(String date, Long count) {
            this.date = date;
            this.count = count;
        }
    }

    @Getter
    @ToString
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class MonthStatistics {

        private LocalDate currentDate;
        private int existingMemberCount;
        private int reEnrolledMemberCount;
        private int newMemberCount;
    }
}
