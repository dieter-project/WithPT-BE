package com.sideproject.withpt.application.pt.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReRegistrationHistoryResponse {

    private Long ptId;
    private int ptCount;
    private LocalDateTime registrationDate;
    private PtRegistrationStatus registrationStatus;

    @QueryProjection
    public ReRegistrationHistoryResponse(Long ptId, int ptCount, LocalDateTime registrationDate,
        PtRegistrationStatus registrationStatus) {
        this.ptId = ptId;
        this.ptCount = ptCount;
        this.registrationDate = registrationDate;
        this.registrationStatus = registrationStatus;
    }
}
