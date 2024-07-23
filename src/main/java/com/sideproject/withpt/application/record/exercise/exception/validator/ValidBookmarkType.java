package com.sideproject.withpt.application.record.exercise.exception.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;

@Target(TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookmarkTypeValidator.class)
public @interface ValidBookmarkType {

    String message() default "운동 유형에 따른 파라미터 값이 유효하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
