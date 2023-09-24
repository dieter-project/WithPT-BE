package com.sideproject.withpt.application.weight.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class WeightException extends GlobalException {

    public static final WeightException WEIGHT_NOT_EXIST = new WeightException(WeightErrorCode.WEIGHT_NOT_EXIST);
    public static final WeightException WEIGHT_NOT_BELONG_TO_MEMBER = new WeightException(WeightErrorCode.WEIGHT_NOT_BELONG_TO_MEMBER);

    private final WeightErrorCode errorCode;

    public WeightException(WeightErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
