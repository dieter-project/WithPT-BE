package com.sideproject.withpt.application.record.body.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.body.Body;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeightInfoRequest {

    @NotNull(message = "측정일을 입력해 주세요")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    @NotNull(message = "체중을 입력해 주세요")
    private double weight;

    public Body toEntity(Member member) {
        return Body.builder()
            .member(member)
            .targetWeight(member.getTargetWeight())
            .weight(weight)
            .uploadDate(uploadDate)
            .build();
    }

}
