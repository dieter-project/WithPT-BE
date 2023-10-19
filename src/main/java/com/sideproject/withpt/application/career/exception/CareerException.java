package com.sideproject.withpt.application.career.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class CareerException extends GlobalException {

    private final CareerErrorCode errorCode;

    public CareerException(CareerErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
