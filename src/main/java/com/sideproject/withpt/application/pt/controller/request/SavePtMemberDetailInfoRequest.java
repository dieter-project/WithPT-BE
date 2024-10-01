package com.sideproject.withpt.application.pt.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SavePtMemberDetailInfoRequest {

    @Min(value = 1, message = "최소 1회 이상 등록해야 합니다.")
    private final int ptCount;

    @YearMonthType
    private final String centerFirstRegistrationMonth;
    private final String note;

    @Builder
    private SavePtMemberDetailInfoRequest(int ptCount, String centerFirstRegistrationMonth, String note) {
        this.ptCount = ptCount;
        this.centerFirstRegistrationMonth = centerFirstRegistrationMonth;
        this.note = note;
    }

    public LocalDateTime getcenterFirstRegistrationMonth() {
        int[] registrationDate = Arrays.stream(centerFirstRegistrationMonth.split("-")).mapToInt(Integer::parseInt).toArray();
        return LocalDateTime.of(registrationDate[0], registrationDate[1], 1, 0, 0, 0);
    }
}
