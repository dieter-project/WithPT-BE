package com.sideproject.withpt.application.lesson.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.Sex;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMemberResponse {

    private Long memberId;
    private String memberName;
    private PTInfoInputStatus infoInputStatus;
    private LocalDate birth;
    private Sex sex;
    private int remainingPtCount;

    @QueryProjection
    public SearchMemberResponse(Long memberId, String memberName, PTInfoInputStatus infoInputStatus, LocalDate birth, Sex sex, int remainingPtCount) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.infoInputStatus = infoInputStatus;
        this.birth = birth;
        this.sex = sex;
        this.remainingPtCount = remainingPtCount;
    }
}
