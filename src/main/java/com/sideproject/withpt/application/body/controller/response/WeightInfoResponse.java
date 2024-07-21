package com.sideproject.withpt.application.body.controller.response;

import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private double currentTargetWeight = 0.0;
    private List<WeightResponse> weights = new ArrayList<>();
    private BodyInfoResponse bodyInfo;

    public static WeightInfoResponse from(List<Body> weightList, Optional<Body> info) {

        double targetWeight = weightList.size() == 0 ? 0 : weightList.get(0).getTargetWeight();
        return WeightInfoResponse.builder()
            .currentTargetWeight(targetWeight)
            .weights(weightList.stream().map(body ->
                new WeightResponse(body.getUploadDate(), body.getWeight())
            ).collect(Collectors.toList()))
            .bodyInfo(
                info.map(body ->
                    new BodyInfoResponse(
                        body.getUploadDate(),
                        body.getSkeletalMuscle(),
                        body.getBodyFatPercentage(),
                        body.getBmi()
                    )
                ).orElse(null)
            )
            .build();
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    private static class WeightResponse {

        private LocalDate recentUploadDate;
        private double weight;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    private static class BodyInfoResponse {

        private LocalDate recentUploadDate;
        private double skeletalMuscle;
        private double bodyFatPercentage;
        private double bmi;
    }
}
