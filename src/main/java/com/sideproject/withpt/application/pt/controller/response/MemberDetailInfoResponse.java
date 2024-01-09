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
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailInfoResponse {

    private MemberInfo member;
    private GymInfo gym;
    private PtInfo pt;

    @QueryProjection
    public MemberDetailInfoResponse(MemberInfo member, GymInfo gym, PtInfo pt) {
        this.member = member;
        this.gym = gym;
        this.pt = pt;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfo {
        private Long id;
        private String name;
        private LocalDate birth;
        private Sex sex;
        private Double height;
        private Double weight;
        private DietType dietType;

        @QueryProjection
        public MemberInfo(Long id, String name, LocalDate birth, Sex sex, Double height, Double weight,
            DietType dietType) {
            this.id = id;
            this.name = name;
            this.birth = birth;
            this.sex = sex;
            this.height = height;
            this.weight = weight;
            this.dietType = dietType;
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GymInfo {
        private Long id;
        private String name;

        @QueryProjection
        public GymInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PtInfo {
        private Long id;
        private PtRegistrationStatus registrationStatus;
        private int totalPtCount;
        private int remainingPtCount;
        private String note;
        private LocalDateTime firstRegistrationDate;
        private LocalDateTime lastRegistrationDate;

        @QueryProjection
        public PtInfo(Long id, PtRegistrationStatus registrationStatus, int totalPtCount, int remainingPtCount,
            String note,
            LocalDateTime firstRegistrationDate, LocalDateTime lastRegistrationDate) {
            this.id = id;
            this.registrationStatus = registrationStatus;
            this.totalPtCount = totalPtCount;
            this.remainingPtCount = remainingPtCount;
            this.note = note;
            this.firstRegistrationDate = firstRegistrationDate;
            this.lastRegistrationDate = lastRegistrationDate;
        }
    }

}
