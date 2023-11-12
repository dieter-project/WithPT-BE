package com.sideproject.withpt.application.education.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class EducationException extends GlobalException {

    private final EducationErrorCode errorCode;

    public EducationException(EducationErrorCode errorCode){
        super(errorCode);
        this.errorCode = errorCode;
    }
}
