package com.sideproject.withpt.application.record.bookmark.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sideproject.withpt.application.record.exercise.exception.validator.ValidExerciseType;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.bookmark.BookmarkBodyCategory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Bookmark toEntity(Member member) {
        return Bookmark.builder()
            .member(member)
            .title(title)
            .exerciseType(exerciseType)
            .bodyCategory(toParentBodyCategory())
            .weight(weight)
            .exerciseSet(exerciseSet)
            .times(times)
            .exerciseTime(exerciseTime)
            .uploadDate(uploadDate)
            .build();
    }

    private BookmarkBodyCategory toParentBodyCategory() {
        return Optional.ofNullable(bodyPart)
            .map(part -> BookmarkBodyCategory.builder()
                .name(BodyPart.valueOf(part))
                .children(toChildBodyCategory()) // 자식 카테고리가 있으면 설정
                .build())
            .orElse(null);
    }

    private List<BookmarkBodyCategory> toChildBodyCategory() {
        return Optional.ofNullable(specificBodyParts)
            .map(parts -> parts.stream()
                .map(part -> BookmarkBodyCategory.builder()
                    .name(BodyPart.valueOf(part))
                    .build())
                .collect(Collectors.toList())
            ).orElse(null);
    }

}
