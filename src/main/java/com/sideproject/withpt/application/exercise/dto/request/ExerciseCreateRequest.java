package com.sideproject.withpt.application.exercise.dto.request;

import com.sideproject.withpt.application.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Exercise;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidExerciseType
public class ExerciseCreateRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    private int weight;
    private int set;
    private int times;
    private int hour;

    @NotBlank(message = "북마크 여부를 입력해주세요.")
    private String bookmarkYn;

    @ValidEnum(enumClass = BodyPart.class)
    private BodyPart bodyPart;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    public Exercise toEntity(Member member) {
        return Exercise.builder()
                .member(member)
                .title(title)
                .weight(weight)
                .set(set)
                .times(times)
                .hour(hour)
                .bookmarkYn(bookmarkYn)
                .bodyPart(bodyPart)
                .exerciseType(exerciseType)
                .build();
    }

}
