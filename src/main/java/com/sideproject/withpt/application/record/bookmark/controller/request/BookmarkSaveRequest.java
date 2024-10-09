package com.sideproject.withpt.application.record.bookmark.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.record.bookmark.service.request.BookmarkSaveDto;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.ExerciseType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@ValidExerciseType
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkSaveRequest {

    @NotNull(message = "운동 일자를 입력해주세요.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate uploadDate;

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

    public BookmarkSaveDto toServiceDto() {
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
