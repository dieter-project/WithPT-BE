package com.sideproject.withpt.application.pt.repository.model;

import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingResponse;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.Sex;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailInfoResponse {

    private MemberInfo member;
    private GymInfo gym;
    private PersonalTrainingResponse pt;

    @QueryProjection
    public MemberDetailInfoResponse(MemberInfo member, GymInfo gym, PersonalTrainingResponse pt) {
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
        private String imageUrl;
        private LocalDate birth;
        private Sex sex;
        private Double height;
        private Double weight;
        private DietType dietType;

        @QueryProjection
        public MemberInfo(Long id, String name, String imageUrl, LocalDate birth, Sex sex, Double height, Double weight,
            DietType dietType) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
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
}
