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
    private final String firstRegistrationDate;
    private final String note;

    @Builder
    private SavePtMemberDetailInfoRequest(int ptCount, String firstRegistrationDate, String note) {
        this.ptCount = ptCount;
        this.firstRegistrationDate = firstRegistrationDate;
        this.note = note;
    }

    public LocalDateTime getFirstRegistrationDate() {
        int[] registrationDate = Arrays.stream(firstRegistrationDate.split("-")).mapToInt(Integer::parseInt).toArray();
        return LocalDateTime.of(registrationDate[0], registrationDate[1], 1, 0, 0, 0);
    }
}
