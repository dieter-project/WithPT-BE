package com.sideproject.withpt.application.pt.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.PtRegistrationStatus;
import com.sideproject.withpt.application.type.Sex;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailInfoResponse {

    private Long memberId;
    private Long gymId;

    private PtRegistrationStatus registrationStatus;
    private int totalPtCount;
    private int remainingPtCount;

    private LocalDateTime firstRegistrationDate;
    private LocalDateTime lastRegistrationDate;

    private String memberName;
    private String gymName;
    private LocalDate birthDate;
    private Sex sex;
    private Double height;
    private Double weight;
    private DietType dietType;
    private String note;

    @QueryProjection
    public MemberDetailInfoResponse(Long memberId, Long gymId, PtRegistrationStatus registrationStatus, int totalPtCount,
        int remainingPtCount, LocalDateTime firstRegistrationDate, LocalDateTime lastRegistrationDate, String memberName,
        String gymName, LocalDate birthDate, Sex sex, Double height, Double weight, DietType dietType, String note) {
        this.memberId = memberId;
        this.gymId = gymId;
        this.registrationStatus = registrationStatus;
        this.totalPtCount = totalPtCount;
        this.remainingPtCount = remainingPtCount;
        this.firstRegistrationDate = firstRegistrationDate;
        this.lastRegistrationDate = lastRegistrationDate;
        this.memberName = memberName;
        this.gymName = gymName;
        this.birthDate = birthDate;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.dietType = dietType;
        this.note = note;
    }
}
