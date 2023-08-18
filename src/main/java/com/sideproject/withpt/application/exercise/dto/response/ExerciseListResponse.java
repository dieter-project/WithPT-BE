package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseListResponse {

    private String title;
    private int weight;
    private int set;
    private int time;
    private BodyPart part;

}
