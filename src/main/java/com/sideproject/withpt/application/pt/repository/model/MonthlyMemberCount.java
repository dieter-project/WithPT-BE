package com.sideproject.withpt.application.pt.repository.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyMemberCount {

    private String date;
    private Long count;

    @QueryProjection
    public MonthlyMemberCount(String date, Long count) {
        this.date = date;
        this.count = count;
    }
}
