package com.sideproject.withpt.application.record.exercise.controller.request;

import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseEditRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    private String bodyPart;

    private List<String> specificBodyParts;

    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set

    private int exerciseTime; // 유산소, 스트레칭

    @Builder
    private ExerciseEditRequest(String title, ExerciseType exerciseType, String bodyPart, List<String> specificBodyParts, int weight, int times, int exerciseSet, int exerciseTime) {
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyPart = bodyPart;
        this.specificBodyParts = specificBodyParts;
        this.weight = weight;
        this.times = times;
        this.exerciseSet = exerciseSet;
        this.exerciseTime = exerciseTime;
    }

    public BodyCategory toParentBodyCategory() {
        return Optional.ofNullable(bodyPart)
            .map(part -> BodyCategory.builder()
                .name(BodyPart.valueOf(part))
                .children(toChildBodyCategory()) // 자식 카테고리가 있으면 설정
                .build())
            .orElse(null);
    }

    private List<BodyCategory> toChildBodyCategory() {

        return Optional.ofNullable(specificBodyParts)
            .map(parts -> parts.stream()
                .map(part -> BodyCategory.builder()
                    .name(BodyPart.valueOf(part))
                    .build())
                .collect(Collectors.toList())
            ).orElse(null);
    }
}
