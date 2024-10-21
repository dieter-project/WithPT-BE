package com.sideproject.withpt.application.record.diet.controller.request;

import com.sideproject.withpt.common.type.DietCategory;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.user.member.Member;
import com.sideproject.withpt.domain.record.diet.Diets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveDietRequest {

    private LocalDate uploadDate;

    @ValidEnum(enumClass = DietCategory.class)
    private DietCategory dietCategory;

    @NotNull(message = "식사 시간을 입력해주세요.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime dietTime;

    private List<DietFoodRequest> dietFoods;

    public Diets toEntity(Member member) {
        return Diets.builder()
            .member(member)
            .uploadDate(uploadDate)
            .targetDietType(member.getDietType())
            .build();
    }
}
