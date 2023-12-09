package com.sideproject.withpt.application.pt.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExtendPtRequest {

    private int ptCount;

    @YearMonthType
    private String reRegistrationDate;

    public LocalDateTime getReRegistrationDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int[] registrationDate = Arrays.stream(this.reRegistrationDate.split("-")).mapToInt(Integer::parseInt).toArray();
        return LocalDateTime.of(registrationDate[0], registrationDate[1],
            currentDateTime.getDayOfMonth(),
            currentDateTime.getHour(),
            currentDateTime.getMinute(),
            currentDateTime.getSecond());
    }
}
