package com.sideproject.withpt.application.record.bookmark.service.request;

import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.bookmark.BookmarkBodyCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkSaveDto {

    private LocalDate uploadDate;
    private String title;
    private ExerciseType exerciseType;
    private String bodyPart;
    private List<String> specificBodyParts;
    private int weight; // 무게(kg)
    private int times; // 횟수
    private int exerciseSet; // 운동 set
    private int exerciseTime; // 유산소, 스트레칭

    @Builder
    private BookmarkSaveDto(LocalDate uploadDate, String title, ExerciseType exerciseType, String bodyPart, List<String> specificBodyParts, int weight, int times, int exerciseSet, int exerciseTime) {
        this.uploadDate = uploadDate;
        this.title = title;
        this.exerciseType = exerciseType;
        this.bodyPart = bodyPart;
        this.specificBodyParts = specificBodyParts;
        this.weight = weight;
        this.times = times;
        this.exerciseSet = exerciseSet;
        this.exerciseTime = exerciseTime;
    }

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
