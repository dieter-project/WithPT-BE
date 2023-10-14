package com.sideproject.withpt.application.body.dto.response;

import com.sideproject.withpt.domain.record.Body;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightInfoResponse {

    private double weight;
    private double skeletalMuscle;
    private double bodyFatPercentage;
    private double bmi;

    private LocalDateTime weightRecordDate;

    public static WeightInfoResponse from(Body body) {
        return WeightInfoResponse.builder()
                .weight(body.getWeight())
                .skeletalMuscle(body.getSkeletalMuscle())
                .bodyFatPercentage(body.getBodyFatPercentage())
                .bmi(body.getBmi())
                .weightRecordDate(body.getBodyRecordDate())
                .build();
    }

}
