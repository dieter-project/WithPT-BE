package com.sideproject.withpt.common.exception.validator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Slf4j
public class LocalDateValidator implements ConstraintValidator<LocalDateType, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return false;
        }

        try {
            log.info("날짜 검증 로직");
            LocalDate.parse(value, FORMATTER);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

}
