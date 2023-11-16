package com.sideproject.withpt.application.body.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Body;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BodyInfoRequest {

    @NotNull(message = "골격근량을 입력해주세요.")
    private double skeletalMuscle;

    @NotNull(message = "체지방률을 입력해주세요.")
    private double bodyFatPercentage;

    @NotNull(message = "BMI 지수를 입력해주세요.")
    private double bmi;

    @NotNull(message = "측정일을 입력해 주세요")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate bodyRecordDate;

    public Body toEntity(Member member) {
        return Body.builder()
                .member(member)
                .weight(member.getWeight())
                .skeletalMuscle(skeletalMuscle)
                .bodyFatPercentage(bodyFatPercentage)
                .bmi(bmi)
                .bodyRecordDate(bodyRecordDate)
                .build();
    }

    public Body toBodyEntity(Member member, Body body) {
        return Body.builder()
                .member(member)
                .weight(body.getWeight())
                .bodyRecordDate(bodyRecordDate)
                .bmi(bmi)
                .skeletalMuscle(skeletalMuscle)
                .bodyFatPercentage(bodyFatPercentage)
                .build();
    }

}
