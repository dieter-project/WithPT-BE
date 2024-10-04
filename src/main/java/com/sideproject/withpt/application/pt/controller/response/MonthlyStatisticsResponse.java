package com.sideproject.withpt.application.pt.controller.response;

import java.time.YearMonth;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyStatisticsResponse {

    private List<MonthStatistic> statistics;

    private MonthlyStatisticsResponse(List<MonthStatistic> statistics) {
        this.statistics = statistics;
    }

    public static MonthlyStatisticsResponse of(List<MonthStatistic> statistics) {
        return new MonthlyStatisticsResponse(statistics);
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MonthStatistic {

        private YearMonth date;
        private Long total;
        private Long existingMemberCount;
        private Long reEnrolledMemberCount;
        private Long newMemberCount;

        @Builder
        private MonthStatistic(YearMonth date, Long existingMemberCount, Long reEnrolledMemberCount, Long newMemberCount) {
            this.date = date;
            this.total = existingMemberCount + reEnrolledMemberCount + newMemberCount;
            this.existingMemberCount = existingMemberCount;
            this.reEnrolledMemberCount = reEnrolledMemberCount;
            this.newMemberCount = newMemberCount;
        }
    }


}
