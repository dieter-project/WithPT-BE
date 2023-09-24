package com.sideproject.withpt.application.weight.dto.response;

import com.sideproject.withpt.domain.record.Weight;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BodyInfoResponse {

    private double weight;
    private double skeletalMuscle;
    private double bodyFatPercentage;
    private double bmi;

    private LocalDateTime weightRecordDate;

    public static BodyInfoResponse from(Weight weight) {
        return BodyInfoResponse.builder()
                .weight(weight.getWeight())
                .skeletalMuscle(weight.getSkeletalMuscle())
                .bodyFatPercentage(weight.getBodyFatPercentage())
                .bmi(weight.getBmi())
                .weightRecordDate(weight.getWeightRecordDate())
                .build();
    }

}
