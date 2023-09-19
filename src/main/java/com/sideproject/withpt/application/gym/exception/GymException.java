package com.sideproject.withpt.application.gym.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class GymException extends GlobalException {

    public static final GymException GYM_NOT_FOUND = new GymException(GymErrorCode.GYM_NOT_FOUND);
    public static final GymException GYM_TRAINER_NOT_MAPPING = new GymException(GymErrorCode.GYM_TRAINER_NOT_MAPPING);

    private final GymErrorCode errorCode;

    public GymException(GymErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
