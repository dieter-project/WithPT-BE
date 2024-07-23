package com.sideproject.withpt.application.record.exercise.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.record.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidExerciseType
public class ExerciseRequest {

    @NotNull(message = "운동 일자를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

    @NotBlank(message = "운동명을 입력해주세요.")
    private String title;

    @ValidEnum(enumClass = ExerciseType.class)
    private ExerciseType exerciseType;

    private List<String> bodyParts;

    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set

    private int exerciseTime; // 유산소, 스트레칭

    @NotBlank(message = "북마크 여부를 입력해주세요.")
    private Boolean bookmarkYn;

    public ExerciseInfo toExerciseInfo() {
        return ExerciseInfo.builder()
            .title(title)
            .exerciseType(exerciseType)
            .bodyParts(bodyParts.stream().map(BodyPart::valueOf).collect(Collectors.toList()))
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
    }

//    public Bookmark toBookmarkEntity(Member member) {
//        return Bookmark.builder()
//                .member(member)
//                .title(title)
//                .weight(weight)
//                .exerciseSet(exerciseSet)
//                .times(times)
////                .exerciseTime(exerciseTime)
//                .bodyPart(bodyPart)
//                .exerciseType(exerciseType)
//                .build();
//    }

}
