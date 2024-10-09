package com.sideproject.withpt.application.record.exercise.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class ExerciseException extends GlobalException {

    public static final ExerciseException EXERCISE_NOT_EXIST = new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_EXIST);
    public static final ExerciseException EXERCISE_INFO_NOT_EXIST = new ExerciseException(ExerciseErrorCode.EXERCISE_INFO_NOT_EXIST);
    public static final ExerciseException EXERCISE_NOT_BELONG_TO_MEMBER = new ExerciseException(ExerciseErrorCode.EXERCISE_NOT_BELONG_TO_MEMBER);

    private final ExerciseErrorCode errorCode;

    public ExerciseException(ExerciseErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
