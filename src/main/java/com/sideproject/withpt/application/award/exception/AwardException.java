package com.sideproject.withpt.application.award.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class AwardException extends GlobalException {

    private final AwardErrorCode errorCode;

    public AwardException(AwardErrorCode errorCode){
        super(errorCode);
        this.errorCode = errorCode;
    }
}
