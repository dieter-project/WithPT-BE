package com.sideproject.withpt.application.record.exercise.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveDto;
import com.sideproject.withpt.application.record.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.record.exercise.BodyCategory;
import com.sideproject.withpt.domain.record.exercise.Exercise;
import com.sideproject.withpt.domain.record.exercise.ExerciseInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
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

    private String bodyPart;

    private List<String> specificBodyParts;

    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set

    private int exerciseTime; // 유산소, 스트레칭

    @NotBlank(message = "북마크 여부를 입력해주세요.")
    private Boolean bookmarkYn;

    @Builder
    private ExerciseRequest(LocalDate uploadDate, String title, ExerciseType exerciseType, String bodyPart, List<String> specificBodyParts, int weight, int times, int exerciseSet, int exerciseTime, Boolean bookmarkYn) {
        this.uploadDate = uploadDate;
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyPart = bodyPart;
        this.specificBodyParts = specificBodyParts;
        this.weight = weight;
        this.times = times;
        this.exerciseSet = exerciseSet;
        this.exerciseTime = exerciseTime;
        this.bookmarkYn = bookmarkYn;
    }

    public ExerciseInfo toExerciseInfo(Exercise exercise) {
        return ExerciseInfo.builder()
            .exercise(exercise)
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(toParentBodyCategory())
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .build();
    }

    private BodyCategory toParentBodyCategory() {
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

    public BookmarkSaveDto toBookmarkSaveDto() {
        return BookmarkSaveDto.builder()
            .uploadDate(uploadDate)
            .title(title)
            .exerciseType(exerciseType)
            .bodyPart(bodyPart)
            .specificBodyParts(specificBodyParts)
            .weight(weight)
            .times(times)
            .exerciseSet(exerciseSet)
            .exerciseTime(exerciseTime)
            .build();
    }

}
