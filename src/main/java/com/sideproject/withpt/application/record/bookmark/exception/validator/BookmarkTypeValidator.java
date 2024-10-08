package com.sideproject.withpt.application.record.bookmark.exception.validator;

import com.sideproject.withpt.application.record.bookmark.controller.request.BookmarkRequest;
import com.sideproject.withpt.common.type.ExerciseType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookmarkTypeValidator implements ConstraintValidator<ValidBookmarkType, BookmarkRequest> {

    @Override
    public boolean isValid(BookmarkRequest request, ConstraintValidatorContext context) {

        if (request.getExerciseType().equals(ExerciseType.AEROBIC)) {    // 유산소
            return request.getHour() > 0;
        } else if (request.getExerciseType().equals(ExerciseType.ANAEROBIC)) {    // 무산소
            return request.getWeight() > 0 && request.getExerciseSet() > 0 && request.getTimes() > 0;
        }

        return true;
    }

}
