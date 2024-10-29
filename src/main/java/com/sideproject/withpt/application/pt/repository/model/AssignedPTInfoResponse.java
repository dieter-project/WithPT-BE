package com.sideproject.withpt.application.pt.repository.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import com.sideproject.withpt.application.pt.service.response.PersonalTrainingResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
public class AssignedPTInfoResponse {

    private TrainerInfo trainer;
    private GymInfo gym;
    private PersonalTrainingResponse pt;

    @QueryProjection
    public AssignedPTInfoResponse(TrainerInfo trainer, GymInfo gym, PersonalTrainingResponse pt) {
        this.trainer = trainer;
        this.gym = gym;
        this.pt = pt;
    }

    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TrainerInfo {

        private Long id;
        private String name;
        private String imageUrl;

        @QueryProjection
        public TrainerInfo(Long id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
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
