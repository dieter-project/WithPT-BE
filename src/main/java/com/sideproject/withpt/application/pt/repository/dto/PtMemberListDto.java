package com.sideproject.withpt.application.pt.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.type.PTInfoInputStatus;
import com.sideproject.withpt.application.type.PtRegistrationAllowedStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PtMemberListDto {

    private MemberInfo member;

    @QueryProjection
    public PtMemberListDto(MemberInfo member) {
        this.member = member;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfo {
        private Long id;
        private String name;
        private PtInfo pt;

        @QueryProjection
        public MemberInfo(Long id, String name, PtInfo ptInfo) {
            this.id = id;
            this.name = name;
            this.pt = ptInfo;
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PtInfo {
        private Long id;
        private int totalPtCount;
        private int remainingPtCount;
        private PTInfoInputStatus infoInputStatus;
        private PtRegistrationAllowedStatus registrationAllowedStatus;
        private LocalDateTime registrationRequestDate;

        @QueryProjection
        public PtInfo(Long id, int totalPtCount, int remainingPtCount, PTInfoInputStatus infoInputStatus,
            PtRegistrationAllowedStatus registrationAllowedStatus, LocalDateTime registrationRequestDate) {
            this.id = id;
            this.totalPtCount = totalPtCount;
            this.remainingPtCount = remainingPtCount;
            this.infoInputStatus = infoInputStatus;
            this.registrationAllowedStatus = registrationAllowedStatus;
            this.registrationRequestDate = registrationRequestDate;
        }
    }

}
