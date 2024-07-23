package com.sideproject.withpt.application.record.diet.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class DietException extends GlobalException {

    public static final DietException DIET_NOT_EXIST = new DietException(DietErrorCode.DIET_NOT_EXIST);
    public static final DietException DIET_FOOD_NOT_EXIST = new DietException(DietErrorCode.DIET_FOOD_NOT_EXIST);
    public static final DietException DIET_NOT_BELONG_TO_MEMBER = new DietException(DietErrorCode.DIET_NOT_BELONG_TO_MEMBER);

    private final DietErrorCode errorCode;

    public DietException(DietErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
