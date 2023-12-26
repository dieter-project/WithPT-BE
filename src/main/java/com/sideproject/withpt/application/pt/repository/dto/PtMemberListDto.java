package com.sideproject.withpt.application.pt.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PtMemberListDto {

    private Long id;
    private String name;
    private Long ptId;
    private int totalPtCount;
    private int remainingPtCount;
    private PTInfoInputStatus infoInputStatus;
    private PtRegistrationAllowedStatus registrationAllowedStatus;
    private LocalDateTime registrationRequestDate;

    @QueryProjection
    public PtMemberListDto(Long id, String name, Long ptId, int totalPtCount, int remainingPtCount,
        PTInfoInputStatus infoInputStatus,
        PtRegistrationAllowedStatus registrationAllowedStatus, LocalDateTime registrationRequestDate) {
        this.id = id;
        this.name = name;
        this.ptId = ptId;
        this.totalPtCount = totalPtCount;
        this.remainingPtCount = remainingPtCount;
        this.infoInputStatus = infoInputStatus;
        this.registrationAllowedStatus = registrationAllowedStatus;
        this.registrationRequestDate = registrationRequestDate;
    }
}
