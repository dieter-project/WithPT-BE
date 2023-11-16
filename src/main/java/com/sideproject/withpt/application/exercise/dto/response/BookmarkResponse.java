package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.application.type.ExerciseType;
import com.sideproject.withpt.domain.record.Bookmark;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkResponse {

    private Long id;
    private String title;
    private int weight;
    private int set;
    private int times;
    private int hour;

    private BodyPart bodyPart;
    private ExerciseType exerciseType;

    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .title(bookmark.getTitle())
                .weight(bookmark.getWeight())
                .set(bookmark.getSet())
                .times(bookmark.getTimes())
                .hour(bookmark.getHour())
                .bodyPart(bookmark.getBodyPart())
                .exerciseType(bookmark.getExerciseType())
                .build();
    }

}
