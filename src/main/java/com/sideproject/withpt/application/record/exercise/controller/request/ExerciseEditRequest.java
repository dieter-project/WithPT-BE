package com.sideproject.withpt.application.record.exercise.controller.request;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseEditRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    private String bodyPart;

    private List<String> specificBodyParts = new ArrayList<>();

    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set

    private int exerciseTime; // 유산소, 스트레칭

    public ExerciseInfo toExerciseInfo() {
        return ExerciseInfo.builder()
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(toParentBodyCategory())
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
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
