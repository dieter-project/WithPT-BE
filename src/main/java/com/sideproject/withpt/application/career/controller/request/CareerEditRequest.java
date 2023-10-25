package com.sideproject.withpt.application.career.controller.request;

import com.sideproject.withpt.common.exception.validator.YearMonthType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CareerEditRequest {

    @NotNull(message = "경력 ID는 필수입니다.")
    private Long id;

    @NotBlank(message = "센터명을 입력해주세요")
    private String centerName;

    @NotBlank(message = "직책을 입력해주세요")
    private String jobPosition;

    @YearMonthType
    private String startOfWorkYearMonth;

    @YearMonthType
    private String endOfWorkYearMonth;

}
