package com.sideproject.withpt.application.academic.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class AcademicException extends GlobalException {

    private final AcademicErrorCode errorCode;

    public AcademicException(AcademicErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
