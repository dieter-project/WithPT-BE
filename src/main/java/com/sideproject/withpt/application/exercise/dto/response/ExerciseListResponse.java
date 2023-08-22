package com.sideproject.withpt.application.exercise.dto.response;

import com.sideproject.withpt.application.type.BodyPart;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseListResponse {

    private String title;
    private int weight;
    private int set;
    private int time;

    @ValidEnum(enumClass = BodyPart.class)
    private BodyPart bodyPart;

    private List<MultipartFile> images;

}
