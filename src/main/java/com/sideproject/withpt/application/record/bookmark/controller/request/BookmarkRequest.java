package com.sideproject.withpt.application.record.bookmark.controller.request;

import com.sideproject.withpt.application.record.bookmark.exception.validator.ValidBookmarkType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

//    public Bookmark toEntity(Member member) {
//        return Bookmark.builder()
//                .member(member)
//                .title(title)
//                .weight(weight)
//                .exerciseSet(exerciseSet)
//                .times(times)
//                .exerciseTime(hour)
////                .bodyPart(bodyPart)
//                .exerciseType(exerciseType)
//                .build();
//    }

}
