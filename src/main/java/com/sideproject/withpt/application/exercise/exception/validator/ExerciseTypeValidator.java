package com.sideproject.withpt.application.exercise.exception.validator;

import com.sideproject.withpt.application.exercise.dto.request.ExerciseRequest;
import com.sideproject.withpt.application.type.ExerciseType;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class ExerciseTypeValidator implements ConstraintValidator<ValidExerciseType, ExerciseRequest> {

    @Override
    public boolean isValid(ExerciseRequest request, ConstraintValidatorContext context) {

        if(request.getExerciseType().equals(ExerciseType.AEROBIC)) {    // 유산소
            return request.getHour() > 0;
        } else if(request.getExerciseType().equals(ExerciseType.ANAEROBIC)) {    // 무산소
            return request.getWeight() > 0 && request.getSet() > 0 && request.getTimes() > 0;
        }

        return true;
    }

}
