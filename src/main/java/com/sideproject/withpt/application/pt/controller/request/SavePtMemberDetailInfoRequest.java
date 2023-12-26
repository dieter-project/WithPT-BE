package com.sideproject.withpt.application.pt.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SavePtMemberDetailInfoRequest {

    private int ptCount;

    @YearMonthType
    private String firstRegistrationDate;
    private String note;

    public LocalDateTime getFirstRegistrationDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int[] registrationDate = Arrays.stream(firstRegistrationDate.split("-")).mapToInt(Integer::parseInt).toArray();
        return LocalDateTime.of(registrationDate[0], registrationDate[1],
            currentDateTime.getDayOfMonth(),
            currentDateTime.getHour(),
            currentDateTime.getMinute(),
            currentDateTime.getSecond());
    }
}
