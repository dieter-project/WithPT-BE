package com.sideproject.withpt.application.member.controller.request;

import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditMemberExerciseFrequencyRequest {

    @ValidEnum(enumClass = ExerciseFrequency.class)
    private ExerciseFrequency exerciseFrequency;
}
