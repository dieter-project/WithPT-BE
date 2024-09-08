package com.sideproject.withpt.application.record.body.controller.response;

import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightInfoResponse {

    // 목표 체중
    private double currentTargetWeight;
    private List<WeightResponse> weights;
    private BodyInfoResponse bodyInfo;

    public static WeightInfoResponse from(List<Body> weightList, Optional<Body> info) {
        return WeightInfoResponse.builder()
            .currentTargetWeight(weightList.size() == 0 ? 0 : weightList.get(0).getTargetWeight())
            .weights(weightList.stream().map(body ->
                new WeightResponse(body.getUploadDate(), body.getWeight())
            ).collect(Collectors.toList()))
            .bodyInfo(
                info
                    .map(BodyInfoResponse::of)
                    .orElse(new BodyInfoResponse())
            )
            .build();
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    public static class WeightResponse {

        private LocalDate recentUploadDate;
        private double weight;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BodyInfoResponse {

        private LocalDate recentUploadDate;
        private double skeletalMuscle;
        private double bodyFatPercentage;
        private double bmi;

        public static BodyInfoResponse of(Body body) {
            return BodyInfoResponse.builder()
                .recentUploadDate(body.getUploadDate())
                .skeletalMuscle(body.getSkeletalMuscle())
                .bodyFatPercentage(body.getBodyFatPercentage())
                .bmi(body.getBmi())
                .build();
        }
    }
}
