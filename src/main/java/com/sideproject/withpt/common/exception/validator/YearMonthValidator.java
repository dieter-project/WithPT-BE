package com.sideproject.withpt.common.exception.validator;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class YearMonthValidator implements ConstraintValidator<YearMonthType, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(!StringUtils.hasText(value)) {
            // 값이 비어있는 경우는 검증을 통과한다고 가정
            return false;
        }

        try {

            YearMonth.parse(value, FORMATTER);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
