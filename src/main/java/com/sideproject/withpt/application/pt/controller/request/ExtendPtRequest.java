package com.sideproject.withpt.application.pt.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtendPtRequest {

    @Min(value = 1, message = "최소 1회 이상 등록해야 합니다.")
    private int ptCount;

    @YearMonthType
    private String reRegistrationDate;

    @Builder
    private ExtendPtRequest(int ptCount, String reRegistrationDate) {
        this.ptCount = ptCount;
        this.reRegistrationDate = reRegistrationDate;
    }

    public LocalDateTime getReRegistrationDate() {
        int[] registrationDate = Arrays.stream(this.reRegistrationDate.split("-")).mapToInt(Integer::parseInt).toArray();
        return LocalDateTime.of(registrationDate[0], registrationDate[1], 1, 0, 0, 0);
    }
}
