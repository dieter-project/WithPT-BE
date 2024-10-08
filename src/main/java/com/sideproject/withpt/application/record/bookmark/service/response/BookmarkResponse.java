package com.sideproject.withpt.application.record.bookmark.service.response;

import com.sideproject.withpt.common.type.BodyPart;
import com.sideproject.withpt.common.type.ExerciseType;
import com.sideproject.withpt.domain.record.bookmark.Bookmark;
import com.sideproject.withpt.domain.record.bookmark.BookmarkBodyCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkResponse {

    private Long id;
    private LocalDate uploadDate;
    private String title;
    private ExerciseType exerciseType;
    private BodyPart bodyPart;
    private List<BodyPart> specificBodyParts;
    private int weight; // 무게(kg)
    private int exerciseSet; // 운동 set
    private int times; // 횟수
    private int exerciseTime;

    public static BookmarkResponse of(Bookmark bookmark) {
        BodyPart bodyPart = getBodyPart(bookmark);
        List<BodyPart> specificBodyParts = getSpecificBodyParts(bookmark);

        return BookmarkResponse.builder()
            .id(bookmark.getId())
            .uploadDate(bookmark.getUploadDate())
            .title(bookmark.getTitle())
            .exerciseType(bookmark.getExerciseType())
            .bodyPart(bodyPart)
            .specificBodyParts(specificBodyParts)
            .weight(bookmark.getWeight())
            .exerciseSet(bookmark.getExerciseSet())
            .times(bookmark.getTimes())
            .exerciseTime(bookmark.getExerciseTime())
            .build();
    }

    private static BodyPart getBodyPart(Bookmark bookmark) {
        return Optional.ofNullable(bookmark.getBodyCategory())
            .map(BookmarkBodyCategory::getName)
            .orElse(null);
    }

    private static List<BodyPart> getSpecificBodyParts(Bookmark bookmark) {
        return Optional.ofNullable(bookmark.getBodyCategory())
            .map(BookmarkBodyCategory::getChildren)
            .filter(children -> !children.isEmpty())
            .map(children -> children.stream()
                .map(BookmarkBodyCategory::getName)
                .collect(Collectors.toList()))
            .orElse(null);
    }

}
