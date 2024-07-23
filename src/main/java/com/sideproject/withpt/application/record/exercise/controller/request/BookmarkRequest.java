package com.sideproject.withpt.application.record.exercise.controller.request;

import com.sideproject.withpt.application.record.exercise.exception.validator.ValidBookmarkType;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.exercise.Bookmark;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidBookmarkType
public class BookmarkRequest {

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    private int weight;
    private int exerciseSet;
    private int times;
    private int hour;

    @ValidEnum(enumClass = BodyPart.class)
    private BodyPart bodyPart;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    public Bookmark toEntity(Member member) {
        return Bookmark.builder()
                .member(member)
                .title(title)
                .weight(weight)
                .exerciseSet(exerciseSet)
                .times(times)
                .hour(hour)
                .bodyPart(bodyPart)
                .exerciseType(exerciseType)
                .build();
    }

}
