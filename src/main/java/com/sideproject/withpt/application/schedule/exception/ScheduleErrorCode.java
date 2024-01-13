package com.sideproject.withpt.application.schedule.exception;

import com.sideproject.withpt.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    IS_EXIST_SAME_WEEKDAY(HttpStatus.BAD_REQUEST, "동일한 요일이 존재하여 수정 및 저장이 불가능합니다."),
    WORK_SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 근무 시간이 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String message;

}
