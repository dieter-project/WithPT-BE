package com.sideproject.withpt.application.lesson.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class LessonException extends GlobalException {

    public static final LessonException ALREADY_RESERVATION = new LessonException(LessonErrorCode.ALREADY_RESERVATION);

    private final LessonErrorCode errorCode;

    public LessonException(LessonErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
