package com.sideproject.withpt.application.pt.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.common.type.PTInfoInputStatus;
import com.sideproject.withpt.common.type.PtRegistrationAllowedStatus;
import com.sideproject.withpt.common.type.PtRegistrationStatus;
import com.sideproject.withpt.domain.pt.PersonalTraining;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
public class PersonalTrainingResponse {

    private Long id;
    private int totalPtCount;
    private int remainingPtCount;
    private String note;
    private PtRegistrationStatus registrationStatus;
    private PtRegistrationAllowedStatus registrationAllowedStatus;
    private PTInfoInputStatus infoInputStatus;
    private LocalDateTime centerFirstRegistrationMonth;
    private LocalDateTime centerLastReRegistrationMonth;

    @Builder
    @QueryProjection
    public PersonalTrainingResponse(Long id, int totalPtCount, int remainingPtCount, String note, PtRegistrationStatus registrationStatus, PtRegistrationAllowedStatus registrationAllowedStatus, PTInfoInputStatus infoInputStatus, LocalDateTime centerFirstRegistrationMonth, LocalDateTime centerLastReRegistrationMonth) {
        this.id = id;
        this.totalPtCount = totalPtCount;
        this.remainingPtCount = remainingPtCount;
        this.note = note;
        this.registrationStatus = registrationStatus;
        this.registrationAllowedStatus = registrationAllowedStatus;
        this.infoInputStatus = infoInputStatus;
        this.centerFirstRegistrationMonth = centerFirstRegistrationMonth;
        this.centerLastReRegistrationMonth = centerLastReRegistrationMonth;
    }


    public static PersonalTrainingResponse of(PersonalTraining pt) {
        return PersonalTrainingResponse.builder()
            .id(pt.getId())
            .totalPtCount(pt.getTotalPtCount())
            .remainingPtCount(pt.getRemainingPtCount())
            .note(pt.getNote())
            .registrationStatus(pt.getRegistrationStatus())
            .registrationAllowedStatus(pt.getRegistrationAllowedStatus())
            .infoInputStatus(pt.getInfoInputStatus())
            .centerFirstRegistrationMonth(pt.getCenterFirstRegistrationMonth())
            .centerLastReRegistrationMonth(pt.getCenterLastReRegistrationMonth())
            .build();
    }
}
