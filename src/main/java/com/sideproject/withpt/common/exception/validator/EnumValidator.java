package com.sideproject.withpt.common.exception.validator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private List<Object> enumValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        enumValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        return isNotNull(value) && isContainsValue(value);
    }

    private boolean isContainsValue(Enum<?> value) {
        return enumValues.contains(value.name());
    }

    private boolean isNotNull(Enum<?> value) {
        return value != null;
    }
}
