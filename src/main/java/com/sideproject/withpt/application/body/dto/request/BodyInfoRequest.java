package com.sideproject.withpt.application.body.dto.request;

import com.sideproject.withpt.application.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Body;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidExerciseType
public class BodyInfoRequest {

    @NotBlank(message = "골격근량을 입력해주세요.")
    private double skeletalMuscle;

    @NotBlank(message = "체지방률을 입력해주세요.")
    private double bodyFatPercentage;

    @NotBlank(message = "BMI 지수를 입력해주세요.")
    private double bmi;

    @NotBlank(message = "측정일을 입력해 주세요")
    private LocalDateTime weightRecordDate;

    public Body toEntity(Member member) {
        return Body.builder()
                .member(member)
                .weight(member.getWeight())
                .skeletalMuscle(skeletalMuscle)
                .bodyFatPercentage(bodyFatPercentage)
                .bmi(bmi)
                .weightRecordDate(weightRecordDate)
                .build();
    }

}
