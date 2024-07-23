package com.sideproject.withpt.application.record.body.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class BodyException extends GlobalException {

    public static final BodyException BODY_NOT_EXIST = new BodyException(BodyErrorCode.BODY_NOT_EXIST);
    public static final BodyException BODY_NOT_BELONG_TO_MEMBER = new BodyException(BodyErrorCode.BODY_NOT_BELONG_TO_MEMBER);

    private final BodyErrorCode errorCode;

    public BodyException(BodyErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
