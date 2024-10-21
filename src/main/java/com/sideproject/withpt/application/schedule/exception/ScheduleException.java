package com.sideproject.withpt.application.schedule.exception;

import com.sideproject.withpt.common.exception.GlobalException;
import lombok.Getter;

@Getter
public class ScheduleException extends GlobalException {

    private final ScheduleErrorCode errorCode;

    public ScheduleException(ScheduleErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
