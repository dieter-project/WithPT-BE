package com.sideproject.withpt.application.body.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightInfoRequest {

    @NotNull(message = "측정일을 입력해 주세요")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate bodyRecordDate;

    @NotNull(message = "체중을 입력해 주세요")
    private double weight;

    public Body toEntity(Member member) {
        return Body.builder()
                .member(member)
                .weight(weight)
                .bodyRecordDate(bodyRecordDate)
                .build();
    }

    public Body toBodyEntity(Member member, Body body) {
        return Body.builder()
                .member(member)
                .weight(weight)
                .bodyRecordDate(bodyRecordDate)
                .bmi(body.getBmi())
                .skeletalMuscle(body.getSkeletalMuscle())
                .bodyFatPercentage(body.getBodyFatPercentage())
                .build();
    }

}
