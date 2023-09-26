package com.sideproject.withpt.application.body.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class BodyException extends GlobalException {

    public static final BodyException WEIGHT_NOT_EXIST = new BodyException(BodyErrorCode.WEIGHT_NOT_EXIST);
    public static final BodyException WEIGHT_NOT_BELONG_TO_MEMBER = new BodyException(BodyErrorCode.WEIGHT_NOT_BELONG_TO_MEMBER);

    private final BodyErrorCode errorCode;

    public BodyException(BodyErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
