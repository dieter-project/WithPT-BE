package com.sideproject.withpt.application.record;

import com.sideproject.withpt.common.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.YearMonth;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/record")
public class RecordController {

    private final RecordService recordService;

    @GetMapping()
    public ApiSuccessResponse<Map<String, AllDatesRecordResponse>> getAllDatesRecord(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth date,
        @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {
        return ApiSuccessResponse.from(
            recordService.getAllDatesRecord(memberId, date.getYear(), date.getMonthValue())
        );
    }
}
