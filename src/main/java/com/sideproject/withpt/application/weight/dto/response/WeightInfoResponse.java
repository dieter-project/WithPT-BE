package com.sideproject.withpt.application.weight.dto.response;

import com.sideproject.withpt.domain.record.Weight;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightInfoResponse {

    private double skeletalMuscle;
    private double bodyFatPercentage;
    private double bmi;

    private LocalDateTime weightRecordDate;

    public static WeightInfoResponse from(Weight weight) {
        return WeightInfoResponse.builder()
                .skeletalMuscle(weight.getSkeletalMuscle())
                .bodyFatPercentage(weight.getBodyFatPercentage())
                .bmi(weight.getBmi())
                .weightRecordDate(weight.getWeightRecordDate())
                .build();
    }

}
