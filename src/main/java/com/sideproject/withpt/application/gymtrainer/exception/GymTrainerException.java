package com.sideproject.withpt.application.gymtrainer.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class GymTrainerException extends GlobalException {

    public static final GymTrainerException GYM_TRAINER_NOT_MAPPING = new GymTrainerException(GymTrainerErrorCode.GYM_TRAINER_NOT_MAPPING);

    private final GymTrainerErrorCode errorCode;

    public GymTrainerException(GymTrainerErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
